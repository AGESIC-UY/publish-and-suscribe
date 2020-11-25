package uy.gub.agesic.pdi.pys.fil.evaluator;

import uy.gub.agesic.pdi.pys.domain.FilterRule;

public interface OperationEvaluator {
    boolean evaluate(FilterRule.Operator operator, String left, String right, int position);
}
