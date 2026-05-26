package org.course.challenge03

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table


@Table("STOCKS")
data class Stock(
    @Id
    val id: Long? = null,
    val symbol: String,
    val price: Double,
)


enum class StockEventType {
    MODIFIED,
    REMOVED,
}
@Table("STOCK_EVENTS")
data class StockEvent(
    @Id
    val id: Long? = null,
    val type: StockEventType,
    val symbol: String,
    val price: Double,
)

fun Stock.toStockEvent(eventType: StockEventType) = StockEvent(symbol = symbol, price = price, type = eventType)

data class StockQuoteDto(val symbol: String,
                         val currentPrice: Double)





data class StockAdvisorRequest(val content: String)

enum class StockAdvisorResponseType(@get:JsonValue val value: String) {
    CHUNK("chunk"),
    COMPLETE("complete"),
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = StockAdvisorChunk::class, name = "chunk"),
    JsonSubTypes.Type(value = StockAdvisorComplete::class, name = "complete"),
)
sealed interface StockAdvisorResponse {
    val id: String
    val type: StockAdvisorResponseType
}

data class StockAdvisorChunk(
    override val id: String,
    val content: String,
    override val type: StockAdvisorResponseType = StockAdvisorResponseType.CHUNK,
) : StockAdvisorResponse

data class StockAdvisorComplete(
    override val id: String,
    override val type: StockAdvisorResponseType = StockAdvisorResponseType.COMPLETE,
) : StockAdvisorResponse
