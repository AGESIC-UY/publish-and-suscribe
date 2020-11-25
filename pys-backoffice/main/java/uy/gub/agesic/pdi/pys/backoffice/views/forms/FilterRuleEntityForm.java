package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.views.Error;
import uy.gub.agesic.pdi.pys.backoffice.views.PageFilterForm;
import uy.gub.agesic.pdi.pys.domain.Factor;
import uy.gub.agesic.pdi.pys.domain.Filter;
import uy.gub.agesic.pdi.pys.domain.FilterRule;
import uy.gub.agesic.pdi.pys.domain.FilterTopic;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterRuleEntityForm extends BackofficeForm {
    private static final long serialVersionUID = 1L;

    private ModoOperacion modoRule;
    private String filterName;
    private String filterRuleName;

    private String name;
    private String leftFactorValue;
    private String rightFactorValue;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class SelectOption implements Serializable {
        private String key;
        private String value;
    }

    private ChoiceRenderer<SelectOption> choiceRenderer = new ChoiceRenderer("value", "key");

    private final SelectOption[] OPERATOR_OPTIONS_KEY_VALUE = new SelectOption[] {
            new SelectOption(FilterRule.Operator.valueOf("GREATER").name(), "Mayor"),
            new SelectOption(FilterRule.Operator.valueOf("GREATEROREQUAL").name(), "Mayor o igual"),
            new SelectOption(FilterRule.Operator.valueOf("EQUAL").name(), "Igual"),
            new SelectOption(FilterRule.Operator.valueOf("MINOR").name(), "Menor"),
            new SelectOption(FilterRule.Operator.valueOf("LESSOREQUAL").name(), "Menor o igual"),
            new SelectOption(FilterRule.Operator.valueOf("CONTAINS").name(), "Contiene"),
    };
    private SelectOption selectedOperator = OPERATOR_OPTIONS_KEY_VALUE[2];

    private final SelectOption[] FACTOR_TYPE_OPTIONS_KEY_VALUE = new SelectOption[] {
            new SelectOption(Factor.Type.valueOf("XPATH").name(), "XPATH"),
            new SelectOption(Factor.Type.valueOf("SUBSCRIBER").name(), "Suscriptor"),
            new SelectOption(Factor.Type.valueOf("FIXEDTEXT").name(), "Texto fijo"),
            new SelectOption(Factor.Type.valueOf("LIST").name(), "Lista"),
    };
    private SelectOption selectedLeftFactorType = FACTOR_TYPE_OPTIONS_KEY_VALUE[0];
    private SelectOption selectedRightFactorType = FACTOR_TYPE_OPTIONS_KEY_VALUE[0];

    private final SelectOption[] VALUE_TYPE_OPTIONS_KEY_VALUE = new SelectOption[] {
            new SelectOption(Factor.ValueType.valueOf("TEXT").name(), "Texto"),
            new SelectOption(Factor.ValueType.valueOf("NUMBER").name(), "Numérico"),
            new SelectOption(Factor.ValueType.valueOf("DATETIME").name(), "Fecha y hora"),
    };
    private SelectOption selectedLeftValueType = VALUE_TYPE_OPTIONS_KEY_VALUE[0];
    private SelectOption selectedRightValueType = VALUE_TYPE_OPTIONS_KEY_VALUE[0];

    private final SelectOption[] SUBSCRIBER_PROPERTIES = new SelectOption[] {
            new SelectOption("dn", "Rol"),
            new SelectOption("fechaCreacion", "Fecha de Creación"),
            new SelectOption("nombre", "Nombre"),
    };
    private SelectOption selectedLeftSubscriberProperty = SUBSCRIBER_PROPERTIES[0];
    private SelectOption selectedRightSubscriberProperty = SUBSCRIBER_PROPERTIES[0];

    private static final List<String> FORMATS_DATETIME = Stream.of(
            "yyyyMMddHHmmss",
            "yyyy-MM-dd HHmmss",
            "yyyy-MM-dd",
            "dd-MM-yyyyHHmmss",
            "dd-MM-yyyy HHmmss",
            "dd-MM-yyyy",
            "HHmmss",
            "HH:mm:ss",
            "HH:mm"
    ).collect(Collectors.toList());
    private String selectedLeftFormatDatetime = "yyyyMMddHHmmss";
    private String selectedRightFormatDatetime = "yyyyMMddHHmmss";

    private static final List<String> FORMATS_NUMERIC = Stream.of(
            "Integer",
            "Double"
    ).collect(Collectors.toList());
    private String selectedLeftFormatNumeric = "Integer";
    private String selectedRightFormatNumeric = "Integer";

    public FilterRuleEntityForm() {
        super("filterRuleEntityForm");
    }

    @Override
    public void initForm() {
        this.setDefaultModel(new CompoundPropertyModel<>(this));
        super.initForm();
        BackofficeProperties properties = ((BackofficePage) this.getPage()).getProperties();

        this.add(new FilterRuleEntityForm.AddFilterRuleButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        this.filterName = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(FILTER_PARAM_NAME).toString();

        if (RequestCycle.get().getRequest().getRequestParameters().getParameterNames().contains (FILER_MODE_RULE_PARAM_NAME)) {
            this.modoRule = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(FILER_MODE_RULE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        final Label labelNameField = new Label(FILTER_RULE_LABEL_NAME_FIELD, "Nombre *");
        labelNameField.setOutputMarkupId(true);
        labelNameField.setOutputMarkupPlaceholderTag(true);
        labelNameField.setVisible(true);
        this.add(labelNameField);

        final FormComponent<String> nameField = new TextField<String>(FILTER_RULE_NAME_FIELD).setRequired(true);
        nameField.add(StringValidator.maximumLength(250));
        this.add(new ComponentCustomFeedbackPanel(FILTER_RULE_NAME_FEED_FIELD, new ComponentFeedbackMessageFilter(nameField)));
        nameField.setLabel(new Model("Nombre"));
        this.add(nameField);

        final Label labelSelectOperatorField = new Label(FILTER_RULE_LABEL_OPERATOR_FIELD, "Operador *");
        labelSelectOperatorField.setOutputMarkupId(true);
        labelSelectOperatorField.setOutputMarkupPlaceholderTag(true);
        labelSelectOperatorField.setVisible(true);
        this.add(labelSelectOperatorField);

        final DropDownChoice<String> selectOperatorChoiceField = new DropDownChoice(
                FILTER_RULE_OPERATOR_FIELD, new PropertyModel<>(this, "selectedOperator"), Arrays.asList(OPERATOR_OPTIONS_KEY_VALUE), choiceRenderer);
        selectOperatorChoiceField.setNullValid(false);
        this.add(selectOperatorChoiceField);

        final Label labelLeftFactorValue = new Label(FILTER_RULE_LABEL_LEFTFACTORVALUE_FIELD, "Valor *");
        labelLeftFactorValue.setOutputMarkupId(true);
        labelLeftFactorValue.setOutputMarkupPlaceholderTag(true);
        labelLeftFactorValue.setVisible(true);
        this.add(labelLeftFactorValue);

        final FormComponent<String> leftFactorValueField = new TextField<String>(FILTER_RULE_LEFTFACTORVALUE_FIELD).setRequired(true);
        leftFactorValueField.add(StringValidator.maximumLength(250));
        leftFactorValueField.setOutputMarkupId(true);
        leftFactorValueField.setOutputMarkupPlaceholderTag(true);
        leftFactorValueField.setLabel(new Model("Valor"));
        final ComponentCustomFeedbackPanel leftFactorValueFeedback = new ComponentCustomFeedbackPanel(FILTER_RULE_LEFTFACTORVALUE_FEED_FIELD, new ComponentFeedbackMessageFilter(leftFactorValueField));
        leftFactorValueFeedback.setOutputMarkupId(true);
        leftFactorValueFeedback.setOutputMarkupPlaceholderTag(true);
        this.add(leftFactorValueFeedback);
        this.add(leftFactorValueField);

        final ModalWindow modalWindowLeft = new ModalWindow(FILTER_RULE_MODAL_LEFT);
        this.add(modalWindowLeft);
        modalWindowLeft.setOutputMarkupId(true);
        modalWindowLeft.setOutputMarkupPlaceholderTag(true);
        modalWindowLeft.setVisible(false);
        final ModalSubscribers modalSubscribersLeft = new ModalSubscribers(modalWindowLeft.getContentId(), FilterRuleEntityForm.this, modalWindowLeft, true);
        modalWindowLeft.setContent(modalSubscribersLeft);
        modalWindowLeft.setTitle("Generar lista de subcriptores");
        modalWindowLeft.setCookieName("modal-subscribers-left");
        modalWindowLeft.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                return true;
            }
        });
        modalWindowLeft.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                target.add(leftFactorValueField);
            }
        });
        AjaxLink modalLeftLink = new AjaxLink(FILTER_RULE_MODAL_LEFT_LINK) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalWindowLeft.show(target);
            }
        };
        modalLeftLink.setOutputMarkupId(true);
        modalLeftLink.setOutputMarkupPlaceholderTag(true);
        modalLeftLink.setVisible(false);
        this.add(modalLeftLink);

        final Label labelLeftFactorSubscriber = new Label(FILTER_RULE_LABEL_LEFTFACTORSUBSCRIBER_FIELD, "Suscriptor *");
        labelLeftFactorSubscriber.setOutputMarkupId(true);
        labelLeftFactorSubscriber.setOutputMarkupPlaceholderTag(true);
        labelLeftFactorSubscriber.setVisible(false);
        this.add(labelLeftFactorSubscriber);

        final DropDownChoice<String> selectLeftFactorSubscriberChoiceField = new DropDownChoice(
                FILTER_RULE_LEFTFACTORSUBSCRIBER_FIELD, new PropertyModel<String>(
                this,
                "selectedLeftSubscriberProperty"
        ), Arrays.asList(SUBSCRIBER_PROPERTIES), choiceRenderer);

        selectLeftFactorSubscriberChoiceField.setNullValid(false);
        selectLeftFactorSubscriberChoiceField.setVisible(false);
        selectLeftFactorSubscriberChoiceField.setOutputMarkupId(true);
        selectLeftFactorSubscriberChoiceField.setOutputMarkupPlaceholderTag(true);
        selectLeftFactorSubscriberChoiceField.setLabel(new Model("Suscriptor"));
        this.add(selectLeftFactorSubscriberChoiceField);

        final Label labelLeftFactorValueFormatDatetime = new Label(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_DATETIME_FIELD, "Formato *");
        labelLeftFactorValueFormatDatetime.setOutputMarkupId(true);
        labelLeftFactorValueFormatDatetime.setOutputMarkupPlaceholderTag(true);
        labelLeftFactorValueFormatDatetime.setVisible(false);
        this.add(labelLeftFactorValueFormatDatetime);

        final DropDownChoice<String> selectLeftFactorValueFormatDatetimeChoiceField = new DropDownChoice<>(
                FILTER_RULE_LEFTFACTORVALUEFORMAT_DATETIME_FIELD, new PropertyModel<>(this, "selectedLeftFormatDatetime"), FORMATS_DATETIME);
        selectLeftFactorValueFormatDatetimeChoiceField.setNullValid(false);
        selectLeftFactorValueFormatDatetimeChoiceField.setVisible(false);
        selectLeftFactorValueFormatDatetimeChoiceField.setOutputMarkupId(true);
        selectLeftFactorValueFormatDatetimeChoiceField.setOutputMarkupPlaceholderTag(true);
        selectLeftFactorValueFormatDatetimeChoiceField.setLabel(new Model("Formato"));
        this.add(selectLeftFactorValueFormatDatetimeChoiceField);

        final Label labelLeftFactorValueFormatNumeric = new Label(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD, "Formato *");
        labelLeftFactorValueFormatNumeric.setOutputMarkupId(true);
        labelLeftFactorValueFormatNumeric.setOutputMarkupPlaceholderTag(true);
        labelLeftFactorValueFormatNumeric.setVisible(false);
        this.add(labelLeftFactorValueFormatNumeric);

        final DropDownChoice<String> selectLeftFactorValueFormatNumericChoiceField = new DropDownChoice<>(
                FILTER_RULE_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD, new PropertyModel<>(this, "selectedLeftFormatNumeric"), FORMATS_NUMERIC);
        selectLeftFactorValueFormatNumericChoiceField.setNullValid(false);
        selectLeftFactorValueFormatNumericChoiceField.setVisible(false);
        selectLeftFactorValueFormatNumericChoiceField.setOutputMarkupId(true);
        selectLeftFactorValueFormatNumericChoiceField.setOutputMarkupPlaceholderTag(true);
        selectLeftFactorValueFormatNumericChoiceField.setLabel(new Model("Formato"));
        this.add(selectLeftFactorValueFormatNumericChoiceField);

        final Label labelSelectLeftValueType = new Label(FILTER_RULE_LABEL_LEFTFACTORVALUETYPE_FIELD, "Tipo de valor *");
        labelSelectLeftValueType.setOutputMarkupId(true);
        labelSelectLeftValueType.setOutputMarkupPlaceholderTag(true);
        labelSelectLeftValueType.setVisible(true);
        this.add(labelSelectLeftValueType);

        final DropDownChoice<String> selectLeftValueTypeChoiceField = new DropDownChoice(
                FILTER_RULE_LEFTFACTORVALUETYPE_FIELD, new PropertyModel<String>(
                        this,
                "selectedLeftValueType"
        ), Arrays.asList(VALUE_TYPE_OPTIONS_KEY_VALUE), choiceRenderer);
        selectLeftValueTypeChoiceField.setNullValid(false);
        selectLeftValueTypeChoiceField.setVisible(true);
        selectLeftValueTypeChoiceField.setOutputMarkupId(true);
        selectLeftValueTypeChoiceField.setOutputMarkupPlaceholderTag(true);
        this.add(selectLeftValueTypeChoiceField);
        selectLeftValueTypeChoiceField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (target != null) {
                    target.add(labelLeftFactorValueFormatNumeric);
                    target.add(selectLeftFactorValueFormatNumericChoiceField);
                    target.add(labelLeftFactorValueFormatDatetime);
                    target.add(selectLeftFactorValueFormatDatetimeChoiceField);
                    target.add(modalWindowLeft);
                    target.add(modalLeftLink);

                    modalWindowLeft.setVisible(false);
                    modalLeftLink.setVisible(false);

                    if (Factor.ValueType.valueOf(selectedLeftValueType.getKey()) == Factor.ValueType.TEXT) {
                        labelLeftFactorValueFormatNumeric.setVisible(false);
                        selectLeftFactorValueFormatNumericChoiceField.setVisible(false);
                        labelLeftFactorValueFormatDatetime.setVisible(false);
                        selectLeftFactorValueFormatDatetimeChoiceField.setVisible(false);

                        if (Factor.Type.valueOf(selectedLeftFactorType.getKey()) == Factor.Type.LIST) {
                            modalWindowLeft.setVisible(true);
                            modalLeftLink.setVisible(true);
                        }
                    } else if (Factor.ValueType.valueOf(selectedLeftValueType.getKey()) == Factor.ValueType.DATETIME) {
                        labelLeftFactorValueFormatNumeric.setVisible(false);
                        selectLeftFactorValueFormatNumericChoiceField.setVisible(false);
                        labelLeftFactorValueFormatDatetime.setVisible(true);
                        selectLeftFactorValueFormatDatetimeChoiceField.setVisible(true);
                    } else {
                        labelLeftFactorValueFormatNumeric.setVisible(true);
                        selectLeftFactorValueFormatNumericChoiceField.setVisible(true);
                        labelLeftFactorValueFormatDatetime.setVisible(false);
                        selectLeftFactorValueFormatDatetimeChoiceField.setVisible(false);
                    }
                }
            }
        });

        final Label labelSelectLeftFactorType = new Label(FILTER_RULE_LABEL_LEFTFACTORTYPE_FIELD, "Tipo *");
        labelSelectLeftFactorType.setOutputMarkupId(true);
        labelSelectLeftFactorType.setOutputMarkupPlaceholderTag(true);
        labelSelectLeftFactorType.setVisible(true);
        this.add(labelSelectLeftFactorType);

        final DropDownChoice<String> selectLeftFactorTypeChoiceField = new DropDownChoice(
                FILTER_RULE_LEFTFACTORTYPE_FIELD, new PropertyModel(this, "selectedLeftFactorType"), Arrays.asList(FACTOR_TYPE_OPTIONS_KEY_VALUE), this.choiceRenderer);
        selectLeftFactorTypeChoiceField.setNullValid(false);
        selectLeftFactorTypeChoiceField.setLabel(new Model("Tipo"));
        this.add(selectLeftFactorTypeChoiceField);
        selectLeftFactorTypeChoiceField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (target != null) {
                    target.add(labelLeftFactorValue);
                    target.add(leftFactorValueField);
                    target.add(leftFactorValueFeedback);
                    target.add(labelLeftFactorSubscriber);
                    target.add(selectLeftFactorSubscriberChoiceField);
                    target.add(labelSelectLeftValueType);
                    target.add(selectLeftValueTypeChoiceField);

                    target.add(labelLeftFactorValueFormatDatetime);
                    target.add(selectLeftFactorValueFormatDatetimeChoiceField);
                    target.add(labelLeftFactorValueFormatNumeric);
                    target.add(selectLeftFactorValueFormatNumericChoiceField);

                    target.add(modalWindowLeft);
                    target.add(modalLeftLink);

                    modalWindowLeft.setVisible(false);
                    modalLeftLink.setVisible(false);

                    if (Factor.Type.valueOf(selectedLeftFactorType.getKey()) == Factor.Type.SUBSCRIBER) {
                        labelLeftFactorValue.setVisible(false);
                        leftFactorValueField.setVisible(false);
                        leftFactorValueFeedback.setVisible(false);
                        labelLeftFactorSubscriber.setVisible(true);
                        selectLeftFactorSubscriberChoiceField.setVisible(true);
                        labelSelectLeftValueType.setVisible(false);
                        selectLeftValueTypeChoiceField.setVisible(false);

                        labelLeftFactorValueFormatDatetime.setVisible(false);
                        selectLeftFactorValueFormatDatetimeChoiceField.setVisible(false);
                        labelLeftFactorValueFormatNumeric.setVisible(false);
                        selectLeftFactorValueFormatNumericChoiceField.setVisible(false);
                    } else {
                        labelLeftFactorValue.setVisible(true);
                        leftFactorValueField.setVisible(true);
                        leftFactorValueFeedback.setVisible(true);
                        labelLeftFactorSubscriber.setVisible(false);
                        selectLeftFactorSubscriberChoiceField.setVisible(false);
                        labelSelectLeftValueType.setVisible(true);
                        selectLeftValueTypeChoiceField.setVisible(true);

                        labelLeftFactorValueFormatDatetime.setVisible(false);
                        selectLeftFactorValueFormatDatetimeChoiceField.setVisible(false);
                        labelLeftFactorValueFormatNumeric.setVisible(false);
                        selectLeftFactorValueFormatNumericChoiceField.setVisible(false);

                        selectedLeftValueType = VALUE_TYPE_OPTIONS_KEY_VALUE[0];

                        if (Factor.Type.valueOf(selectedLeftFactorType.getKey()) == Factor.Type.LIST) {
                            modalWindowLeft.setVisible(true);
                            modalLeftLink.setVisible(true);
                        }
                    }
                }
            }
        });

        final Label labelRightFactorValue = new Label(FILTER_RULE_LABEL_RIGHTFACTORVALUE_FIELD, "Valor *");
        labelRightFactorValue.setOutputMarkupId(true);
        labelRightFactorValue.setOutputMarkupPlaceholderTag(true);
        labelRightFactorValue.setVisible(true);
        this.add(labelRightFactorValue);

        final FormComponent<String> rightFactorValueField = new TextField<String>(FILTER_RULE_RIGHTFACTORVALUE_FIELD).setRequired(true);
        rightFactorValueField.add(StringValidator.maximumLength(250));
        rightFactorValueField.setOutputMarkupId(true);
        rightFactorValueField.setOutputMarkupPlaceholderTag(true);
        rightFactorValueField.setLabel(new Model("Valor"));
        final ComponentCustomFeedbackPanel rightFactorValueFeedback = new ComponentCustomFeedbackPanel(FILTER_RULE_RIGHTFACTORVALUE_FEED_FIELD, new ComponentFeedbackMessageFilter(rightFactorValueField));
        rightFactorValueFeedback.setOutputMarkupId(true);
        rightFactorValueFeedback.setOutputMarkupPlaceholderTag(true);
        this.add(rightFactorValueFeedback);
        this.add(rightFactorValueField);

        final Label labelRightFactorSubscriber = new Label(FILTER_RULE_LABEL_RIGHTFACTORSUBSCRIBER_FIELD, "Suscriptor *");
        labelRightFactorSubscriber.setOutputMarkupId(true);
        labelRightFactorSubscriber.setOutputMarkupPlaceholderTag(true);
        labelRightFactorSubscriber.setVisible(false);
        this.add(labelRightFactorSubscriber);

        final ModalWindow modalWindowRight = new ModalWindow(FILTER_RULE_MODAL_RIGHT);
        this.add(modalWindowRight);
        modalWindowRight.setOutputMarkupId(true);
        modalWindowRight.setOutputMarkupPlaceholderTag(true);
        modalWindowRight.setVisible(false);
        final ModalSubscribers modalSubscribersRight = new ModalSubscribers(modalWindowRight.getContentId(), FilterRuleEntityForm.this, modalWindowRight, false);
        modalWindowRight.setContent(modalSubscribersRight);
        modalWindowRight.setTitle("Generar lista de subcriptores");
        modalWindowRight.setCookieName("modal-subscribers-right");
        modalWindowRight.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                return true;
            }
        });
        modalWindowRight.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                target.add(rightFactorValueField);
            }
        });
        AjaxLink modalRightLink = new AjaxLink(FILTER_RULE_MODAL_RIGHT_LINK) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalWindowRight.show(target);
            }
        };
        modalRightLink.setOutputMarkupId(true);
        modalRightLink.setOutputMarkupPlaceholderTag(true);
        modalRightLink.setVisible(false);
        this.add(modalRightLink);

        final DropDownChoice<String> selectRightFactorSubscriberChoiceField = new DropDownChoice(
                FILTER_RULE_RIGHTFACTORSUBSCRIBER_FIELD, new PropertyModel<String>(
                this,
                "selectedRightSubscriberProperty"
        ), Arrays.asList(SUBSCRIBER_PROPERTIES), choiceRenderer);

        selectRightFactorSubscriberChoiceField.setNullValid(false);
        selectRightFactorSubscriberChoiceField.setVisible(false);
        selectRightFactorSubscriberChoiceField.setOutputMarkupId(true);
        selectRightFactorSubscriberChoiceField.setOutputMarkupPlaceholderTag(true);
        selectRightFactorSubscriberChoiceField.setLabel(new Model("Suscriptor"));
        this.add(selectRightFactorSubscriberChoiceField);

        final Label labelRightFactorValueFormatDatetime = new Label(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD, "Formato *");
        labelRightFactorValueFormatDatetime.setOutputMarkupId(true);
        labelRightFactorValueFormatDatetime.setOutputMarkupPlaceholderTag(true);
        labelRightFactorValueFormatDatetime.setVisible(false);
        this.add(labelRightFactorValueFormatDatetime);

        final DropDownChoice<String> selectRightFactorValueFormatDatetimeChoiceField = new DropDownChoice<>(
                FILTER_RULE_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD, new PropertyModel<>(this, "selectedRightFormatDatetime"), FORMATS_DATETIME);
        selectRightFactorValueFormatDatetimeChoiceField.setNullValid(false);
        selectRightFactorValueFormatDatetimeChoiceField.setVisible(false);
        selectRightFactorValueFormatDatetimeChoiceField.setOutputMarkupId(true);
        selectRightFactorValueFormatDatetimeChoiceField.setOutputMarkupPlaceholderTag(true);
        selectRightFactorValueFormatDatetimeChoiceField.setLabel(new Model("Formato"));
        this.add(selectRightFactorValueFormatDatetimeChoiceField);

        final Label labelRightFactorValueFormatNumeric = new Label(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD, "Formato *");
        labelRightFactorValueFormatNumeric.setOutputMarkupId(true);
        labelRightFactorValueFormatNumeric.setOutputMarkupPlaceholderTag(true);
        labelRightFactorValueFormatNumeric.setVisible(false);
        this.add(labelRightFactorValueFormatNumeric);

        final DropDownChoice<String> selectRightFactorValueFormatNumericChoiceField = new DropDownChoice<>(
                FILTER_RULE_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD, new PropertyModel<>(this, "selectedRightFormatNumeric"), FORMATS_NUMERIC);
        selectRightFactorValueFormatNumericChoiceField.setNullValid(false);
        selectRightFactorValueFormatNumericChoiceField.setVisible(false);
        selectRightFactorValueFormatNumericChoiceField.setOutputMarkupId(true);
        selectRightFactorValueFormatNumericChoiceField.setOutputMarkupPlaceholderTag(true);
        selectRightFactorValueFormatNumericChoiceField.setLabel(new Model("Formato"));
        this.add(selectRightFactorValueFormatNumericChoiceField);

        final Label labelSelectRightValueType = new Label(FILTER_RULE_LABEL_RIGHTFACTORVALUETYPE_FIELD, "Tipo de valor *");
        labelSelectRightValueType.setOutputMarkupId(true);
        labelSelectRightValueType.setOutputMarkupPlaceholderTag(true);
        labelSelectRightValueType.setVisible(true);
        this.add(labelSelectRightValueType);

        final DropDownChoice<String> selectRightValueTypeChoiceField = new DropDownChoice(
                FILTER_RULE_RIGHTFACTORVALUETYPE_FIELD, new PropertyModel<>(this, "selectedRightValueType"), Arrays.asList(VALUE_TYPE_OPTIONS_KEY_VALUE), choiceRenderer);
        selectRightValueTypeChoiceField.setNullValid(false);
        selectRightValueTypeChoiceField.setVisible(true);
        selectRightValueTypeChoiceField.setOutputMarkupId(true);
        selectRightValueTypeChoiceField.setOutputMarkupPlaceholderTag(true);
        this.add(selectRightValueTypeChoiceField);
        selectRightValueTypeChoiceField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (target != null) {
                    target.add(labelRightFactorValueFormatNumeric);
                    target.add(selectRightFactorValueFormatNumericChoiceField);
                    target.add(labelRightFactorValueFormatDatetime);
                    target.add(selectRightFactorValueFormatDatetimeChoiceField);
                    target.add(modalWindowRight);
                    target.add(modalRightLink);

                    modalWindowRight.setVisible(false);
                    modalRightLink.setVisible(false);

                    if (Factor.ValueType.valueOf(selectedRightValueType.getKey()) == Factor.ValueType.TEXT) {
                        labelRightFactorValueFormatNumeric.setVisible(false);
                        selectRightFactorValueFormatNumericChoiceField.setVisible(false);
                        labelRightFactorValueFormatDatetime.setVisible(false);
                        selectRightFactorValueFormatDatetimeChoiceField.setVisible(false);

                        if (Factor.Type.valueOf(selectedRightFactorType.getKey()) == Factor.Type.LIST) {
                            modalWindowRight.setVisible(true);
                            modalRightLink.setVisible(true);
                        }
                    }  else if (Factor.ValueType.valueOf(selectedRightValueType.getKey()) == Factor.ValueType.DATETIME) {
                        labelRightFactorValueFormatNumeric.setVisible(false);
                        selectRightFactorValueFormatNumericChoiceField.setVisible(false);
                        labelRightFactorValueFormatDatetime.setVisible(true);
                        selectRightFactorValueFormatDatetimeChoiceField.setVisible(true);
                    } else {
                        labelRightFactorValueFormatNumeric.setVisible(true);
                        selectRightFactorValueFormatNumericChoiceField.setVisible(true);
                        labelRightFactorValueFormatDatetime.setVisible(false);
                        selectRightFactorValueFormatDatetimeChoiceField.setVisible(false);
                    }
                }
            }
        });

        final Label labelSelectRightFactorType = new Label(FILTER_RULE_LABEL_RIGHTFACTORTYPE_FIELD, "Tipo *");
        labelSelectRightFactorType.setOutputMarkupId(true);
        labelSelectRightFactorType.setOutputMarkupPlaceholderTag(true);
        labelSelectRightFactorType.setVisible(true);
        this.add(labelSelectRightFactorType);

        final DropDownChoice<String> selectRightFactorTypeChoiceField = new DropDownChoice(
                FILTER_RULE_RIGHTFACTORTYPE_FIELD, new PropertyModel<>(this, "selectedRightFactorType"), Arrays.asList(FACTOR_TYPE_OPTIONS_KEY_VALUE), this.choiceRenderer);
        selectRightFactorTypeChoiceField.setNullValid(false);
        selectRightFactorTypeChoiceField.setLabel(new Model("Tipo"));
        this.add(selectRightFactorTypeChoiceField);
        selectRightFactorTypeChoiceField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (target != null) {
                    target.add(labelRightFactorValue);
                    target.add(rightFactorValueField);
                    target.add(rightFactorValueFeedback);
                    target.add(labelRightFactorSubscriber);
                    target.add(selectRightFactorSubscriberChoiceField);
                    target.add(labelSelectRightValueType);
                    target.add(selectRightValueTypeChoiceField);

                    target.add(labelRightFactorValueFormatDatetime);
                    target.add(selectRightFactorValueFormatDatetimeChoiceField);
                    target.add(labelRightFactorValueFormatNumeric);
                    target.add(selectRightFactorValueFormatNumericChoiceField);

                    target.add(modalWindowRight);
                    target.add(modalRightLink);

                    modalWindowRight.setVisible(false);
                    modalRightLink.setVisible(false);

                    if (Factor.Type.valueOf(selectedRightFactorType.getKey()) == Factor.Type.SUBSCRIBER) {
                        labelRightFactorValue.setVisible(false);
                        rightFactorValueField.setVisible(false);
                        rightFactorValueFeedback.setVisible(false);
                        labelRightFactorSubscriber.setVisible(true);
                        selectRightFactorSubscriberChoiceField.setVisible(true);
                        labelSelectRightValueType.setVisible(false);
                        selectRightValueTypeChoiceField.setVisible(false);

                        labelRightFactorValueFormatDatetime.setVisible(false);
                        selectRightFactorValueFormatDatetimeChoiceField.setVisible(false);
                        labelRightFactorValueFormatNumeric.setVisible(false);
                        selectRightFactorValueFormatNumericChoiceField.setVisible(false);
                    } else {
                        labelRightFactorValue.setVisible(true);
                        rightFactorValueField.setVisible(true);
                        rightFactorValueFeedback.setVisible(true);
                        labelRightFactorSubscriber.setVisible(false);
                        selectRightFactorSubscriberChoiceField.setVisible(false);
                        labelSelectRightValueType.setVisible(true);
                        selectRightValueTypeChoiceField.setVisible(true);

                        labelRightFactorValueFormatDatetime.setVisible(false);
                        selectRightFactorValueFormatDatetimeChoiceField.setVisible(false);
                        labelRightFactorValueFormatNumeric.setVisible(false);
                        selectRightFactorValueFormatNumericChoiceField.setVisible(false);

                        selectedRightValueType = VALUE_TYPE_OPTIONS_KEY_VALUE[0];

                        if (Factor.Type.valueOf(selectedRightFactorType.getKey()) == Factor.Type.LIST) {
                            modalWindowRight.setVisible(true);
                            modalRightLink.setVisible(true);
                        }
                    }
                }
            }
        });

        this.add(new FilterRuleEntityForm.LinkVolver());
    }

    private class LinkVolver extends StatelessLink {
        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {
            parametersCallback.set(MODE_PARAM_NAME, modo);
            parametersCallback.set(FILTER_PARAM_NAME, filterName);
            parametersCallback.set(FILTER_RULE_PARAM_NAME, filterRuleName);
            parametersCallback.set(FILER_MODE_RULE_PARAM_NAME, modoRule);

            setResponsePage(PageFilterForm.class, parametersCallback);
        }

        public PageParameters getParametersCallback() {
            return parametersCallback;
        }

        public void setParametersCallback(PageParameters parametersCallback) {
            this.parametersCallback = parametersCallback;
        }
    }

    private void initializeValues() {
        Optional<Filter> filterOptional = getFilterService().getFilter(this.filterName);
        if (filterOptional.isPresent()) {
            Filter currentFilter = filterOptional.get();

            List<FilterRule> rulesList = currentFilter.getRules();
            if(rulesList == null)
                rulesList = new ArrayList<>();

            FilterRule filterRule = new FilterRule();

            for (FilterRule rule : rulesList) {
                if (rule.getName().equals(this.filterRuleName)) {
                    filterRule = rule;
                    break;
                }
            }

            this.name = filterRule.getName();

            this.get(FILTER_RULE_MODAL_LEFT).setVisible(false);
            this.get(FILTER_RULE_MODAL_LEFT_LINK).setVisible(false);

            Factor leftFactor = filterRule.getLeftFactor();
            if (leftFactor.getType() == Factor.Type.SUBSCRIBER) {
                this.selectedLeftSubscriberProperty = Arrays.asList(SUBSCRIBER_PROPERTIES)
                        .stream()
                        .filter(options -> leftFactor.getValue().equals(options.getKey()))
                        .findAny()
                        .orElse(SUBSCRIBER_PROPERTIES[0]);
                this.leftFactorValue = "";

                this.get(FILTER_RULE_LABEL_LEFTFACTORSUBSCRIBER_FIELD).setVisible(true);
                this.get(FILTER_RULE_LEFTFACTORSUBSCRIBER_FIELD).setVisible(true);
                this.get(FILTER_RULE_LABEL_LEFTFACTORVALUE_FIELD).setVisible(false);
                this.get(FILTER_RULE_LEFTFACTORVALUE_FIELD).setVisible(false);
                this.get(FILTER_RULE_LEFTFACTORVALUE_FEED_FIELD).setVisible(false);

                this.get(FILTER_RULE_LABEL_LEFTFACTORVALUETYPE_FIELD).setVisible(false);
                this.get(FILTER_RULE_LEFTFACTORVALUETYPE_FIELD).setVisible(false);

                this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
            } else {
                this.leftFactorValue = leftFactor.getValue();
                this.selectedLeftSubscriberProperty = SUBSCRIBER_PROPERTIES[0];

                this.get(FILTER_RULE_LABEL_LEFTFACTORVALUETYPE_FIELD).setVisible(true);
                this.get(FILTER_RULE_LEFTFACTORVALUETYPE_FIELD).setVisible(true);

                if (leftFactor.getValueType() == Factor.ValueType.TEXT) {
                    this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);

                    if (leftFactor.getType() == Factor.Type.LIST) {
                        this.get(FILTER_RULE_MODAL_LEFT).setVisible(true);
                        this.get(FILTER_RULE_MODAL_LEFT_LINK).setVisible(true);
                    }
                } else if (leftFactor.getValueType() == Factor.ValueType.DATETIME) {
                    this.selectedLeftFormatDatetime = leftFactor.getValueFormat();

                    this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(true);
                    this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(true);
                } else {
                    this.selectedLeftFormatNumeric = leftFactor.getValueFormat();

                    this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(true);
                    this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(true);
                    this.get(FILTER_RULE_LABEL_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                }
            }

            Factor rightFactor = filterRule.getRightFactor();
            if (rightFactor.getType() == Factor.Type.SUBSCRIBER) {
                this.selectedRightSubscriberProperty = Arrays.asList(SUBSCRIBER_PROPERTIES)
                        .stream()
                        .filter(options -> rightFactor.getValue().equals(options.getKey()))
                        .findAny()
                        .orElse(SUBSCRIBER_PROPERTIES[0]);

                this.rightFactorValue = "";

                this.get(FILTER_RULE_LABEL_RIGHTFACTORSUBSCRIBER_FIELD).setVisible(true);
                this.get(FILTER_RULE_RIGHTFACTORSUBSCRIBER_FIELD).setVisible(true);
                this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUE_FIELD).setVisible(false);
                this.get(FILTER_RULE_RIGHTFACTORVALUE_FIELD).setVisible(false);
                this.get(FILTER_RULE_RIGHTFACTORVALUE_FEED_FIELD).setVisible(false);

                this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUETYPE_FIELD).setVisible(false);
                this.get(FILTER_RULE_RIGHTFACTORVALUETYPE_FIELD).setVisible(false);

                this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
            } else {
                this.rightFactorValue = rightFactor.getValue();
                this.selectedRightSubscriberProperty = SUBSCRIBER_PROPERTIES[0];

                this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUETYPE_FIELD).setVisible(true);
                this.get(FILTER_RULE_RIGHTFACTORVALUETYPE_FIELD).setVisible(true);

                if (rightFactor.getValueType() == Factor.ValueType.TEXT) {
                    this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                    this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);

                    if (rightFactor.getType() == Factor.Type.LIST) {
                        this.get(FILTER_RULE_MODAL_RIGHT).setVisible(true);
                        this.get(FILTER_RULE_MODAL_RIGHT_LINK).setVisible(true);
                    }
                } else if (rightFactor.getValueType() == Factor.ValueType.DATETIME) {
                    this.selectedRightFormatDatetime = rightFactor.getValueFormat();

                    this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(false);
                    this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(true);
                    this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(true);
                } else {
                    this.selectedRightFormatNumeric = rightFactor.getValueFormat();

                    this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(true);
                    this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).setVisible(true);
                    this.get(FILTER_RULE_LABEL_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                    this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).setVisible(false);
                }
            }

            FilterRule finalFilterRule = filterRule;
            this.selectedOperator = Arrays.asList(OPERATOR_OPTIONS_KEY_VALUE)
                    .stream()
                    .filter(options -> finalFilterRule.getOperator().name().equals(options.getKey()))
                    .findAny()
                    .orElse(OPERATOR_OPTIONS_KEY_VALUE[2]);

            this.selectedLeftFactorType = Arrays.asList(FACTOR_TYPE_OPTIONS_KEY_VALUE)
                    .stream()
                    .filter(options -> leftFactor.getType().toString().equals(options.getKey()))
                    .findAny()
                    .orElse(FACTOR_TYPE_OPTIONS_KEY_VALUE[0]);

            this.selectedRightFactorType = Arrays.asList(FACTOR_TYPE_OPTIONS_KEY_VALUE)
                    .stream()
                    .filter(options -> rightFactor.getType().toString().equals(options.getKey()))
                    .findAny()
                    .orElse(FACTOR_TYPE_OPTIONS_KEY_VALUE[0]);

            this.selectedLeftValueType = Arrays.asList(VALUE_TYPE_OPTIONS_KEY_VALUE)
                    .stream()
                    .filter(options -> leftFactor.getValueType().toString().equals(options.getKey()))
                    .findAny()
                    .orElse(VALUE_TYPE_OPTIONS_KEY_VALUE[0]);

            this.selectedRightValueType = Arrays.asList(VALUE_TYPE_OPTIONS_KEY_VALUE)
                    .stream()
                    .filter(options -> rightFactor.getValueType().toString().equals(options.getKey()))
                    .findAny()
                    .orElse(VALUE_TYPE_OPTIONS_KEY_VALUE[0]);
        }
    }

    public void updateLeftValue(String list) {
        TextField textField = (TextField) this.get(FILTER_RULE_LEFTFACTORVALUE_FIELD);
        this.get(FILTER_RULE_LEFTFACTORVALUE_FIELD).detachModels();
        textField.setModelValue(new String[]{list});
        this.leftFactorValue = list;
    }

    public void updateRightValue(String list) {
        TextField textField = (TextField) this.get(FILTER_RULE_RIGHTFACTORVALUE_FIELD);
        this.get(FILTER_RULE_RIGHTFACTORVALUE_FIELD).detachModels();
        textField.setModelValue(new String[]{list});
        this.rightFactorValue = list;
    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(FILER_MODE_RULE_PARAM_NAME)) {
            this.modoRule = parameters.get(FILER_MODE_RULE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(FILTER_PARAM_NAME)) {
            this.filterName = parameters.get(FILTER_PARAM_NAME).toString();
        }
        
        if (parameters.getNamedKeys().contains(FILTER_RULE_PARAM_NAME)) {
            this.filterRuleName = parameters.get(FILTER_RULE_PARAM_NAME).toString();
        }

        if (!this.modoRule.equals(ModoOperacion.ALTA)) {
            this.initializeValues();
        }

        if (this.modoRule.equals(ModoOperacion.CONSULTA)) {
            this.get(FILTER_RULE_NAME_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_OPERATOR_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_LEFTFACTORTYPE_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_LEFTFACTORVALUE_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_LEFTFACTORVALUETYPE_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_NUMERIC_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_RIGHTFACTORTYPE_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_RIGHTFACTORVALUE_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_RIGHTFACTORVALUETYPE_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_NUMERIC_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_LEFTFACTORSUBSCRIBER_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_RIGHTFACTORSUBSCRIBER_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_LEFTFACTORVALUEFORMAT_DATETIME_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_RIGHTFACTORVALUEFORMAT_DATETIME_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_RULE_MODAL_LEFT).setVisible(false);
            this.get(FILTER_RULE_MODAL_LEFT_LINK).setVisible(false);
        }

        if (this.modoRule.equals(ModoOperacion.MODIFICACION)) {
            this.get(FILTER_RULE_NAME_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        }
    }

    private boolean existRuleName(String name, List<FilterRule> rulesList) {
        for (FilterRule rule: rulesList) {
            if (rule.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private class AddFilterRuleButton extends BotonAccion {
        public AddFilterRuleButton(String messageConfirmation) {
            super("btnAddRule", Error.class, false, messageConfirmation);
        }

        @Override
        public boolean poseePermisoEjecucion() {
            return true;
        }

        @Override
        public boolean isVisible() {
            return !FilterRuleEntityForm.this.modoRule.equals(ModoOperacion.CONSULTA);
        }

        @Override
        public void ejecutar() {
            saveFilterRule();
        }
    }

    private void saveFilterRule() {
        if (!this.isValid()) {
            this.showError("FilterRule.valid.data");
            return;
        }

        Optional<Filter> filterOptional = getFilterService().getFilter(this.filterName);
        if (filterOptional.isPresent()) {
            Filter currentFilter = filterOptional.get();

            FilterRule filterRule = new FilterRule();
            filterRule.setName(this.name);
            filterRule.setOperator(FilterRule.Operator.valueOf(this.selectedOperator.getKey()));
            filterRule.setLeftFactor(this.buildFactorLeft());
            filterRule.setRightFactor(this.buildFactorRight());

            if (!this.validateRelationWithTopic(currentFilter, filterRule)) {
                this.showError("FilterRule.save.warning");
                return;
            }

            List<FilterRule> rulesList = currentFilter.getRules();
            if(rulesList == null)
                rulesList = new ArrayList<>();

            if (this.modoRule.equals(ModoOperacion.ALTA)) {
                if (this.existRuleName(this.name, rulesList)) {
                    this.showError("Ya existe la regla: " + this.name);
                    return;
                }

                rulesList.add(filterRule);
            } else {
                for (FilterRule rule : rulesList) {
                    if (rule.getName().equals(this.filterRuleName)) {
                        rulesList.remove(rule);
                        rulesList.add(filterRule);
                        break;
                    }
                }
            }

            currentFilter.setRules(rulesList);
            this.getFilterService().saveFilter(currentFilter);

            PageParameters parameters = new PageParameters();
            parameters.add(MODE_PARAM_NAME, modo);
            parameters.add(FILER_MODE_RULE_PARAM_NAME, this.modoRule);
            parameters.add(FILTER_PARAM_NAME, this.filterName);

            setResponsePage(PageFilterForm.class, parameters);
            this.showSuccess("Operacion.exitosa");
        }
    }

    private Factor buildFactorLeft() {
        Factor leftFactor = new Factor();
        leftFactor.setType(Factor.Type.valueOf(this.selectedLeftFactorType.getKey()));
        if (Factor.Type.valueOf(this.selectedLeftFactorType.getKey()) == Factor.Type.SUBSCRIBER) {
            leftFactor.setValue(this.selectedLeftSubscriberProperty.getKey());
        } else {
            leftFactor.setValue(this.leftFactorValue);
        }
        leftFactor.setValueType(Factor.ValueType.valueOf(this.selectedLeftValueType.getKey()));
        if (Factor.ValueType.valueOf(this.selectedLeftValueType.getKey()) == Factor.ValueType.TEXT) {
            leftFactor.setValueFormat("");
        } else if (Factor.ValueType.valueOf(this.selectedLeftValueType.getKey()) == Factor.ValueType.DATETIME) {
            leftFactor.setValueFormat(this.selectedLeftFormatDatetime);
        } else {
            leftFactor.setValueFormat(this.selectedLeftFormatNumeric);
        }

        return leftFactor;
    }

    private Factor buildFactorRight() {
        Factor rightFactor = new Factor();
        rightFactor.setType(Factor.Type.valueOf(this.selectedRightFactorType.getKey()));
        if (Factor.Type.valueOf(this.selectedRightFactorType.getKey()) == Factor.Type.SUBSCRIBER) {
            rightFactor.setValue(this.selectedRightSubscriberProperty.getKey());
        } else {
            rightFactor.setValue(this.rightFactorValue);
        }
        rightFactor.setValueType(Factor.ValueType.valueOf(this.selectedRightValueType.getKey()));
        if (Factor.ValueType.valueOf(this.selectedRightValueType.getKey()) == Factor.ValueType.TEXT) {
            rightFactor.setValueFormat("");
        } else if (Factor.ValueType.valueOf(this.selectedRightValueType.getKey()) == Factor.ValueType.DATETIME) {
            rightFactor.setValueFormat(this.selectedRightFormatDatetime);
        } else {
            rightFactor.setValueFormat(this.selectedRightFormatNumeric);
        }

        return rightFactor;
    }

    private boolean validateRelationWithTopic(Filter filter,  FilterRule filterRule) {
        if (filterRule.getLeftFactor().getType() == Factor.Type.SUBSCRIBER || filterRule.getRightFactor().getType() == Factor.Type.SUBSCRIBER) {
            List<FilterTopic> filterTopicList = this.getFilterTopicService().searchFilterTopicsByFilter(filter);

            for (FilterTopic current: filterTopicList) {
                if (current.getType() == FilterTopic.Type.MESSAGE) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isValid() {
        boolean isValid = false;

        if (this.validateFactorLeft() && this.validateFactorRight()) {
            isValid = this.isRuleComparable();
        }

        return isValid;
    }

    private boolean isRuleComparable() {
        boolean isValid = false;

        if (this.getFormatLeft().equals(this.getFormatRight())) {
            isValid = true;
        }

        return isValid;
    }

    private String getFormat(Factor.Type type, String value, Factor.ValueType valueType, String format) {
        String result = "";

        switch (type) {
            case SUBSCRIBER:
                result = this.getFormatSubcriber(value);
                break;
            case FIXEDTEXT:
            case LIST:
            case XPATH:
            default:
                if (valueType == Factor.ValueType.NUMBER) {
                    result = format;
                } else if (valueType == Factor.ValueType.DATETIME) {
                    result = this.getFormatDatetime(format);
                } else {
                    result = "Text";
                }
        }

        return result;
    }

    private String getFormatLeft() {
        Factor.Type type = Factor.Type.valueOf(this.selectedLeftFactorType.getKey());
        Factor.ValueType valueType = Factor.ValueType.valueOf(this.selectedLeftValueType.getKey());

        String format;
        if (Factor.ValueType.valueOf(this.selectedLeftValueType.getKey()) == Factor.ValueType.TEXT) {
            format = Factor.ValueType.TEXT.name();
        } else if (Factor.ValueType.valueOf(this.selectedLeftValueType.getKey()) == Factor.ValueType.DATETIME) {
            format = this.selectedLeftFormatDatetime;
        } else {
            format = this.selectedLeftFormatNumeric;
        }

        String value = this.leftFactorValue;
        if (this.hasSubscriber()) {
            value = this.selectedLeftSubscriberProperty.getKey();
        }

        return this.getFormat(type, value, valueType, format);
    }

    private String getFormatRight() {
        Factor.Type type = Factor.Type.valueOf(this.selectedRightFactorType.getKey());
        Factor.ValueType valueType = Factor.ValueType.valueOf(this.selectedRightValueType.getKey());

        String format;
        if (Factor.ValueType.valueOf(this.selectedRightValueType.getKey()) == Factor.ValueType.TEXT) {
            format = Factor.ValueType.TEXT.name();
        } else if (Factor.ValueType.valueOf(this.selectedRightValueType.getKey()) == Factor.ValueType.DATETIME) {
            format = this.selectedRightFormatDatetime;
        } else {
            format = this.selectedRightFormatNumeric;
        }

        String value = this.rightFactorValue;
        if (this.hasSubscriber()) {
            value = this.selectedRightSubscriberProperty.getKey();
        }

        return this.getFormat(type, value, valueType, format);
    }

    private String getFormatSubcriber(String value) {
        String result;

        switch (value) {
            case "fechaCreacion":
                result = "Date";
                break;
            case "id":
            case "dn":
            case "nombre":
            default:
                result = "Text";
        }

        return result;
    }

    private String getFormatDatetime(String format) {
        String result;

        List<String> formatsDate = Stream.of(
                "yyyy-MM-dd",
                "dd-MM-yyyy"
        ).collect(Collectors.toList());

        List<String> formatsTime = Stream.of(
                "HHmmss",
                "HH:mm:ss",
                "HH:mm"
        ).collect(Collectors.toList());

        if (formatsTime.contains(format)) {
            result = "Time";
        } else if (formatsDate.contains(format)) {
            result = "Date";
        } else {
            result = "Datetime";
        }

        return result;
    }

    private boolean validateFactor(Factor.Type type, String value, Factor.ValueType valueType, String format) {
        boolean isValid;

        switch (type) {
            case FIXEDTEXT:
                isValid = this.validateFactorFixedType(value, valueType, format);
                break;
            case LIST:
                isValid = this.validateFactorListType(value, valueType, format);
                break;
            case SUBSCRIBER:
            case XPATH:
            default:
                isValid = true;
        }

        return isValid;
    }

    private boolean validateFactorLeft() {
        Factor.Type type = Factor.Type.valueOf(this.selectedLeftFactorType.getKey());
        Factor.ValueType valueType = Factor.ValueType.valueOf(this.selectedLeftValueType.getKey());

        String format;
        if (Factor.ValueType.valueOf(this.selectedLeftValueType.getKey()) == Factor.ValueType.TEXT) {
            format = Factor.ValueType.TEXT.name();
        } else if (Factor.ValueType.valueOf(this.selectedLeftValueType.getKey()) == Factor.ValueType.DATETIME) {
            format = this.selectedLeftFormatDatetime;
        } else {
            format = this.selectedLeftFormatNumeric;
        }

        String value = this.leftFactorValue;
        if (this.hasSubscriber()) {
            value = this.selectedLeftSubscriberProperty.getKey();
        }

        return this.validateFactor(type, value, valueType, format);
    }

    private boolean validateFactorRight() {
        Factor.Type type = Factor.Type.valueOf(this.selectedRightFactorType.getKey());
        Factor.ValueType valueType = Factor.ValueType.valueOf(this.selectedRightValueType.getKey());

        String format;
        if (Factor.ValueType.valueOf(this.selectedRightValueType.getKey()) == Factor.ValueType.TEXT) {
            format = Factor.ValueType.TEXT.name();
        } else if (Factor.ValueType.valueOf(this.selectedRightValueType.getKey()) == Factor.ValueType.DATETIME) {
            format = this.selectedRightFormatDatetime;
        } else {
            format = this.selectedRightFormatNumeric;
        }

        String value = this.rightFactorValue;
        if (this.hasSubscriber()) {
            value = this.selectedRightSubscriberProperty.getKey();
        }

        return this.validateFactor(type, value, valueType, format);
    }

    private boolean hasSubscriber() {
        return Factor.Type.valueOf(this.selectedLeftFactorType.getKey()) == Factor.Type.SUBSCRIBER ||
                Factor.Type.valueOf(this.selectedRightFactorType.getKey()) == Factor.Type.SUBSCRIBER;
    }

    private boolean validateFactorFixedType(String value, Factor.ValueType valueType, String format) {
        boolean isValid;

        switch (valueType) {
            case NUMBER:
                isValid = this.validateValueTypeNumber(value, format);
            break;
            case DATETIME:
                isValid = this.validateValueTypeDatetime(value, format);
            break;
            case TEXT:
            default:
                isValid = true;
        }

        return isValid;
    }

    private boolean validateFactorListType(String value, Factor.ValueType valueType, String format) {
        boolean isValid;

        List<String> items = Arrays.asList(value.split("\\s*;\\s*"));

        switch (valueType) {
            case NUMBER:
                isValid = this.listValidateValueTypeNumber(items, format);
                break;
            case DATETIME:
                isValid = this.listValidateValueTypeDatetime(items, format);
                break;
            case TEXT:
            default:
                isValid = true;
        }

        return isValid;
    }

    private boolean validateValueTypeNumber(String value, String format) {
        boolean isValid;

        if (format.equals("Integer")) {
            isValid = this.validateInteger(value);
        } else {
            isValid = this.validateDouble(value);
        }

        return isValid;
    }

    private boolean validateValueTypeDatetime(String value, String format) {
        boolean isValid;

        List<String> formatsDate = Stream.of(
                "yyyy-MM-dd",
                "dd-MM-yyyy"
        ).collect(Collectors.toList());

        List<String> formatsTime = Stream.of(
                "HHmmss",
                "HH:mm:ss",
                "HH:mm"
        ).collect(Collectors.toList());

        if (formatsTime.contains(format)) {
            isValid = this.validateTime(value, format);
        } else if (formatsDate.contains(format)) {
            isValid = this.validateDate(value, format);
        } else {
            isValid = this.validateDatetime(value, format);
        }

        return isValid;
    }

    private boolean listValidateValueTypeNumber(List<String> list, String format) {
        for (String item: list) {
            if (!this.validateValueTypeNumber(item, format)) {
                return false;
            }
        }
        return true;
    }

    private boolean listValidateValueTypeDatetime(List<String> list, String format) {
        for (String item: list) {
            if (!this.validateValueTypeDatetime(item, format)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateDate(String value, String format) {
        try {
            LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

    private boolean validateDatetime(String value, String format) {
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

    private boolean validateTime(String value, String format) {
        try {
            LocalTime.parse(value, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

    private boolean validateInteger(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }

        return true;
    }

    private boolean validateDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }

        return true;
    }
}
