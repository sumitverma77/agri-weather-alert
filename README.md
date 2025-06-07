# 🌾☁️ AgriWeather Alert System

## 📌 Project Description

### 🎯 Problem Statement

Many farmers, especially in rural areas, are not comfortable using smartphones or typing queries online. However, they
can read messages and make phone calls. They often rely on rumors or second-hand weather advice, which sometimes leads
to uninformed crop decisions.

---

### 📊 _According to the _Down To Earth: State of India’s Environment in Figures 2020_ report:_

#### 🌾 More than 50% of marginal farmers reported losing crops due to unseasonal rains, floods, or heatwaves

#### 📡 **There’s a gap in weather communication**, especially for farmers without smartphones or internet access.

### 🔍 Evidence from Report

![img.png](docs/images/report.png)
![img_1.png](docs/images/report2.png)

### ✅ Solution Overview

AgriWeather Alert System automates the delivery of weather updates via SMS, WhatsApp, and voice calls in Hindi and
English, requiring no technical skills or interactions from the farmers. It runs scheduled weather checks twice a day
and sends easy-to-understand messages or provides weather information through voice calls.

### 🌾 Key Benefits

1. **No smartphone or internet required for voice calls** — farmers can simply dial a number from any basic phone and
   speak their city name to get weather updates in their language.

2. **Fully supports non-tech-savvy users** — especially useful for farmers who cannot operate apps or browse the
   internet.
3. **Multiple channels available:**

    * 📞 **Voice Call (no internet or smartphone needed)**
    * 📩**SMS & WhatsApp (requires a phone with basic or smart messaging capability)**

4. **Language preference** — farmers choose Hindi or English at the start of the call, and all updates follow in that
   language.

5. **Natural interaction** — speak the city name in your voice; no need to type or navigate menus.

6. **Daily updates automatically** — once a location is set as primary, updates are sent every day without having to
   call
   again.

7. Saves time and effort — no need to wait for newspapers or depend on others for weather info.

8. Accurate and reliable — based on real-time weather data and summarized in a farmer-friendly format.

---

## 🛠️ Setup Instructions

1. Install **Java JDK 17** or higher.
2. Install **Maven 3.6** or higher.
3. Create accounts for the following services:
    * [Twilio](https://www.twilio.com/)
    * [Google Cloud AI Gemini](https://aistudio.google.com/)
    * Weather API provider (e.g., OpenWeatherMap)
4. Configure credentials in `src/main/resources/application.properties`:

   ```properties
   twilio.account.sid=...
   twilio.auth.token=...
   twilio.whatsapp.number=...
   gemini.api.key=...
   weather.api.key=...
   spring.datasource.url=...
   spring.datasource.username=...
   spring.datasource.password=...
   ```
5. Build the application:

   ```bash
   mvn clean install
   ```
6. Run the application:

   ```bash
   mvn spring-boot:run
   ```

> By default, the application runs on **http://localhost:8080**

---

## ▶️ Run the Project Locally

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/agriweather-alert.git
cd agriweather-alert
```

### 2. Start the Application

```bash
mvn spring-boot:run
```

---

## 🌐 Expose Localhost using Ngrok

### 1. Install [Ngrok](https://ngrok.com/download)

### 2. Start a tunnel:

```bash
ngrok http 8080
```

Copy the HTTPS URL from the terminal output (e.g., `https://abcd1234.ngrok.io`).

---

## 💬 Configure Twilio Sandbox

### 1. Log in to the [Twilio Console](https://www.twilio.com/console).

### 2. Go to **Messaging > Try it Out > WhatsApp Sandbox**.

### 3. Set the **Webhook URL** to:

```
https://your-ngrok-url.ngrok.io/api/webhook
```

### 4. Join the sandbox by sending the join code (e.g., `join brave-owl`) to the provided WhatsApp number.

![Twilio Sandbox Config](docs/images/sandbox-config.png)

---

## 📞 Voice Call Setup

1. In your Twilio account, navigate to **Phone Numbers > Manage > Active numbers** and select your Twilio phone number.
2. In the **Voice & Fax** section, configure the **A Call Comes In** setting to use a **Webhook**.
3. Set the **Webhook URL** to your Ngrok URL with the `/api/voice` endpoint (
   e.g., `https://your-ngrok-url.ngrok.io/api/voice`).
4. Make sure the HTTP method is set to **POST**.

---

## 🕒 Scheduling

To automatically send alerts twice a day, configure a cron job or use Spring’s `@Scheduled` annotation in your service
class.

Example:

```java

@Scheduled(cron = "0 0 5,17 * * *") // 5:00 AM and 5:00 PM daily
public void sendWeatherAlerts() {
    // alert logic here
}
```

---

## 📱 User View (WhatsApp)

### 1. Update Location (via WhatsApp or Call)

![img_1.png](docs/images/img_1.png)

### 2. Language Friendly

* #### In english

![img_5.png](docs/images/img_5.png)

* ##### In Hindi

![img_2.png](docs/images/img_2.png)

### 3. Automatically Scheduled Alerts

Alerts sent at set time daily.
![img_3.png](docs/images/img_3.png)
---

## 📞 User View (Voice Call)

### 1. Call the Given Number

![img.png](docs/images/img10.png)

### 2. Select the language choice

* Listen to the call instructions (e.g., press 1 for Hindi, 2 for English).
* After selecting the language, all further communication will be in that language.

### 3. Get Weather Updates

* Speak the city name for which you want weather updates.
* Listen to the weather updates.

### 4. Set Primary Location

* press the instructed key to if you want to set this city as your primary location.
* Once set, you will receive daily weather updates via SMS/WhatsApp for that location.

---

## 📚 Memory Bank

The `memory-bank/` folder contains key documentation files:

* `projectbrief.md` – Summary of the project purpose
* `productContext.md` – Context of use
* `systemPatterns.md` – Architecture patterns
* `techContext.md` – Technical stack overview
* `activeContext.md` – Current working modules (Voice Call Feature)
* `progress.md` – Development progress and milestones (Voice Call Feature Implemented)

---

## 📩 Contact

If you have any contributions, questions, or concerns, please open an issue or reach out to me
on [LinkedIn](https://www.linkedin.com/in/sumit-verma-/).

---

## 📝 License

This project is licensed under the [MIT License](LICENSE).

---
