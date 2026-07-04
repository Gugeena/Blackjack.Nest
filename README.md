# 🥊 Blackjack.Nest

> **A UFC fantasy prediction platform built with Java Spring Boot.**

Blackjack.Nest is a web application where MMA fans can predict the outcome of upcoming UFC fights using a virtual currency called **Medals**. Users compete against their own prediction history by wagering Medals on fight outcomes and tracking their long-term accuracy.

Unlike traditional betting platforms, **Blackjack.Nest does not involve real money or gambling.** Medals exist solely as an in-app scoring system.

> *"Perfection is not attainable, but if we chase perfection, we can catch excellence."*
> **— Georges St-Pierre**

---

## ✨ Features

### 🔐 Authentication

* User Registration
* Secure Login
* Email Two-Factor Authentication (2FA)
* Session-based Authentication with Spring Security

### 🥊 Fight Predictions

* Predict the winner of upcoming UFC fights
* Select the method of victory
* Edit predictions before the event begins
* Automatic synchronization with the **Cito API**
* Official fight results are automatically updated once available

###  🎟️ Parlay System

* A parlay can only contain predictions from the **same UFC event**.
* Users place **one shared wager** on the entire parlay that is total of all picks inside the parlay.
* The potential payout is **4× the total amount of Medals wagered**.
* A parlay is successful **only if every prediction in the parlay is correct**.
* If even one prediction is incorrect, the entire parlay loses.

### 🪙 Medal System

* Bet your virtual **Medals** on predictions
* ✅ Correct prediction → Earn **2×** your wager
* ❌ Incorrect prediction → Lose your wager
* No real money involved

### 📊 Statistics

Each user has access to:

* Prediction History
* Total Medals
* Prediction Accuracy (%)

---

# 🛠️ Tech Stack

## Backend

* ☕ Java
* 🍃 Spring Boot
* 🔒 Spring Security
* 📦 Spring Data JPA
* 🗄 Hibernate
* ✉ Spring Boot Mail
* 🌿 Lombok
* 🍃 Thymeleaf
* 🌐 Cito API

## Frontend

* HTML
* CSS
* JavaScript
* Thymeleaf

## Database

* PostgreSQL

---

# 🚀 Getting Started

## 1. Clone the Repository

```bash
git clone https://github.com/Gugeena/Blackjack.Nest.git
```

## 2. Open the project

Open the project using **IntelliJ IDEA**.

## 3. Configure Environment Variables

Create a `.env` file using `.env.example`.

Fill in:

* PostgreSQL credentials
* Email credentials
* Cito API credentials

## 4. Configure PostgreSQL

Create your PostgreSQL database and update the connection settings.

## 5. Run the Application

Start the Spring Boot application.

The website will be available at:

```
http://localhost:8080
```

---

# 👤 User Accounts

No pre-created test accounts are provided.

Users can register directly inside the application.

Email verification (2FA via Java Mail) is recommended but optional
Registration works with standard user credentials

---

# 🔄 How Fight Updates Work

Upcoming fights are automatically retrieved from the **Cito API**.

Once official UFC results become available, Blackjack.Nest synchronizes the results automatically, allowing predictions and Medal balances to be updated without manual intervention.

---

# 📂 Project Structure

```text
src
├── main
│   ├── java
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── repository
│   │   ├── security
│   │   └── service
│   └── resources
│       ├── static
│       ├── templates
│       └── application.properties
```

---

# 📈 Future Improvements

* 🏆 Global Leaderboards
* 👤 User Profile Customization
* 📊 Advanced Statistics
* 🥇 Achievement System
* 📱 Responsive Mobile UI
* 📅 Prediction Reminders

---

# 📸 Screenshots

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/2cafc211-002f-4367-bedd-f67dbf95ed18" />
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/f22a548b-d1a8-4107-8ebf-4cf912ab828a" />
---

# 📜 License

This project was created for educational and portfolio purposes.

---

# 👨‍💻 Author

**ლაშა-გიორგი გუგენიშვილი**

Solo Project • Java Spring Boot • UFC Fantasy Prediction Platform

