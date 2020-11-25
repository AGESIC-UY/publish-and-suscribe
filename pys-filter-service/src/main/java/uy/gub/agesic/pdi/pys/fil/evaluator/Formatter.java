package uy.gub.agesic.pdi.pys.fil.evaluator;

import uy.gub.agesic.pdi.pys.domain.Factor;

public interface Formatter {

  Factor.ValueType getType();

  String format(String value, String format);
}
