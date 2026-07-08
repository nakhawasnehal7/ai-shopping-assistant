package com.example.shoppingmcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class ShoppingMcpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingMcpClientApplication.class, args);
    }

    /**
     * Interactive console chat loop. The LLM (via Ollama) decides on its own
     * which MCP tools to call based on what you type — nothing here manually
     * routes to search_products, place_order, etc.
     */
    @Bean
    CommandLineRunner chatLoop(ChatClient chatClient) {
        return args -> {
            Scanner scanner = new Scanner(System.in);

            System.out.println("========================================");
            System.out.println("Shopping assistant ready. Type a request, or 'exit' to quit.");
            System.out.println("Examples:");
            System.out.println("  - Do you have any wireless headphones?");
            System.out.println("  - Order 2 of the mechanical keyboard");
            System.out.println("  - What's the status of order ORD-1?");
            System.out.println("========================================");

            while (true) {
                System.out.print("\nYou: ");
                String input = scanner.nextLine();

                if (input == null || input.isBlank()) {
                    continue;
                }
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    System.out.println("Goodbye!");
                    break;
                }

                try {
                    String response = chatClient.prompt(input).call().content();
                    System.out.println("Assistant: " + response);
                } catch (Exception e) {
                    System.out.println("Error talking to the model or a tool: " + e.getMessage());
                }
            }

            scanner.close();
        };
    }
}
