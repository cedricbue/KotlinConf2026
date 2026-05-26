package org.course.challenge03

import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.course.uitls.retryTillOk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.dataWithType
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.net.URI

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
class Coroutines04WebsocketControllerTest @Autowired constructor(
    val requesterBuilder: RSocketRequester.Builder,
    @MockkBean
    val stockTradingAdvisorService: StockTradingAdvisorService,
    @Value("\${remote.service.url}") baseUrl: String,
    @LocalServerPort val localPort: Int,
) {

    @BeforeEach
    fun setup() {
        coEvery { stockTradingAdvisorService.handleRequest(any(), any()) } answers {
            flowOf(
                StockAdvisorChunk(content = "advisor ", id = "response-1"),
                StockAdvisorChunk(content = "response ", id = "response-1"),
                StockAdvisorComplete(id = "response-1"),
            )
        }
    }

    /**
     * Exercise A and B:
     * For instructions of Exercise A: @see [StockTradingAdvisorService.handleRequest]
     * For instructions of Exercise B: @see [StockTradingAdvisorController.chatWebSocketEndpoint]
     */
    @Test
    @Timeout(5)
    fun `Exercise A and B should chat with financial advisor over one RSocket WebSocket connection`(): Unit = runBlocking {
        val requester = requesterBuilder.websocket(URI("ws://localhost:$localPort/rsocket"))
        val outbound = MutableSharedFlow<StockAdvisorRequest>(replay = 1)
        val consumed = mutableListOf<StockAdvisorResponse>()
        val consumerJob = launch {
            requester.route("stocks.advisor.chat")
                .dataWithType(outbound)
                .retrieveFlow<StockAdvisorResponse>()
                .collect {
                    consumed.add(it)
                }
        }

        try {
            outbound.emit(StockAdvisorRequest(content = "What is the current AAPL price?"))
            retryTillOk {
                consumed shouldContainAll listOf(
                    StockAdvisorChunk(content = "advisor ", id = "response-1"),
                    StockAdvisorChunk(content = "response ", id = "response-1"),
                    StockAdvisorComplete(id = "response-1"),
                )
            }

            outbound.emit(StockAdvisorRequest(content = "Compare GOOG and MSFT in detail."))
            retryTillOk {
                consumed.last().type shouldBe StockAdvisorResponseType.COMPLETE
            }
        } finally {
            consumerJob.cancelAndJoin()
            requester.dispose()
        }
    }

}
