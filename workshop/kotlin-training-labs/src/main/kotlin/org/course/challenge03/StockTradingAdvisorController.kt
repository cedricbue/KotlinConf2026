package org.course.challenge03

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import java.util.UUID

@Controller
class StockTradingAdvisorController(
    private val stockTradingAdvisorService: StockTradingAdvisorService,
) {

    /**
     * Challenge 3: Part 4 -Exercise B:
     * Implement the [chatWebSocketEndpoint] method
     * As you can see, the websocket receives a [Flow<StockAdvisorRequest>] and returns [Flow<StockAdvisorResponse>].
     * Call the [stockTradingAdvisorService.handleRequest] method for each request received by the [Flow<StockAdvisorRequest>].
     * Which operator do you best use to connect the two flows?
     * Hint: you have used this method in previous exercises.
     *
     * Make the corresponding test in @see [org.course.challenge03.Coroutines04WebsocketControllerTest] pass.
     *
     * Once implemented, try the websocket connection in the browser.
     * - Ensure you have the OPENAI_API_KEY environment variable set to your OpenAI API key.
     * - Start the application
     * - Open the browser and navigate to http://localhost:8081/stocks-advisor.html
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @MessageMapping("stocks.advisor.chat")
    fun chatWebSocketEndpoint(@Payload requests: Flow<StockAdvisorRequest>): Flow<StockAdvisorResponse> {
        val conversationId = UUID.randomUUID().toString() //needed for the chat history of a given Websocket session
        return requests.flatMapConcat { stockTradingAdvisorService.handleRequest(it, conversationId) }
    }
}


@Service
class StockTradingAdvisorService(
    private val chatClient: ChatClient,
    private val stocksRepository: StocksRepository,
) {
    /**
     * Challenge 3: Part 4 - Exercise A:
     * Implement the [handleRequest] method.
     * Task 1: map each chunk of the [chatFlow] Flow to an [StockAdvisorChunk]
     * Task 2: Once the [chatFlow] completes, end with one [StockAdvisorComplete] event.
     * Do you remember how to combine two flows into one?
     * Hint: [flowOf] and [flatMapConcat] are your friends here.
     */
    suspend fun handleRequest(request: StockAdvisorRequest, conversationId: String): Flow<StockAdvisorResponse> {
        val responseId = UUID.randomUUID().toString() //needed for StockAdvisorChunk and StockAdvisorComplete to relate to a single response
        val chatFlow: Flow<StockAdvisorChunk> =  chatFlow(request.content, conversationId).map {
            StockAdvisorChunk(responseId, it)
        }
        return chatFlow.onCompletion { flowOf(StockAdvisorComplete(responseId)) }
    }

    /**
     * Chat with the AI returning a Flow of text chunks ready to be streamed to the client.
     */
    private suspend fun chatFlow(content:String, conversationId: String):Flow<String> {
        val stocks = stocksRepository.findAll().toList().joinToString("\n") {
            "${it.symbol}: current price ${it.price}"
        }
        return chatClient.prompt()
            .system(
                """
                You are a financial advisor for a stock trading training application.
                Answer questions using only the supplied stock data.
                Be concise, explain uncertainty, and do not invent symbols or prices.
            """.trimIndent()
            )
            .user(
                """
                Current stock data:
                $stocks

                User question:
                ${content}
            """.trimIndent()
            )
            .advisors { it.param(ChatMemory.CONVERSATION_ID, conversationId) }
            .stream()
            .content()
            .asFlow()
    }
}
