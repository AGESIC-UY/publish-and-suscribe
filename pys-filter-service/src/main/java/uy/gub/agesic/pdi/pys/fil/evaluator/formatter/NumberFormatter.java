package uy.gub.agesic.pdi.pys.fil.evaluator.formatter;

import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.domain.Factor;
import uy.gub.agesic.pdi.pys.fil.evaluator.Formatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class NumberFormatter implements Formatter {
  @Override
  public Factor.ValueType getType() {
    return Factor.ValueType.NUMBER;
  }

  @Override
  public String format(String value, String format) {
    return BigDecimal.valueOf(Double.parseDouble(value)).setScale(4, RoundingMode.HALF_UP).toString();
  }
}
