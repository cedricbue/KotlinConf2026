package org.course.challenge03

import kotlinx.coroutines.*
import org.course.challenge03.blocking.BlockingJExchangeServiceEuronext
import org.course.challenge03.blocking.BlockingJExchangeServiceNasdaq
import org.course.challenge03.blocking.BlockingJExchangeServiceSix
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
class ClassicStockTraderController(
    val stocksRepository: StocksRepository,
    blockingExchangeServiceNasdaq: BlockingJExchangeServiceNasdaq,
    blockingExchangeServiceEuronext: BlockingJExchangeServiceEuronext,
    blockingExchangeServiceSix: BlockingJExchangeServiceSix
) {

    val exchanges = listOf(blockingExchangeServiceEuronext,blockingExchangeServiceNasdaq, blockingExchangeServiceSix)

    /**
     * Challenge 3 - Part 5 - Exercise A
     * In this exercise you will learn how to use VirtualThreads to get the most out of legacy (classic ;-)) code.
     * The below endpoint is almost identical to the /stocks/quote endpoint, except that a blocking Api [RestClient]
     * is used to fetch stock quotes rather than the non-blocking [WebClient]. Take a look at the implementation
     * of one of the [BlockingJExchangeService]XYZ to ensure yourself that the blocking [RestClient] is used.
     *
     * As you might know by now, making blocking calls within Coroutines has dire consequences. For safety reasons,
     * the latest Reactor version does not even allow it and throws a:
     * java.io.IOException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread webflux-http-nio-... @coroutine#...
     * However, by combining VirtualThreads with Coroutines, we can fix this shortcoming.
     *
     * So your task is the following:
     * - Apply VirtualThreads wherever possible to make the first test in [CoroutinesVirtualThreads05ControllerTest] succeed.
     * - The test will also ensure that all exchanges endpoints are truly called in parallel.
     * - 'Apply VirtualThreads' is a broad term: where do you need to configure / provide VirtualThreads to have the below
     *    scenario working as desired?
     * - Hints: [application.properties] and/or [Dispatchers]...
     *
     * Make the corresponding test in @see [org.course.challenge03.CoroutinesVirtualThreads05ControllerTest] pass.
     */
    @GetMapping("/classic/stocks/quote", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    suspend fun bestQuoteBlocking(@RequestParam("symbol") symbol: String, @RequestParam("delay", required = false) delay: Long? = null): StockQuoteDto = coroutineScope {
        TODO("Uncomment code below")
//            stockRepository.findBySymbol(symbol)?.let { stock ->
//                exchanges.map { async { it.getStockQuote(stock.symbol, delay) } }.awaitAll().minByOrNull { (it.currentPrice) }
//            } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find stock with symbol=$symbol")
    }

}
