package com.example.demo;

import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.ShellTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.Scanner;

/**
 * @author journey
 */
@SpringBootApplication
public class Demo1Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(DeepSeekChatModel deepSeekChatModel, ChatMemory chatMemory,
                                        //SessionMemoryAdvisor sessionMemoryAdvisor,
                                        SessionMemoryAdvisor sessionMemorySummarizationAdvisor, ToolService toolService, ToolCallbackProvider toolCallbackProvider) {

        ChatClient chatClient = ChatClient.builder(deepSeekChatModel)
                .defaultAdvisors(//MessageChatMemoryAdvisor.builder(chatMemory).build()
                        //sessionMemoryAdvisor,
                        sessionMemorySummarizationAdvisor, SimpleLoggerAdvisor.builder().build())
                .defaultTools(FileSystemTools.builder().build(), ShellTools.builder().build())
                .defaultTools(toolCallbackProvider).build();
        return args -> {
            // Start the chat loop
            System.out.println("\n我是Journey-claude:.\n");


            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("\n> 你: ");
                    String userMessage = scanner.nextLine();
						/*String content = chatClient.prompt().user(userMessage).call().content();
						System.out.println("\n> AI: " + content);*/
                    System.out.println("\n> AI: ");
                    Flux<String> content = chatClient.prompt().user(userMessage)
                            //.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "Journey")).stream().content();
                            .advisors(a -> a.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, "Journey")).stream()
                            .content();
                    content.doOnNext(System.out::println).blockLast();


                }
            }
        };
    }

}
