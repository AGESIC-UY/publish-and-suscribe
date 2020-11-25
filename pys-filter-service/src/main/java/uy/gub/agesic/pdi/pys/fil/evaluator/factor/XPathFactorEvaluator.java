package uy.gub.agesic.pdi.pys.fil.evaluator.factor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import uy.gub.agesic.pdi.common.xml.XPathEvaluatorHelper;
import uy.gub.agesic.pdi.pys.domain.Factor;
import uy.gub.agesic.pdi.pys.fil.evaluator.FactorEvaluator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;


@Slf4j
@Component
public class XPathFactorEvaluator implements FactorEvaluator {
    @Override
    public Factor.Type getType() {
        return Factor.Type.XPATH;
    }

    @Override
    public String evaluate(String content, String expression) {
        try {
            XPathEvaluatorHelper evaluator = new XPathEvaluatorHelper(content);
            return evaluator.evaluate(expression, XPathConstants.STRING).toString();

        } catch (IOException | SAXException | XPathExpressionException | ParserConfigurationException e) {
            log.error("", e);
        }
        return null;
    }
}
