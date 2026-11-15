package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.ai.session.compaction.RecursiveSummarizationCompactionStrategy;
import org.springframework.ai.session.compaction.TurnCountTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfiguration {
    @Bean
    ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder().maxMessages(10)
                //.chatMemoryRepository()
                .chatMemoryRepository(chatMemoryRepository).build();
    }

    @Bean
    SessionMemoryAdvisor sessionMemorySummarizationAdvisor(DeepSeekChatModel deepSeekChatModel) {
        SessionService service = DefaultSessionService.builder()
                .sessionRepository(InMemorySessionRepository.builder().build()).build();

        return SessionMemoryAdvisor.builder(service).defaultUserId("Journey")
                .compactionTrigger(new TurnCountTrigger(10))
                .compactionStrategy(RecursiveSummarizationCompactionStrategy.builder(ChatClient.builder(deepSeekChatModel)
                        .build()).maxEventsToKeep(10).overlapSize(2).build()).build();
    }

}
