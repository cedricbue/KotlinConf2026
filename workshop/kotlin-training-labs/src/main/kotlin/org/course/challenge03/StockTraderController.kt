package org.course.challenge03

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

@RestController
class StockTraderController(
    val stocksRepository: StocksRepository,
    val stocksEventRepository: StocksEventRepository,
    exchangeServiceNasdaq: ExchangeServiceNasdaq,
    exchangeServiceEuronext: ExchangeServiceEuronext,
    exchangeServiceSix: ExchangeServiceSix
) {

    val exchanges = listOf(exchangeServiceEuronext, exchangeServiceNasdaq, exchangeServiceSix)

    /**
     * Challenge 3 - Part 2 - Exercise A
     * Take a look at the reactive @see [JStockTraderController.getStock] Java implementation,
     * which makes use of the reactive [JStockRepository] to fetch a stock by id from the database.
     * In this exercise you have to convert the Java implementation to its Coroutine counterpart
     * by using the injected [stocksRepository]. Remember not to use the Mono abstraction at all.
     * Make the corresponding test in @see [org.course.challenge03.Coroutines02ControllerTest] pass.
     */
    @GetMapping("/stocks/{stock-id}")
    @ResponseBody
    suspend fun getStock(@PathVariable("stock-id") id: Long = 0): Stock? = stocksRepository.findById(id)


    /**
     * Challenge 3 - Part 2 - Exercise B
     * Take a look at the reactive @see [JStockTraderController.getStock] Java implementation,
     * which makes use of the reactive [JStockRepository] to fetch a stock by symbol from the database.
     * In this exercise you have to convert the Java implementation to its Coroutine counterpart
     * by using the injected [stocksRepository]. Remember not to use the Mono abstraction at all.
     * Make the corresponding test in @see [org.course.challenge03.Coroutines02ControllerTest] pass.
     */
    @GetMapping("/stock")
    @ResponseBody
    suspend fun stockBySymbol(@RequestParam("symbol") symbol: String): Stock? = stocksRepository.findBySymbol(symbol)


    /**
     * Challenge 3 - Part 2 - Exercise C
     * Take a look at the reactive @see [JStockTraderController.upsertStock] Java implementation,
     * which makes use of the reactive [JStockRepository] to upsert a stock in the database.
     * In this exercise you have to convert the Java implementation to its Coroutine counterpart
     * by using the injected [stocksRepository]. Remember not to use the Mono abstraction at all.
     * Make the corresponding test in @see [org.course.challenge03.Coroutines02ControllerTest] pass.
     *
     * (In Challenge 3 - Part 3 - Exercise C you will have to extend this method)
     */
    @PostMapping("/stocks")
    @ResponseBody
    suspend fun upsertStock(@RequestBody stock: Stock): Stock =
        (stocksRepository.findBySymbol(stock.symbol)?.copy(price = stock.price)
            ?: stock).let { stocksRepository.save(it) }

    /**
     * Challenge 3 - Part 2 - Exercise D
     * Take a look at the reactive @see [JStockTraderController.bestQuote] Java implementation,
     * which makes use of the reactive JExchangeServices to fetch several quotes for a stock and
     * filters out the one with the lowest price.
     * In this exercise you have to convert the Java implementation to its Coroutine counterpart
     * by using the injected exchanges. Remember not to use the Mono abstraction at all.
     * Tip: to call the different exchanges in parallel make use of [kotlinx.coroutines.async] / [kotlinx.coroutines.Deferred.await]. Don't forget
     * to use the helper method [kotlinx.coroutines.coroutineScope] for async to work.
     * Make the corresponding test in @see [org.course.challenge03.Coroutines02ControllerTest] pass.
     */
    @GetMapping("/stocks/quote")
    @ResponseBody
    suspend fun bestQuote(
        @RequestParam("symbol") symbol: String,
        @RequestParam("delay", required = false) delay: Long? = null
    ): StockQuoteDto? = coroutineScope() {
        stocksRepository.findBySymbol(symbol)?.let { stock ->
            exchanges
                .map { service -> async { service.getStockQuote(stock.symbol) } }
                .awaitAll()
                .minByOrNull { it.currentPrice }
        } ?: throw HttpClientErrorException(HttpStatus.NOT_FOUND)
    }

    //========================================================================================================

    /**
     * Challenge 3 - Part 3 - Exercise A
     * Take a look at the reactive @see [JStockTraderController.getStocks] Java implementation,
     * which makes use of the reactive [JStockRepository] to fetch all stocks as a Flux.
     * In this exercise you have to convert the Java implementation to its Coroutine counterpart
     * by using the injected [stocksRepository]. Remember to use a Flow and not the Flux abstraction at all.
     * Make the corresponding test in @see [org.course.challenge03.Coroutines03FlowControllerTest] pass.
     */
    @GetMapping("/stocks")
    @ResponseBody
    fun getStocks(): Flow<Stock> = stocksRepository.findAll()


    /**
     * Challenge 3 - Part 3 - Exercise B
     * In this exercise you will apply (almost) the same logic as in Challenge 2 Exercise C: [NewsFeedService.pollingNewsFeedFlow],
     * where you implemented a flow that was polling a resources in intervals of 200ms and returned newly added items.
     *
     * Now you will take this implementation to the next level: you will poll a 'real' database resource and
     * return newly added [StockEvent]s in a [ServerSentEvent]<StockEvent> stream, so that potentially the whole world
     * can consume your flow.
     * Therefore, implement an infinite flow that initially fetches all [StockEvent]s via [StocksEventRepository.findById_GreaterThan]
     * and then keeps on polling for new Stocks in intervals of 200ms
     *
     * Task 1: Implement the fetch() method that fetches all [StockEvent]s via [StocksEventRepository.findById_GreaterThan].
     * For every item fetched, update the latestId. Which flow operator do you need to use?
     *
     * Task 2: Now we need to poll for new items in intervals of 200ms and emit them to the flow.
     * Hint A: For a simple implementation use a flow builder with a while loop that continuously
     * calls fetch() with a delay of 200ms and emits the result.
     *
     * Hint 0: re-visit @see [org.course.challenge02.NewsFeedService.pollingNewsFeedFlow]. The implementation is almost identical
     * Hint 1: Each item of the Flow returned by [StocksEventRepository.findById_GreaterThan] needs to be mapped
     * to a [ServerSentEvent]<StockEvent>. You can use the [ServerSentEvent.builder] helper method for this.
     *
     * Make the corresponding test in @see [org.course.challenge03.Coroutines03FlowControllerTest] pass.
     */
    @GetMapping("/stocks/sse-polling", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @ResponseBody
    suspend fun pollingStockEventFlow(@RequestParam("offset") offsetId: Long = 0): Flow<ServerSentEvent<StockEvent>> {
        var latestId = offsetId
        fun fetch(): Flow<ServerSentEvent<StockEvent>> = stocksEventRepository.findById_GreaterThan(latestId).onEach { stock ->
            latestId = stock.id!!
        }.map { ServerSentEvent.builder<StockEvent>().data(it).build() }

        return generateSequence { Unit }
            .asFlow()
            .onEach { delay(200) }
            .flatMapConcat { fetch() }
    }


    data object StockEventAddedNotification

    private val sharedFlow = MutableSharedFlow<StockEventAddedNotification>()

    /**
     * Challenge 3 - Part 3 - Exercise C
     * In this exercise you will apply (almost) the same logic as in Challenge 2 Exercise D: [NewsFeedService.sharedFlowTriggeredNewsFeedFlow],
     * where you implemented a flow that was first fetching all items of resources and then waited for a notification being
     * emitted by the SharedFlow to fetch and return newly added items.
     *
     * Task 1:
     * First extend the above @see [StockTraderController.upsertStock] method that has to:
     *   - save a [StockEvent] via [StocksEventRepository] for each modified stock
     *   - emit a [StockEventAddedNotification] to the predefined [MutableSharedFlow] so the SSE stream is notified to do a re-fetch.
     *
     * Task 2:
     * A) In the [sharedFlowTriggeredStockEvents] method, implement the fetch() method that fetches all
     * initial [StockEvent]s via [NewsFeedDao.findByIdGreaterThan] based on the given offset.
     * For every item fetched, update the latestId - same like in Exercise D.
     *
     * B) Second, whenever the [MutableSharedFlow] emits a [StockEventAddedNotification], call fetch() to retrieve the new items.
     * Hint: consider using the [flatMapConcat] on [sharedFlow] for triggering the fetch.
     *
     * C) Third, combine the two flows: ensure the [initialFetch] flow is first consumed before
     * the [triggeredFetches] is triggered.
     * Hint: consider using the [flowOf] and [flattenConcat] operators.
     *
     * Make the corresponding test in @see [org.course.challenge03.Coroutines03FlowControllerTest] pass.
     *
     * Once the test passes, you can test all endpoints manually by the following URL in your browser:
     * - http://localhost:8081/stocks-management.html
     */
    @GetMapping("/stocks/sse-shared-flow-triggered", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @ResponseBody
    fun sharedFlowTriggeredStockEvents(@RequestParam("offset") offsetId: Long = 0): Flow<ServerSentEvent<StockEvent>> {
        var latestId = offsetId
        fun fetch(): Flow<StockEvent> = TODO("Task 2 A): implement")

        val initialFetch: Flow<StockEvent> = fetch()

        val triggeredFetches: Flow<StockEvent> = TODO("Task 2 B): implement")

        return TODO("Task 2 C): implement")
    }

}
