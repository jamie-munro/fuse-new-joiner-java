package org.galatea.starter.entrypoint;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Level;
import org.galatea.starter.domain.IexHistoricalPrice;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.galatea.starter.service.IexService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Log(enterLevel = Level.INFO, exitLevel = Level.INFO)
@Validated
@RestController
@RequiredArgsConstructor
public class IexRestController {

  @NonNull
  private IexService iexService;

  @Value("${spring.rest.iexValidRanges}")
  private List<String> iexValidRanges;

  /**
   * Exposes an endpoint to get all the symbols available on IEX.
   *
   * @return a list of all IexStockSymbols.
   */
  @GetMapping(value = "${mvc.iex.getAllSymbolsPath}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public List<IexSymbol> getAllStockSymbols() {
    return iexService.getAllSymbols();
  }

  /**
   * Get the last traded price for each of the symbols passed in.
   *
   * @param symbols list of symbols to get last traded price for.
   * @return a List of IexLastTradedPrice objects for the given symbols.
   */
  @GetMapping(value = "${mvc.iex.getLastTradedPricePath}", produces = {
          MediaType.APPLICATION_JSON_VALUE})
  public List<IexLastTradedPrice> getLastTradedPrice(
          @RequestParam(value = "symbols") final List<String> symbols) {
    return iexService.getLastTradedPriceForSymbols(symbols);
  }

  /**
   * Get historical pricing data for the given symbol on the given date
   *
   * @param symbols the symbol to retrieve data about
   * @param dates   the date which should be queried
   * @return historical pricing data for the given symbol on the given date
   */
  @GetMapping(value = "${mvc.iex.getHistoricalPricePath}", produces = {
          MediaType.APPLICATION_JSON_VALUE})
  public List<IexHistoricalPrice> getHistoricalPrice(
          @RequestParam(value = "symbol", required = true) final List<String> symbols,
          @RequestParam(value = "range", required = false) final List<String> ranges,
          @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyyMMdd") final List<LocalDate> dates) {
    //date mode
    if ((symbols.size() == 1) && (dates != null) && (dates.size() == 1) && (ranges == null)) {
      LocalDate date = dates.get(0);

      if (checkValidDate(date)) {
        return iexService.getHistoricalPriceOnDate(symbols.get(0), dates.get(0));
      }
      else {
        return Collections.emptyList();
      }
    }
    //range mode
    else if ((symbols.size() == 1) && (dates == null) && (ranges != null) && (ranges.size() == 1)) {
      String range = ranges.get(0);

      if (checkValidRange(range)) {
        return iexService.getHistoricalPriceForRange(symbols.get(0), ranges.get(0));
      }
      else {
        return Collections.emptyList();
      }
    }
    //invalid
    else {
      return Collections.emptyList();
    }
  }

  /**
   * Checks if a given date is valid
   *
   * @param date date to check
   * @return validity
   */
  private boolean checkValidDate(LocalDate date) {
    LocalDate today = LocalDate.now();

    //check if data is in the past
    return date.isBefore(today);
  }

  /**
   * Checks if a given range is valid
   *
   * @param range range to check
   * @return validity
   */
  private boolean checkValidRange(String range) {
    return iexValidRanges.contains(range);
  }
}
