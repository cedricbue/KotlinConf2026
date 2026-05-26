package org.course.challenge03.blocking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlockingJExchangeServiceSix extends BlockingJExchangeService {

    public BlockingJExchangeServiceSix(@Value("${remote.service.url}") String baseUrl) {
        super(   baseUrl, "six");
    }
}
