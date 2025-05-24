# System Patterns

**System Architecture:**

The system follows a microservices architecture with the following components:

*   **Webhook Controller:** Receives SMS/WhatsApp messages from Twilio.
*   **Weather Service:** Fetches weather data from a weather API.
*   **LLM Service:** Summarizes weather data using the Gemini LLM API.
*   **SMS/WhatsApp Service:** Sends the summary back to the user via Twilio.

**Design Patterns:**

*   **API Gateway:** The Webhook Controller acts as an API gateway, routing requests to the appropriate services.
*   **Strategy Pattern:** The Weather Service can use different strategies for fetching weather data from different APIs.
*   **Template Method Pattern:** The LLM Service can use a template method to prepare the prompt for the LLM API.

**Component Relationships:**

1.  User sends SMS/WhatsApp message to Twilio.
2.  Twilio sends the message to the Webhook Controller.
3.  The Webhook Controller extracts the PIN code and sends it to the Weather Service.
4.  The Weather Service fetches weather data from the weather API.
5.  The Weather Service sends the weather data to the LLM Service.
6.  The LLM Service summarizes the weather data in Hindi and English.
7.  The LLM Service sends the summary to the SMS/WhatsApp Service.
8.  The SMS/WhatsApp Service sends the summary back to the user via Twilio.
