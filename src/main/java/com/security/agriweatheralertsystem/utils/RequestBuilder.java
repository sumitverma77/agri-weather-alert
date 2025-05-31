package com.security.agriweatheralertsystem.utils;

public class RequestBuilder {
    public static String geminiRequestBuilder(String prompt) {
        String escapedPrompt = prompt.replace("\"", "\\\"");
        return """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(escapedPrompt);
    }
}
