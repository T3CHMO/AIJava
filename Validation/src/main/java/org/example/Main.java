package org.example;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class Main implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final ChatModel chatModel;

    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deployment;

    @Value("${app.prompt.template}")
    private String promptTemplate;

    @Value("${app.prompt.city}")
    private String cityName;

    public Main(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public void run(String... args) {
        // 1) Prepare a BeanOutputConverter (generates JSON schema/format from our POJO)
        var converter = new BeanOutputConverter<>(WeatherReport.class);
        String format = converter.getFormat();

        // 2) Create prompt using template and variables from configuration
        var template = new PromptTemplate(promptTemplate);

        Map<String, Object> vars = Map.of(
                "format", format,
                "city", cityName
        );

        Prompt prompt = template.create(vars);

        // 3) Call Azure OpenAI via Spring AI's ChatModel
        ChatResponse response = chatModel.call(prompt);
        String raw = response.getResult().getOutput().getText();
        logger.info("Raw model JSON:\n{}", raw);

        // 4) Convert JSON -> POJO using the same converter
        WeatherReport report = converter.convert(raw);

        // 5) Validate with Bean Validation
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(report);

            if (!violations.isEmpty()) {
                logger.error("Validation failed:");
                for (ConstraintViolation<WeatherReport> v : violations) {
                    logger.error("- {}: {} (invalid value: {})",
                            v.getPropertyPath(), v.getMessage(), v.getInvalidValue());
                }
                System.exit(3);
            }
        }

        // 6) Success
        logger.info("Validated WeatherReport:");
        logger.info("{}", report);
        logger.info("(Using deployment: {} at {})", deployment, endpoint);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

