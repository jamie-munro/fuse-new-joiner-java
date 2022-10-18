package org.galatea.starter.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class IexHistoricalPrice {

  private String symbol;
  private Date date;
  private BigDecimal open;
  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal volume;
}
