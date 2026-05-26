package org.course.challenge03

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Try out the application
 * - Ensure you have the OPENAI_API_KEY environment variable set to your OpenAI API key.
 * - Start the application
 * - Open the browser and navigate to:
 *      http://localhost:8081/stocks-management.html
 *      http://localhost:8081/stocks-advisor.html
 */
@SpringBootApplication
@EnableScheduling
@EnableR2dbcRepositories
class StockExchangeApplication

fun main(args: Array<String>) {
    run(StockExchangeApplication::class.java, *args)
}

@Configuration
class AiConfig {
    @Bean
    fun chatClient(chatClientBuilder: ChatClient.Builder, chatMemory: ChatMemory): ChatClient =
        chatClientBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build()

    @Bean
    fun chatMemory() = MessageWindowChatMemory.builder().chatMemoryRepository(InMemoryChatMemoryRepository()).build()

}
