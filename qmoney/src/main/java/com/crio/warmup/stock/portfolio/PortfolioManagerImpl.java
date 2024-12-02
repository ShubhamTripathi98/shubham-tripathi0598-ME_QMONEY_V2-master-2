
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private static String token = "279ea24ef64c41da0536b04d684e904eb74137bd";
  private StockQuotesService stockQuotesService;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
  }

  public PortfolioManagerImpl(StockQuotesService stockQuotesService){
      this.stockQuotesService = stockQuotesService;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,JsonMappingException, StockQuoteServiceException {
        return stockQuotesService.getStockQuote(symbol, from, to);
      
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String formattedEndDate = endDate.toString();
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + 
                 "/prices?startDate=" + startDate + "&endDate=" + formattedEndDate + 
                 "&token=" + token;
    return url;
  }
  private Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
 }


  private Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size() -1).getClose();
 }

 public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        if(endDate.isBefore(trade.getPurchaseDate())){
          throw new RuntimeException("Sell date can't be before end date");
        }
        Double buyValue = buyPrice * trade.getQuantity();
        Double sellValue = sellPrice * trade.getQuantity();
        Double totalReturn = (sellValue - buyValue)/ buyValue;
        long noOfDays = ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate);
        double totalNoyears = ((double) (noOfDays)/365.0); 
        Double annualizedReturn = Math.pow(1+totalReturn, 1/totalNoyears)-1;
      return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturn);
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
    LocalDate endDate) {
      List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
      for (PortfolioTrade trade : portfolioTrades) {
          try {
              List<Candle> candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
              AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(
                      endDate, trade, getOpeningPriceOnStartDate(candles), getClosingPriceOnEndDate(candles));
              annualizedReturns.add(annualizedReturn);
          } catch (JsonProcessingException | StockQuoteServiceException e) {
              throw new RuntimeException("Error fetching stock data", e);
          }
      }
      Collections.sort(annualizedReturns, getComparator());
      return annualizedReturns;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate, int i) {
    // TODO Auto-generated method stub
    return null;
  }


}
