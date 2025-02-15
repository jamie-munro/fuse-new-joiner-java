package org.galatea.starter.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.IexHistoricalPrice;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * A layer for transformation, aggregation, and business required when retrieving data from IEX.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IexService {

  @NonNull
  private IexClient iexClient;


  /**
   * Get all stock symbols from IEX.
   *
   * @return a list of all Stock Symbols from IEX.
   */
  public List<IexSymbol> getAllSymbols() {
    return iexClient.getAllSymbols();
  }

  /**
   * Get the last traded price for each Symbol that is passed in.
   *
   * @param symbols the list of symbols to get a last traded price for.
   * @return a list of last traded price objects for each Symbol that is passed in.
   */
  public List<IexLastTradedPrice> getLastTradedPriceForSymbols(final List<String> symbols) {
    if (CollectionUtils.isEmpty(symbols)) {
      return Collections.emptyList();
    } else {
      return iexClient.getLastTradedPriceForSymbols(symbols.toArray(new String[0]));
    }
  }

  /**
   * Get historical pricing data for the given symbol on the given date
   *
   * @param symbol the symbol to retrieve data about
   * @param date the date which should be queried
   * @return historical pricing data for the given symbol on the given date
   */
  public List<IexHistoricalPrice> getHistoricalPriceOnDate(final String symbol, final LocalDate date) {
    return iexClient.getHistoricalPriceOnDate(symbol, date);
  }

  /**
   * Get historical pricing data for the given symbol and range
   *
   * @param symbol the symbol to retrieve data about
   * @param range the range to query (max, 5y, 2y, 1y, ytd, 6m, 3m, 1m, 1mm, 5d, 5dm, date, dynamic)
   * @return historical pricing data for the given symbol on the given date
   */
  public List<IexHistoricalPrice> getHistoricalPriceForRange(final String symbol, final String range) {
    return iexClient.getHistoricalPriceForRange(symbol, range);
  }

}
