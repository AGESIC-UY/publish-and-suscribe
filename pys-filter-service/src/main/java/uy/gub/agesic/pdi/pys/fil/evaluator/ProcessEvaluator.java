package uy.gub.agesic.pdi.pys.fil.evaluator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.domain.Factor;
import uy.gub.agesic.pdi.pys.domain.Filter;
import uy.gub.agesic.pdi.pys.domain.FilterRule;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.fil.error.EvaluationContinueException;
import uy.gub.agesic.pdi.pys.fil.error.EvaluationFailureException;
import uy.gub.agesic.pdi.pys.fil.evaluator.operation.MultipleOperationEvaluator;
import uy.gub.agesic.pdi.pys.fil.evaluator.operation.UnitaryOperationEvaluator;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessEvaluator {
    private final Map<Factor.Type, FactorEvaluator> evaluators;
    private final Map<Factor.ValueType, Formatter> formatters;
    private final ObjectMapper mapper;
    private final MultipleOperationEvaluator multipleOperationEvaluator;
    private final UnitaryOperationEvaluator unitaryOperationEvaluator;

    public ProcessEvaluator(ObjectMapper mapper,
                            List<FactorEvaluator> factorEvaluators,
                            List<Formatter> formatters,
                            MultipleOperationEvaluator multipleOperationEvaluator,
                            UnitaryOperationEvaluator unitaryOperationEvaluator) {
        this.mapper = mapper;
        this.multipleOperationEvaluator = multipleOperationEvaluator;
        this.unitaryOperationEvaluator = unitaryOperationEvaluator;

        evaluators = new EnumMap<>(Factor.Type.class);
        for (FactorEvaluator factorEvaluator : factorEvaluators) {
            evaluators.put(factorEvaluator.getType(), factorEvaluator);
        }

        this.formatters = new EnumMap<>(Factor.ValueType.class);
        for (Formatter formatter : formatters) {
            this.formatters.put(formatter.getType(), formatter);
        }

    }

    public boolean process(FilterRule rule, String left, String right, boolean isUnitary) {
        int position = 0;
        if (isUnitary) {
            return this.unitaryOperationEvaluator.evaluate(rule.getOperator(), left, right, position);
        }
        position = whereIs(rule);

        switch (position) {
            case -1:
                left = formatList(rule.getLeftFactor().getValueType(), left, rule.getLeftFactor().getValueFormat());
                break;
            case 0:
                left = formatList(rule.getLeftFactor().getValueType(), left, rule.getLeftFactor().getValueFormat());
                right = formatList(rule.getRightFactor().getValueType(), right, rule.getRightFactor().getValueFormat());
                break;
            case 1:
                right = formatList(rule.getRightFactor().getValueType(), right, rule.getRightFactor().getValueFormat());
                break;
        }
        return this.multipleOperationEvaluator.evaluate(rule.getOperator(), left, right, position);
    }

    private String formatList(Factor.ValueType type, String expression, String format) {
        if (format == null || format.isEmpty()) {
            return String.join(";", expression.split("\\s*;\\s*"));
        }

        return Arrays.stream(expression.split("\\s*;\\s*"))
                .map(s -> formatters.get(type).format(s, format))
                .collect(Collectors.joining(";"));
    }

    public Boolean evaluate(String content, Filter filter, Suscriptor suscriptor, BinaryOperator<Boolean> operator, int sequence) throws RuntimeException {
        if (operator.apply(false, true)) {
            return evaluateOperatorOr(filter, content, suscriptor, sequence);
        } else {
            return evaluateOperatorAnd(filter, content, suscriptor, sequence);
        }
    }

    private boolean evaluateOperatorAnd(Filter filter, String content, Suscriptor suscriptor, int sequence) throws RuntimeException {
        if (suscriptor != null) {
            return evaluateOperatorAndTypeSubscriber(filter, content, suscriptor, sequence);
        }
        return evaluateOperatorAndTypeMessage(filter, content, suscriptor);
    }

    private boolean evaluateOperatorAndTypeSubscriber(Filter filter, String content, Suscriptor suscriptor, int sequence) throws RuntimeException {
        if (sequence == 0) {
            log.info("Evaluando reglas de tipo mensaje");
            filter.getRules().stream()
                    .anyMatch(rule -> {
                        if (isMessageTypeRule(rule)) {
                            if (!evaluate(content, rule, suscriptor)) {
                                throw new EvaluationFailureException("Falla la evaluacion para regla de tipo MESSAGE en filtro-topico tipo SUBSCRIBER con operador AND");
                            }
                        }
                        return false;
                    });
        }
        log.info("Evaluando reglas de tipo subscriber");
        return !filter.getRules().stream()
                .anyMatch(rule -> {
                    if (isSubscriberTypeRule(rule)) {
                        return !evaluate(content, rule, suscriptor);
                    }
                    return false;
                });
    }

    private boolean evaluateOperatorAndTypeMessage(Filter filter, String content, Suscriptor suscriptor) {
        return !filter.getRules().stream()
                .anyMatch(rule -> !evaluate(content, rule, suscriptor));
    }

    private boolean evaluateOperatorOr(Filter filter, String content, Suscriptor suscriptor, int sequence) throws RuntimeException {
        if (suscriptor != null) {
            return evaluateOperatorOrTypeSubscriber(filter, content, suscriptor, sequence);
        }
        return evaluateOperatorOrTypeMessage(filter, content, suscriptor);
    }

    private boolean evaluateOperatorOrTypeSubscriber(Filter filter, String content, Suscriptor suscriptor, int sequence) throws RuntimeException {
        if (sequence == 0) {
            log.info("Evaluando reglas de tipo mensaje");
            boolean resultRulesMessage = filter.getRules().stream()
                    .anyMatch(rule -> {
                        if (isMessageTypeRule(rule)) {
                            return evaluate(content, rule, suscriptor);
                        }
                        return false;
                    });
            if (resultRulesMessage) {
                throw new EvaluationContinueException("Evaluacion verdadera para regla de tipo MESSAGE en fitro-topico tipo SUBSCRIBER con operador OR");
            }
        }
        log.info("Evaluando reglas de tipo subscriber");
        return filter.getRules().stream()
                .anyMatch(rule -> {
                    if (isSubscriberTypeRule(rule)) {
                        return evaluate(content, rule, suscriptor);
                    }
                    return false;
                });
    }

    private boolean evaluateOperatorOrTypeMessage(Filter filter, String content, Suscriptor suscriptor) {
        return filter.getRules().stream()
                .anyMatch(rule -> evaluate(content, rule, suscriptor));
    }

    private boolean evaluate(String content, FilterRule rule, Suscriptor suscriptor) {
        String left = this.evaluateFactor(content, rule, suscriptor, true);
        String right = this.evaluateFactor(content, rule, suscriptor, false);

        boolean isUnitary = true;
        if (rule.getLeftFactor().getType() == Factor.Type.LIST || rule.getRightFactor().getType() == Factor.Type.LIST) {
            isUnitary = false;
        }

        if (rule.getLeftFactor().getType() != Factor.Type.LIST) {
            Formatter formatter = formatters.get(rule.getLeftFactor().getValueType());
            if (formatter != null)
                left = formatter.format(left, rule.getLeftFactor().getValueFormat());
        }
        Formatter formatter = formatters.get(rule.getRightFactor().getValueType());
        if (rule.getLeftFactor().getType() != Factor.Type.LIST)
            if (formatter != null)
                right = formatter.format(right, rule.getRightFactor().getValueFormat());

        boolean result = this.process(rule, left, right, isUnitary);
        log.info(String.format("(%s) %s (%s) = %s", left, rule.getOperator(), right, result));

        return result;
    }

    private boolean isSubscriberTypeRule(FilterRule rule) {
        return rule.getLeftFactor().getType().equals(Factor.Type.SUBSCRIBER) || rule.getRightFactor().getType().equals(Factor.Type.SUBSCRIBER) ;
    }

    private boolean isMessageTypeRule(FilterRule rule) {
        return !rule.getLeftFactor().getType().equals(Factor.Type.SUBSCRIBER) && !rule.getRightFactor().getType().equals(Factor.Type.SUBSCRIBER) ;
    }

    private String evaluateFactor(String content, FilterRule rule, Suscriptor suscriptor, boolean isLeft) {
        String json;
        try {
            json = mapper.writeValueAsString(suscriptor);
        } catch (JsonProcessingException e) {
            json = "";
        }

        String factor;
        if (isLeft) {
            factor = evaluators.get(rule.getLeftFactor().getType())
                    .evaluate(rule.getLeftFactor().getType().equals(Factor.Type.SUBSCRIBER) ? json : content, rule.getLeftFactor().getValue());
        } else {
            factor = evaluators.get(rule.getRightFactor().getType())
                    .evaluate(rule.getRightFactor().getType().equals(Factor.Type.SUBSCRIBER) ? json : content, rule.getRightFactor().getValue());
        }

        return factor;
    }

    private int whereIs(FilterRule rule) {
        if (rule.getLeftFactor().getType() == Factor.Type.LIST && rule.getRightFactor().getType() == Factor.Type.LIST) {
            return 0;
        } else {
            if (rule.getLeftFactor().getType() == Factor.Type.LIST) {
                return -1;
            }
        }
        return 1;
    }
}
