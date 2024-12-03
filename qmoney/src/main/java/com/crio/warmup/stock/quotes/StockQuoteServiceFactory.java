
package com.crio.warmup.stock.quotes;

import org.springframework.web.client.RestTemplate;

public final class StockQuoteServiceFactory {

  // Note: (Recommended reading)
  // Pros and cons of implementing Singleton via enum.
  // https://softwareengineering.stackexchange.com/q/179386/253205

  public final static StockQuoteServiceFactory  INSTANCE = new StockQuoteServiceFactory();
  private StockQuoteServiceFactory()
  {

  }


  public StockQuotesService getService(String provider,  RestTemplate restTemplate) {

    switch ((provider!=null)?provider.toLowerCase():"alphavantage") {
      case "tiingo":
        return new TiingoService(restTemplate);
      default:
        return new AlphavantageService(restTemplate);

    
  }
}
}
