package org.course.challenge03.controller_remote

import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.*
import org.course.challenge03.StockQuoteDto
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@RestController
class DummyRemoteExchangeController {

    @GetMapping("/quotes")
    @ResponseBody
    suspend fun getQuote(@RequestParam("symbol") symbol:String, @RequestParam("exchange") exchange:String, @RequestParam("delay", required = false) delayMs:Long? = null): StockQuoteDto {
        delayMs.takeIf { it != null && it > 0 }?.let{ delay(it.milliseconds) }
        return StockQuoteDto(symbol, get(exchange, symbol) ?: ((Random.nextDouble(100.0, 2_000.0) * 100).toInt().toDouble() / 100))
    }

    companion object {
        private val  memRepo:ConcurrentHashMap<String, Double> = ConcurrentHashMap()
        fun clearMemRepo() = memRepo.clear()
        fun add(exchange: String, symbol: String, price:Double) = memRepo.put("$exchange-$symbol", price)
        fun get(exchange: String, symbol: String):Double? = memRepo["$exchange-$symbol"]
    }
}
