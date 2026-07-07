let betAmount = 0;

function select(event)
{
    const button = event.target;
    const selected = document.querySelectorAll('.selected');

    const container = button.closest(".pick-container");
    const error = container.querySelector(".error");
    const betAmountLabel = container.querySelector('.bet');

    let conflict = false;

    let selectedLength = selected.length;

    const containers = document.body.querySelectorAll(".pick-container");
    if(containers.length !== 0)
    {
         containers.forEach(cont =>
            {
                const contsButton = cont.querySelector('.parlay-start');
                if(contsButton != null && contsButton.value != button.value)
                {
                    contsButton.disabled = true;
                }
            })
    }

/*
    if(selectedLength !== 0)
    {
        selected.forEach(item =>
        {
            if(item.value != button.value)
            {
                conflict = true;
                return;
            }
        });
    }

    if(conflict)
    {
        error.textContent = 'Same-Event Parley';
        return;
    }


    error.textContent = '';
*/

    const parlayDisplay = document.getElementById('parlayDisplay');
    let multiplier = 4;

    const changeText = betAmountLabel.textContent.slice(12);
    const change = Number(changeText);

    if(button.classList.contains('selected'))
    {
        if(selected.length === 1)
        {
            const elements = document.body.querySelectorAll("span");
            elements.forEach(el =>
            {
                if(el.textContent === 'Same-Event Parley')
                {
                    el.textContent = '';
                }
            })
        }
        selectedLength = selectedLength - 1;
        button.classList.remove('selected');
        betAmount = betAmount - change;
    }
    else
    {
        selectedLength = selectedLength + 1;
        button.classList.add('selected');
        betAmount = betAmount + change;
    }

    parlayDisplay.textContent = 'Selected: ' + (selectedLength) + ' ' + ' Payout: ' + (betAmount * 4);

    if(selectedLength > 1)
    {
        const parlayCreator = document.getElementById("parlayCreator");
        parlayCreator.disabled = false;
    }
    else
    {
        parlayCreator.disabled = true;
        if(selectedLength === 0)
        {
             containers.forEach(cont =>
             {
                    const contsButton1 = cont.querySelector('.parlay-start');
                    const autoDisabled = contsButton1.getAttribute("autoDisabled") === "true";
                    if(contsButton1.value != button.value && !autoDisabled)
                    {
                        contsButton1.disabled = false;
                    }
             })
        }
    }

}

async function buildParlay(event)
{
    event.preventDefault();

    const nowSelected = document.querySelectorAll('.selected');
    const pickIds = Array.from(nowSelected).map(el => el.id);

    const formData = new FormData();

    pickIds.forEach(id =>
    {
        formData.append("pickIds", id);
    })

    const response = await fetch('http://localhost:8080/createParlay',
        {
            method: 'POST',
            body: formData
        })
        .catch(() =>
        {
            document.body.classList.remove('loading');
            event.stopImmediatePropagation();
            event.target.textContent = "Create Parlay"
            return;
        })
    const data = await response.json();

    if(response.ok)
    {
        window.location.href = "/" + data.redirect;
    }
}