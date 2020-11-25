package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import com.google.common.base.Strings;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.xml.sax.SAXException;
import uy.gub.agesic.pdi.common.xml.XPathEvaluatorHelper;
import uy.gub.agesic.pdi.common.xml.XmlEvaluator;
import uy.gub.agesic.pdi.common.xml.XmlNode;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GuardarButton;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuilderXpathForm extends BackofficeForm {
    private static final long serialVersionUID = 1L;

    String xpathBuildField;
    String labelXpathEvaluation;
    String dataXml;
    List<String> listXpaths;

    public BuilderXpathForm() {
        super("builderXpathForm");
        this.listXpaths = new ArrayList<>();
    }

    @Override
    public void initForm() {
        this.setDefaultModel(new CompoundPropertyModel<>(this));
        super.initForm();
        BackofficeProperties properties = ((BackofficePage) this.getPage()).getProperties();

        this.add(new BuilderXpathForm.EvaluateAllButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        final FormComponent<String> dataXmlField = new TextArea<>(XPATH_XML_NAME_FIELD);
        dataXmlField.setRequired(true);
        dataXmlField.setLabel(new Model("XML"));
        this.add(dataXmlField);

        final ListView listView = new ListView(XPATH_XMLPATHS_NAME_FIELD, new PropertyModel<>(this, "listXpaths")) {
            @Override
            protected void populateItem(ListItem item) {
                final String currentItem = (String)item.getModelObject();
                item.add(new Label("name", currentItem));

                try {
                    XPathEvaluatorHelper evaluator = new XPathEvaluatorHelper(dataXml);
                    item.add(new Label("value", evaluator.evaluate(currentItem, XPathConstants.STRING).toString()));
                }catch (IOException | SAXException | XPathExpressionException | ParserConfigurationException e) { }
            }
        };
        this.add(listView);

        final Label labelBuildXpathField = new Label(XPATH_LABEL_BUILD_NAME_FIELD, "Construir expresión xpath *");
        labelBuildXpathField.setOutputMarkupId(true);
        labelBuildXpathField.setOutputMarkupPlaceholderTag(true);
        labelBuildXpathField.setVisible(true);
        this.add(labelBuildXpathField);

        final FormComponent<String> buildXpathField = new TextField<String>(XPATH_BUILD_NAME_FIELD);
        buildXpathField.add(StringValidator.maximumLength(400));
        this.add(new ComponentCustomFeedbackPanel(XPATH_BUILD_FEED_NAME_FIELD, new ComponentFeedbackMessageFilter(buildXpathField)));
        buildXpathField.setLabel(new Model("Expresión xpath"));
        this.add(buildXpathField);

        this.add(new BuilderXpathForm.EvaluateExpressionButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        final Label labelEvaluationXpathField = new Label(XPATH_LABEL_EVALUATION_NAME_FIELD, "");
        labelEvaluationXpathField.setOutputMarkupId(true);
        labelEvaluationXpathField.setOutputMarkupPlaceholderTag(true);
        labelEvaluationXpathField.setVisible(true);
        this.add(labelEvaluationXpathField);
    }

    @Override
    public void setParametersInner(PageParameters parameters) { }

    private class EvaluateAllButton extends GuardarButton {
        public EvaluateAllButton(String message) {
            super(message, BuilderXpathForm.this);
        }

        @Override
        public boolean isVisible(){
            return true;
        }

        @Override
        public void ejecutar() {
            evaluateAll();
        }
    }

    protected void evaluateAll() {
        try {
            String dataXmlValue = this.dataXml;
            XmlNode xmlNode = XmlEvaluator.evaluate(dataXmlValue);
            this.listXpaths = xmlNode.xPathTraversal();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            this.showError("BuilderXPath.error.listxpaths");
        }
    }

    private class EvaluateExpressionButton extends BotonAccion {
        private BackofficeForm form;

        public EvaluateExpressionButton(String message) {
            super("btnEvaluateExpression", Error.class, false, message);
            this.form = BuilderXpathForm.this;
        }

        @Override
        public boolean poseePermisoEjecucion() {
            return true;
        }

        @Override
        public boolean isVisible(){
            return true;
        }

        @Override
        public void ejecutar() {
            evaluateExpression();
        }
    }

    protected void evaluateExpression() {
        try {
            if ( Strings.isNullOrEmpty(this.dataXml) || Strings.isNullOrEmpty(this.xpathBuildField) ) {
                this.showError("BuilderXPath.error.nullvalues.evaluate.expression");
                return;
            }

            XPathEvaluatorHelper evaluator = new XPathEvaluatorHelper(this.dataXml);
            this.get(XPATH_LABEL_EVALUATION_NAME_FIELD).setDefaultModelObject(evaluator.evaluate(this.xpathBuildField, XPathConstants.STRING).toString());

        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
            this.showError("BuilderXPath.error.evaluate.expression");
        }
    }
}
