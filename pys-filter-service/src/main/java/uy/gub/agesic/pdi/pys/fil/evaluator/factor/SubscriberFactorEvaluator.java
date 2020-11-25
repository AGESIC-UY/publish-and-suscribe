package uy.gub.agesic.pdi.pys.fil.evaluator.factor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.domain.Factor;
import uy.gub.agesic.pdi.pys.fil.evaluator.FactorEvaluator;

import java.io.IOException;
import java.util.HashMap;

@Component
@Slf4j
public class SubscriberFactorEvaluator implements FactorEvaluator {
    private final ObjectMapper mapper;

    public SubscriberFactorEvaluator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Factor.Type getType() {
        return Factor.Type.SUBSCRIBER;
    }

    @Override
    public String evaluate(String content, String expression) {
        try {
            HashMap<String, Object> subscriber = mapper.readValue(content, new TypeReference<HashMap<String, Object>>() {
            });
            return subscriber.get(expression).toString();
        } catch (IOException e) {
            log.error("Error in subscriber's deserializer : " + content, e);
        }
        return null;
    }
}
