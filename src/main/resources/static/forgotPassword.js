let sent = false;

async function sendCode(event)
{
    event.preventDefault();

    const username = document.getElementById("username").value;
    const informationLabel = document.getElementById("information");

    const button = event.target;

    if(username.trim().length === 0)
    {
        informationLabel.textContent = "Input Username"
        button.textContent = "Send Code";
        document.body.classList.remove('loading');
        event.stopImmediatePropagation();
        return;
    }

    button.disabled = true;

    const response = await fetch('http://localhost:8080/forgotPassword',
    {
       method: 'POST',
       headers: { "Content-Type": "application/x-www-form-urlencoded" },
       body: new URLSearchParams({ username })
    })
    .catch(() =>
    {
       sent = false;
       information.textContent = "Error";
       button.disabled = false;
       button.textContent = "Send Code";
       return;
    })

    let data;
    try
    {
       data = await response.json();
    }
    catch
    {
       data = null;
    }

    document.body.classList.remove('loading');
    button.textContent = "Send Code";

    if(!response.ok)
    {
       information.textContent = data.error;
       button.disabled = false;
       button.textContent = "Send Code";
       return;
    }

    information.textContent = "Check Your Email";
    sent = true;
}

async function verifyCode(event)
{
    event.preventDefault();

    const informationLabel = document.getElementById("information");

    const button = event.target;

    if(!sent)
    {
       informationLabel.textContent = "Send The Code First"
       button.textContent = "Verify";
       document.body.classList.remove('loading');
       event.stopImmediatePropagation();
       return;
    }

    const code = document.getElementById("code").value;

    const response = await fetch('http://localhost:8080/checkCode',
       {
          method: 'POST',
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: new URLSearchParams({ code })
       })
       .catch(() =>
       {
          button.textContent = "Verify";
          document.body.classList.remove('loading');
          window.href.location = "/error";
          return;
       })

       let data;
       try
       {
          data = await response.json();
       }
       catch
       {
          data = null;
       }

       document.body.classList.remove('loading');
       button.textContent = "Verify";
       informationLabel.textContent = "";

       if(!response.ok)
       {
          informationLabel.textContent = data.error;
          return;
       }

       if(data.checking)
       {
          document.querySelector(".box-container.hiddenDiv").style.display = "block";
       }
}

async function updatePassword(event)
{
     event.preventDefault();

     const password = document.getElementById("newPassword").value;
     const username = document.getElementById("username").value;
     const error = document.getElementById("error");

     if(password.length < 4 || username.length < 4)
     {
         document.body.classList.remove('loading');
         event.target.textContent = "Change Password";
         error.textContent = "Username And Password Must Be Valid And Over 4 Characters";
         event.stopImmediatePropagation();
         return;
     }

      const response = await fetch('http://localhost:8080/forgotPassword',
      {
         method: 'PUT',
         headers: { "Content-Type": "application/x-www-form-urlencoded" },
         body: new URLSearchParams({ password, username })
      })
      .catch(() =>
      {
         event.target.textContent = "Change Password";
         document.body.classList.remove('loading');
         window.href.location = "/error";
         return;
      })

      if(!response.ok)
      {
          informationLabel.textContent = data.error;
          return;
      }

      let data;
      try
      {
         data = await response.json();
      }
      catch
      {
         data = null;
      }

      event.target.textContent = "Change Password";
      document.body.classList.remove('loading');

      if(!response.ok)
      {
         error.textContent = data.error;
         return;
      }

      window.location.href = "/login";
}