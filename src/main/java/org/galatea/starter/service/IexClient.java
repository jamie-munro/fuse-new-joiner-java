package org.galatea.starter.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import feign.Param;
import org.galatea.starter.domain.IexHistoricalPrice;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A Feign Declarative REST Client to access endpoints from the Free and Open IEX API to get market
 * data. See https://iextrading.com/developer/docs/
 */
@FeignClient(name = "IEX", url = "${spring.rest.iexBasePath}")
public interface IexClient {

  /**
   * Get a list of all stocks supported by IEX. See https://iextrading.com/developer/docs/#symbols.
   * As of July 2019 this returns almost 9,000 symbols, so maybe don't call it in a loop.
   *
   * @return a list of all of the stock symbols supported by IEX.
   */
  @GetMapping("/ref-data/symbols?token=${spring.rest.iexToken}")
  List<IexSymbol> getAllSymbols();

  /**
   * Get the last traded price for each stock symbol passed in. See https://iextrading.com/developer/docs/#last.
   *
   * @param symbols stock symbols to get last traded price for.
   * @return a list of the last traded price for each of the symbols passed in.
   */
  @GetMapping("/tops/last?token=${spring.rest.iexToken}")
  List<IexLastTradedPrice> getLastTradedPriceForSymbols(@RequestParam("symbols") String[] symbols);

  /**
   * Get historical pricing data for the given symbol on the given date. See https://iexcloud.io/docs/api/#historical-prices
   *
   * @param symbol the symbol to retrieve data about
   * @param date the date which should be queried (in "yyyyMMdd" String format)
   * @return historical pricing data for the given symbol on the given date
   */
  @GetMapping("/stock/{symbol}/chart/date/{date}?chartByDay=true&token=${spring.rest.iexToken}")
  List<IexHistoricalPrice> getHistoricalPriceOnDate(@PathVariable(value="symbol") String symbol, @PathVariable(value="date") String date);

  /**
   * Helper method to convert LocalDate format dates in the string format required by IEX
   *
   * @param symbol the symbol to retrieve data about
   * @param date the date which should be queried (in LocalDate format)
   * @return historical pricing data for the given symbol on the given date
   */
  default List<IexHistoricalPrice> getHistoricalPriceOnDate(String symbol, LocalDate date) {
    return getHistoricalPriceOnDate(symbol, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
  }

  /**
   * Get historical pricing data for the given symbol and range. See https://iexcloud.io/docs/api/#historical-prices
   *
   * @param symbol the symbol to retrieve data about
   * @param range the range to query (max, 5y, 2y, 1y, ytd, 6m, 3m, 1m, 1mm, 5d, 5dm, date, dynamic)
   * @return historical pricing data for the given symbol on the given date
   */
  @GetMapping("/stock/{symbol}/chart/{range}?token=${spring.rest.iexToken}")
  List<IexHistoricalPrice> getHistoricalPriceForRange(@PathVariable(value="symbol") String symbol, @PathVariable(value="range") String range);
}
