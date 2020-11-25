package uy.gub.agesic.pdi.pys.fil.evaluator;

import uy.gub.agesic.pdi.pys.domain.Factor;

public interface FactorEvaluator {
    Factor.Type getType();
    String evaluate(String content, String expression);
}
