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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
   * @param symbol the symbol to retrieve data about
   * @param date   the date which should be queried
   * @return historical pricing data for the given symbol on the given date
   */
  @GetMapping(value = "${mvc.iex.getHistoricalPricePath}", produces = {
          MediaType.APPLICATION_JSON_VALUE})
  public List<IexHistoricalPrice> getHistoricalPrice(
          @RequestParam(value = "symbol", required = true) final String symbol,
          @RequestParam(value = "range", required = false) final String range,
          @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyyMMdd") final LocalDate date) {
    //handle empty symbol in line with existing empty symbol behaviour
    if (symbol.equals("")) {
      return Collections.emptyList();
    }
    //date mode
    else if ((date != null) && (range == null)) {
      if (checkValidDate(date)) {
        return iexService.getHistoricalPriceOnDate(symbol, date);
      }
      else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date specified");
      }
    }
    //range mode
    else if ((date == null) && (range != null)) {
      if (checkValidRange(range)) {
        return iexService.getHistoricalPriceForRange(symbol, range);
      }
      else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid range specified");
      }
    }
    //values for both date and range
    else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both date and range specified, only one can be set at a time");
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
