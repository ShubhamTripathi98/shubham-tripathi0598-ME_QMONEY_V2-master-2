
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {


  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {

      return new PortfolioManagerImpl(restTemplate);

  }




   public static PortfolioManager getPortfolioManager(String provider, RestTemplate restTemplate) {
    return new PortfolioManagerImpl(StockQuoteServiceFactory.INSTANCE.getService(provider, restTemplate));    
}

}
