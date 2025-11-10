# Validation

This project demonstrates the use of Azure OpenAI chat models in a Spring Boot application to validate model responses in a strict JSON format.

## Features
- Integration with Azure OpenAI for chat-based tasks
- Configurable prompt template and city via `application.yml`
- Example of validation of model responses in strict JSON format

## Configuration
Edit `src/main/resources/application.yml` to set your Azure OpenAI endpoint, API key, deployment name, and target city.

```
spring:
  ai:
    model:
      chat: azure-openai
    azure:
      openai:
        endpoint: https://<resource>.openai.azure.com/
        api-key: YOUR_AZURE_OPENAI_API_KEY
        chat:
          options:
            deployment-name: gpt-5-mini
            temperature: 1.0
app:
  prompt:
    template: |
      You are a service that emits ONLY strict JSON in the exact format below.
      No code fences, no extra keys, no explanations.
      Format:
      {format}
      Task:
      Produce a weather report for city "{city}".
    city: Prague
```

## Usage
1. Set up your Azure OpenAI resource and obtain an API key.
2. Update the configuration in `application.yml`.
3. Build and run the project:
   ```zsh
   ./gradlew build && ./gradlew bootRun
   ```
4. The application will generate a weather report for the configured city.

## Requirements
- Java 17 or higher
- Gradle
- Azure OpenAI resource


