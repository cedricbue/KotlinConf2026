
package org.course.challenge03.blocking;

import org.course.challenge03.StockQuoteDto;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.client.RestClient;

abstract public class BlockingJExchangeService {

    private RestClient restClient;
    public final String baseUrl;
    private final String exchangeId;

    public BlockingJExchangeService(String baseUrl, String exchangeId) {
        this.baseUrl = baseUrl;
        this.exchangeId = exchangeId;
    }

    public StockQuoteDto getStockQuote(String stockSymbol, @Nullable Long delay) {
        var url = "/quotes?symbol=" + stockSymbol + "&exchange=" + exchangeId + (delay != null ? "&delay=" + delay : "");
        return getRestClient().get().uri(url, StockQuoteDto.class).retrieve().body(StockQuoteDto.class);

    }


    public String getExchangeId() {
        return exchangeId;
    }

    private RestClient getRestClient() {
        if (restClient == null) {
            this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        }
        return restClient;
    }



}
