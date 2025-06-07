# System Patterns

**System Architecture:**

The system follows a microservices architecture with the following components:

*   **Webhook Controller:** Receives SMS/WhatsApp messages from Twilio (for registration and updates).
*   **Voice Controller:** Handles incoming voice calls from Twilio.
*   **Weather Service:** Fetches weather data from a weather API.
*   **LLM Service:** Summarizes weather data using the Gemini LLM API.
*   **SMS/WhatsApp Service:** Sends the summary back to the user via SMS/WhatsApp.
*   **Voice Service:** Handles voice call logic and interacts with the Weather and LLM services.
*   **Weather Alert Scheduler:** Sends weather alerts automatically on a scheduled basis.

**Design Patterns:**

*   **API Gateway:** The Webhook Controller and Voice Controller act as API gateways, routing requests to the appropriate services.
*   **Strategy Pattern:** The Weather Service can use different strategies for fetching weather data from different APIs.
*   **Template Method Pattern:** The LLM Service can use a template method to prepare the prompt for the LLM API.

**Component Relationships:**

1.  User sends SMS/WhatsApp message to Twilio (for registration and updates) or initiates a voice call.
2.  Twilio sends the message to the Webhook Controller or the voice call to the Voice Controller.
3.  The Webhook Controller extracts the location and registers/updates the user.
4.  The Voice Controller receives the voice input (location) and passes it to the Voice Service.
5.  The Voice Service processes the location and fetches weather information.
6.  The Weather Alert Scheduler triggers the weather alert process.
7.  The Weather Service fetches weather data from the weather API.
8.  The Weather Service sends the weather data to the LLM Service.
9.  The LLM Service summarizes the weather data in Hindi and English.
10. The LLM Service sends the summary to the SMS/WhatsApp Service or the Voice Service.
11. The SMS/WhatsApp Service sends the summary back to the user via Twilio.
12. The Voice Service sends the summary back to the user via voice call.
