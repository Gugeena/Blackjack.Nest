async function register(event)
{
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const email = document.getElementById("email").value;
    const error = document.getElementById("error");

    const regex = /^[A-Za-z0-9]+$/;

    if(username.length < 4 || password.length < 4)
    {
        error.textContent = "Username And Password Must Be Over 4 Characters";
        return;
    }

    if(!regex.test(username))
    {
        error.textContent = "Username Must Only Contain Letters And Characters";
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

            const data = await response.json();

          if(!response.ok)
          {
            error.textContent = data.error;
          }
          else
          {
             window.location.href = data.redirect;
          }
    }
    catch(err)
    {
       error.textContent = "Server Down"; return
    }
}

async function login(event)
{
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const error = document.getElementById("error");

    if(username.length < 4 || password.length < 4)
    {
        console.log("aint passing");
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
          error.textContent = data.error;
       }
       else
       {
          window.location.href = "/" + data.redirect;
       }
    }
    catch(err)
    {
       error.textContent = "Server Down"; return
    }
}