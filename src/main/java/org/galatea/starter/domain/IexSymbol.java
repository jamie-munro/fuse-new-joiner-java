package org.galatea.starter.domain;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IexSymbol {

  private String symbol;
  private String name;
  private LocalDate date;
  private boolean isEnabled;
  private String type;
  private String iexId;

}
