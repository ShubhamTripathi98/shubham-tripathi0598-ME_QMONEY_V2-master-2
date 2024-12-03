package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.List;


public interface StockQuotesService {
//    static List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonMappingException, JsonProcessingException {
//     // TODO Auto-generated method stub
//     return null;
// }

List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
throws StockQuoteServiceException, JsonMappingException, JsonProcessingException
  ;
  //CHECKSTYLE:ON

}

 
  


