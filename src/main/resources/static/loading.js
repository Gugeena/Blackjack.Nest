window.addEventListener('beforeunload', () =>
{
    document.body.classList.add('loading');
})

window.addEventListener('pageshow', (event) =>
{
    const elements = Array.from(document.querySelectorAll('.loader'));
    const targetElement = elements.find(el => el.textContent === "Loading...");
    if(targetElement != null)
    {
        targetElement.textContent = targetElement.value;
    }

    const myPicks = document.getElementById("myPicks");
    if(myPicks != null)
    {
        const label = myPicks.querySelector("h1");
        label.textContent = "My Picks";
    }
    document.body.classList.remove('loading');
})

document.querySelectorAll(".loader").forEach(el =>
{
    el.addEventListener('click', (event) => {

        if(el.id != "card-btn");
        {
           let loader = event.target;
           let content = "Loading..."
           if(el.id === "myPicks")
           {
              loader = event.target.querySelector("h1");
           }
           else if(el.id === "parlayCreator")
           {
              content = "Creating...";
           }
           loader.textContent = content;
        }

        document.body.classList.add('loading');
    });
})