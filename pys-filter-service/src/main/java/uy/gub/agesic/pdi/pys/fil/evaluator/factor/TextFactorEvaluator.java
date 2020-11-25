package uy.gub.agesic.pdi.pys.fil.evaluator.factor;

import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.domain.Factor;
import uy.gub.agesic.pdi.pys.fil.evaluator.FactorEvaluator;

@Component
public class TextFactorEvaluator implements FactorEvaluator {
    @Override
    public Factor.Type getType() {
        return Factor.Type.FIXEDTEXT;
    }

    @Override
    public String evaluate(String content, String expression) {
        return expression;
    }
}
