console.log("JS LOADED");

document.querySelectorAll(".method-buttons").forEach(group =>
{
    const buttons = group.querySelectorAll(".method-btn");

    buttons.forEach(button => {
        button.addEventListener("click", () =>
        {
            console.log("Clicked!" + button.textContent);
            buttons.forEach(b => b.classList.remove("selected"));
            button.classList.add("selected");
        });
    });
});

document.querySelectorAll(".bet-buttons").forEach(group =>
{
    const buttons = group.querySelectorAll(".fighter-btn");

    buttons.forEach(button => {
        button.addEventListener("click", () =>
        {
            console.log("Clicked!" + button.textContent);
            buttons.forEach(b => b.classList.remove("selected"));
            button.classList.add("selected");
        });
    });
});

async function submit(event)
{
    const currCard = event.target.closest(".card");
    const method = currCard.querySelector(".method-buttons .selected").textContent;
    const winner = currCard.querySelector(".bet-buttons .selected").textContent;
    let amount = currCard.querySelector(".betAmount").value;
    const eventSlug = currCard.querySelector(".eventSlug").value;
    const loser = currCard.querySelector(".fighter-btn:not(.selected)").textContent;
    const boutId = event.target.value;
    const error = currCard.querySelector(".error");

    if(amount.trim() === "")
    {
        error.textContent = "Input Bet Amount";

        clear(currCard);

        return;
    }

    const formData = new FormData();

    formData.append("winner", winner);
    formData.append("method", method);
    formData.append("boutId", boutId);
    formData.append("amount", amount);
    formData.append("eventSlug", eventSlug);
    formData.append("loser", loser);

    event.preventDefault();

    const response = await fetch('http://localhost:8080/pick',
    {
        method: 'POST',
        body: formData
    })
    .catch(() => {
        error.textContent = "Server Down";
        return;
    })

    const data = await response.json();

    if(!response.ok)
    {
      error.classList.add("deselected");

      if(data.error === "insufficient funds")
      {
         error.textContent = "Insufficient funds";
      }
      else error.textContent = "Error Has Occurred";

      clear(currCard);

      return;
    }

    error.classList.remove("deselected");
    error.textContent = "Pick Has Been Recorded";

    clear(currCard);

    const displayedPick = currCard.querySelector(".chosen");
    displayedPick.textContent = "Current Pick: " + winner + " By " + method;

    currCard.querySelector(".betAmount").value = 0;
}

function clear(currCard)
{
    const pickDiv = currCard.querySelector(".bet-buttons");
    const methodDiv = currCard.querySelector(".method-buttons");

    const pick = pickDiv.querySelector(".selected");
    pick.classList.remove("selected");
    const methodPick = methodDiv.querySelector(".selected");
    methodPick.classList.remove("selected");
}