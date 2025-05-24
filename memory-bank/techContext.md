# Tech Context

**Technologies Used:**

*   **Spring Boot:** Java-based framework for building the application.
*   **Twilio:** For sending and receiving SMS/WhatsApp messages.
*   **Google Cloud AI Gemini:** For summarizing weather data in Hindi and English.
*   **Weather API:** For fetching weather data based on the PIN code.
*   **Maven:** For dependency management.
*   **SLF4J:** For logging.

**Development Setup:**

*   Java Development Kit (JDK) 17 or higher.
*   Maven 3.6 or higher.
*   An IDE such as IntelliJ IDEA or Eclipse.
*   A Twilio account.
*   A Google Cloud AI Gemini account.
*   An account with a weather API provider.

**Technical Constraints:**

*   API rate limits for Twilio, Gemini, and the weather API.
*   The need to handle different weather API response formats.
*   Ensuring the security of API keys and credentials.

**Dependencies:**

*   `twilio-java`
*   `google-cloud-ai-generative-ai`
*   `spring-boot-starter-web`
*   `spring-boot-starter-logging`
