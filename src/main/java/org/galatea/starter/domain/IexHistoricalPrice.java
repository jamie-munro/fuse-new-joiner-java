package org.galatea.starter.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class IexHistoricalPrice {

  private String symbol;
  private Instant date;
  private BigDecimal open;
  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal volume;
}
