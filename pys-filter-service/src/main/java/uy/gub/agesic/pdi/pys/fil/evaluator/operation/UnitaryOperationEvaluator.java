package uy.gub.agesic.pdi.pys.fil.evaluator.operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.domain.FilterRule;
import uy.gub.agesic.pdi.pys.fil.evaluator.OperationEvaluator;

@Slf4j
@Component
public class UnitaryOperationEvaluator implements OperationEvaluator {
    @Override
    public boolean evaluate(FilterRule.Operator operator, String left, String right, int position) {
        boolean result;
        switch (operator) {
            case GREATER:
                result = left.compareTo(right) > 0;
                break;
            case GREATEROREQUAL:
                result = left.compareTo(right) >= 0;
                break;
            case LESSOREQUAL:
                result = left.compareTo(right) <= 0;
                break;
            case MINOR:
                result = left.compareTo(right) < 0;
                break;
            case CONTAINS:
                result = left.contains(right);
                break;
            case EQUAL:
            default:
                result = left.equals(right);
        }
        return result;
    }
}
