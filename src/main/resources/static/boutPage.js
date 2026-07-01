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
    event.preventDefault();

    const button = event.target;

    const currCard = event.target.closest(".card");
    const methodBtn = currCard.querySelector(".method-buttons .selected");
    const winnerBtn = currCard.querySelector(".bet-buttons .selected");
    const error = currCard.querySelector(".error");
    if(methodBtn === null || winnerBtn === null)
    {
         error.textContent = "Place A Pick";
         document.body.classList.remove('loading');
         event.stopImmediatePropagation();
         button.textContent = "Bet"
         return;
    }

    const method = methodBtn.textContent;
    const winner = winnerBtn.textContent;
    let amount = currCard.querySelector(".betAmount").value;
    const eventSlug = currCard.querySelector(".eventSlug").value;
    const loser = currCard.querySelector(".fighter-btn:not(.selected)").textContent;
    const boutId = event.target.value;

    if(amount.trim() === "" || amount.trim() === "0")
    {
        error.textContent = "Input Bet Amount";
        event.stopImmediatePropagation();
        clear(currCard);
        document.body.classList.remove('loading');
        button.textContent = "Bet"
        return;
    }

    const formData = new FormData();

    formData.append("winner", winner);
    formData.append("method", method);
    formData.append("boutId", boutId);
    formData.append("amount", amount);
    formData.append("eventSlug", eventSlug);
    formData.append("loser", loser);

    const response = await fetch('http://localhost:8080/pick',
    {
        method: 'POST',
        body: formData
    })
    .catch(() => {
        error.textContent = "Server Down";
        document.body.classList.remove('loading');
        button.textContent = "Bet"
        event.stopImmediatePropagation();
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

      document.body.classList.remove('loading');
      button.textContent = "Bet"
      event.stopImmediatePropagation();
      clear(currCard);

      return;
    }

    error.classList.remove("deselected");
    error.textContent = "Pick Has Been Recorded";

    document.body.classList.remove('loading');
    button.textContent = "Bet"

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