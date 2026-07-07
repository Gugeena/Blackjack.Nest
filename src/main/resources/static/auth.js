async function register(event)
{
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const email = document.getElementById("email").value;
    const error = document.getElementById("error");

    const loader = document.getElementById("loader");

    const regex = /^[A-Za-z0-9]+$/;

    if(username.length < 4 || password.length < 4)
    {
        removeLoading("Register", loader, event);
        error.textContent = "Username And Password Must Be Over 4 Characters";
        return;
    }

    if(!regex.test(username))
    {
        removeLoading("Register", loader, event);
        error.textContent = "Username Must Only Contain Letters And Numbers";
        return;
    }

    const formData = new FormData();

    formData.append("username", username);
    formData.append("password", password);

    if(email != null && email.length > 0)
    {
        formData.append("email", email);
    }

    try
    {
          const response = await fetch('http://localhost:8080/register', {
                method: 'POST',
                body: formData
            });

          console.log("abou tto unwrap");
          const data = await response.json();
          console.log("unwraped");
          if(!response.ok)
          {
             removeLoading("Register", loader, event);

            error.textContent = data.error;
          }
          else
          {
             document.body.style.pointerEvents = 'none';
             window.location.href = data.redirect;
          }
    }
    catch(err)
    {
       removeLoading("Register", loader, event);
       error.textContent = "Server Down"; return
    }
}

async function login(event)
{
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const error = document.getElementById("error");

    const loader = document.getElementById("loader");

    if(username.length < 4 || password.length < 4)
    {
        removeLoading("Login", loader, event);
        error.textContent = "Invalid Username Or Password";
        return;
    }

    const formData = new FormData();

    formData.append("username", username);
    formData.append("password", password);

    try
    {
       const response = await fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({ username, password })
       });

       const data = await response.json();

       if(!response.ok)
       {
          removeLoading("Login", loader, event);
          error.textContent = data.error;
       }
       else
       {
          window.location.href = "/" + data.redirect;
       }
    }
    catch(err)
    {
       if(loader != null) removeLoading("Login", loader, event);
       error.textContent = "Server Down";
       return;
    }
}

function removeLoading(text, loader, event)
{
    document.body.classList.remove('loading');
    loader.textContent = text;
    event.stopImmediatePropagation();
}