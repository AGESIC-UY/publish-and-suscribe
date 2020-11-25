package uy.gub.agesic.pdi.pys.fil.evaluator.operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.domain.FilterRule;
import uy.gub.agesic.pdi.pys.fil.evaluator.OperationEvaluator;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MultipleOperationEvaluator implements OperationEvaluator {


    @Override
    public boolean evaluate(FilterRule.Operator operator, String left, String right, int position) {
        boolean result;
        switch (position) {
            case -1: {
                result = this.processMultipleInLeft(operator, left, right);
            } break;
            case 1: {
                result = this.processMultipleInRight(operator, left, right);
            } break;
            case 0:
            default:{
                result = this.processMultipleInBoth(operator, left, right);
            }
        }
        return result;
    }

    private boolean processMultipleInLeft(FilterRule.Operator operator, String left, String right) {
        boolean result;
        switch (operator) {
            case CONTAINS:
                result = this.containsElement(right, left);
                break;
            case GREATER:
            case GREATEROREQUAL:
            case LESSOREQUAL:
            case MINOR:
            case EQUAL:
            default:
                result = false;
        }
        return result;
    }

    private boolean processMultipleInRight(FilterRule.Operator operator, String left, String right) {
        return false;
    }

    private boolean processMultipleInBoth(FilterRule.Operator operator, String left, String right) {
        boolean result;
        switch (operator) {
            case CONTAINS:
                result = this.containsList(right, left);
                break;
            case GREATER:
            case GREATEROREQUAL:
            case LESSOREQUAL:
            case MINOR:
            case EQUAL:
            default:
                result = false;
        }
        return result;
    }

    private boolean containsElement(String needle, String haystack) {
        List<String> items = Arrays.asList(haystack.split("\\s*;\\s*"));

        return items.contains(needle);
    }

    private boolean containsList(String needle, String haystack) {
        List<String> left = Arrays.asList(needle.split("\\s*;\\s*"));
        List<String> right = Arrays.asList(haystack.split("\\s*;\\s*"));
        return left.containsAll(right);
    }
}
