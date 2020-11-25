package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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
import org.apache.wicket.validation.validator.RangeValidator;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.views.Error;
import uy.gub.agesic.pdi.pys.backoffice.views.PageFilterTopic;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConTopico;
import uy.gub.agesic.pdi.pys.domain.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterTopicForm extends BackofficeForm {
    private static final long serialVersionUID = 1L;
    private ModoOperacion modoFilterTopic;

    private String nombreTopico;
    private String idFilterTopic;

    private FilterTopic.Type type;
    private int filterTopicMaximumOccurrences;

    private ArrayList<Filter> listFilter;
    private Filter selectedFilter;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class SelectOption implements Serializable {
        private String key;
        private String value;
    }

    private ChoiceRenderer<FilterRuleEntityForm.SelectOption> choiceRenderer = new ChoiceRenderer("value", "key");

    private final SelectOption[] TYPE_OPTIONS_KEY_VALUE = new SelectOption[] {
            new SelectOption(FilterTopic.Type.valueOf("MESSAGE").toString(), "Mensaje"),
            new SelectOption(FilterTopic.Type.valueOf("SUBSCRIBER").toString(), "Suscriptor")
    };
    private SelectOption selectedType = TYPE_OPTIONS_KEY_VALUE[1];

    public FilterTopicForm() {
        super("filterTopicForm");
    }

    public FilterTopicForm(String nombreTopico) {
        super("filterTopicForm");
        this.nombreTopico = nombreTopico;
    }

    @Override
    public void initForm() {
        this.setDefaultModel(new CompoundPropertyModel<>(this));
        super.initForm();
        BackofficeProperties properties = ((BackofficePage) this.getPage()).getProperties();

        this.add(new FilterTopicForm.AddFilterTopicButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        if (this.nombreTopico == null) {
            this.nombreTopico = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(NOMBRE_TOPICO_PARAM_NAME).toString();
        }

        if (RequestCycle.get().getRequest().getRequestParameters().getParameterNames().contains (FILER_MODE_FILTER_PARAM_NAME)) {
            this.modoFilterTopic = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(FILER_MODE_FILTER_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        this.listFilter = new ArrayList<>();
        this.searchFilters();

        final Label labelNameField = new Label(FILTER_TOPIC_LABEL_FILTER_NAME_FIELD, "Filtro *");
        labelNameField.setOutputMarkupId(true);
        labelNameField.setOutputMarkupPlaceholderTag(true);
        labelNameField.setVisible(true);
        this.add(labelNameField);

        final DropDownChoice<Filter> selectFilterChoiceField = new DropDownChoice<>(
                FILTER_TOPIC_FILTER_NAME_FIELD, new PropertyModel<>(this, "selectedFilter"), listFilter);
        selectFilterChoiceField.setNullValid(false);
        selectFilterChoiceField.setRequired(true);
        selectFilterChoiceField.setOutputMarkupId(true);
        selectFilterChoiceField.setOutputMarkupPlaceholderTag(true);
        selectFilterChoiceField.setLabel(new Model("Filtro"));
        this.add(selectFilterChoiceField);

        final Label labelMaximumOccurrences = new Label(FILTER_TOPIC_LABEL_MAXIMUM_OCCURRENCES_NAME_FIELD, "Cantidad de ocurrencias *");
        labelMaximumOccurrences.setOutputMarkupId(true);
        labelMaximumOccurrences.setOutputMarkupPlaceholderTag(true);
        labelMaximumOccurrences.setVisible(true);
        this.add(labelMaximumOccurrences);

        final FormComponent<String> maximumOccurrencesField = new TextField<String>(FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FIELD).setRequired(true);
        maximumOccurrencesField.add(RangeValidator.range(0, 99999));
        maximumOccurrencesField.setOutputMarkupId(true);
        maximumOccurrencesField.setOutputMarkupPlaceholderTag(true);
        maximumOccurrencesField.setLabel(new Model("Cantidad de ocurrencias"));
        final ComponentCustomFeedbackPanel maximumOccurrencesFeedback = new ComponentCustomFeedbackPanel(FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FEED_FIELD, new ComponentFeedbackMessageFilter(maximumOccurrencesField));
        maximumOccurrencesFeedback.setOutputMarkupId(true);
        maximumOccurrencesFeedback.setOutputMarkupPlaceholderTag(true);
        this.add(maximumOccurrencesFeedback);
        this.add(maximumOccurrencesField);

        final Label labelSelectType = new Label(FILTER_TOPIC_LABEL_TYPE_NAME_FIELD, "Tipo *");
        labelSelectType.setOutputMarkupId(true);
        labelSelectType.setOutputMarkupPlaceholderTag(true);
        labelSelectType.setVisible(true);
        this.add(labelSelectType);

        final DropDownChoice<String> selectTypeChoiceField = new DropDownChoice(
                FILTER_TOPIC_TYPE_NAME_FIELD, new PropertyModel<>(this, "selectedType"), Arrays.asList(TYPE_OPTIONS_KEY_VALUE), choiceRenderer);
        selectTypeChoiceField.setNullValid(false);
        selectTypeChoiceField.setOutputMarkupId(true);
        selectTypeChoiceField.setOutputMarkupPlaceholderTag(true);
        selectTypeChoiceField.setLabel(new Model("Tipo"));
        this.add(selectTypeChoiceField);
        selectTypeChoiceField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (target != null) {
                    target.add(labelMaximumOccurrences);
                    target.add(maximumOccurrencesField);
                    target.add(maximumOccurrencesFeedback);

                    if (FilterTopic.Type.valueOf(selectedType.getKey()) == FilterTopic.Type.MESSAGE) {
                        labelMaximumOccurrences.setVisible(false);
                        maximumOccurrencesField.setVisible(false);
                        maximumOccurrencesFeedback.setVisible(false);
                    } else {
                        labelMaximumOccurrences.setVisible(true);
                        maximumOccurrencesField.setVisible(true);
                        maximumOccurrencesFeedback.setVisible(true);
                    }
                }
            }
        });

        this.add(new FilterTopicForm.LinkVolver());
    }

    private class LinkVolver extends StatelessLink {
        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {
            parametersCallback.set(MODE_PARAM_NAME, modo);
            parametersCallback.set(NOMBRE_TOPICO_PARAM_NAME, nombreTopico);
            parametersCallback.set(FILTER_TOPIC_PARAM_ID, idFilterTopic);
            parametersCallback.set(FILER_MODE_FILTER_PARAM_NAME, modoFilterTopic);

            setResponsePage(TrabajarConTopico.class, parametersCallback);
        }

        public PageParameters getParametersCallback() {
            return parametersCallback;
        }

        public void setParametersCallback(PageParameters parametersCallback) {
            this.parametersCallback = parametersCallback;
        }
    }

    private void initializeValues() {
        FilterTopic filterTopic = this.getFilterTopicService().getFilterTopic(this.idFilterTopic);
        this.type = filterTopic.getType();
        this.filterTopicMaximumOccurrences = filterTopic.getMaximumOccurrences();
        this.selectedFilter = filterTopic.getFilter();
        this.selectedType = Arrays.asList(TYPE_OPTIONS_KEY_VALUE)
                .stream()
                .filter(options -> filterTopic.getType().toString().equals(options.getKey()))
                .findAny()
                .orElse(null);;

        if (FilterTopic.Type.valueOf(this.selectedType.getKey()) == FilterTopic.Type.MESSAGE) {
            this.get(FILTER_TOPIC_LABEL_MAXIMUM_OCCURRENCES_NAME_FIELD).setVisible(false);
            this.get(FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FIELD).setVisible(false);
            this.get(FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FEED_FIELD).setVisible(false);
        } else {
            this.get(FILTER_TOPIC_LABEL_MAXIMUM_OCCURRENCES_NAME_FIELD).setVisible(true);
            this.get(FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FIELD).setVisible(true);
            this.get(FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FEED_FIELD).setVisible(true);
        }
    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(FILER_MODE_FILTER_PARAM_NAME)) {
            this.modoFilterTopic = parameters.get(FILER_MODE_FILTER_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(NOMBRE_TOPICO_PARAM_NAME)) {
            this.nombreTopico = parameters.get(NOMBRE_TOPICO_PARAM_NAME).toString();
        }

        if (parameters.getNamedKeys().contains(FILTER_TOPIC_PARAM_ID)) {
            this.idFilterTopic = parameters.get(FILTER_TOPIC_PARAM_ID).toString();
        }

        if (!this.modoFilterTopic.equals(ModoOperacion.ALTA)) {
            this.initializeValues();
        }

        if (this.modoFilterTopic.equals(ModoOperacion.CONSULTA)) {
            this.get(FILTER_TOPIC_FILTER_NAME_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_TOPIC_TYPE_NAME_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_TOPIC_MAXIMUM_OCCURRENCES_NAME_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        }
    }

    private class AddFilterTopicButton extends BotonAccion {
        public AddFilterTopicButton(String message) {
            super("btnAddFilterTopic", Error.class, false, message);
        }

        @Override
        public boolean poseePermisoEjecucion() {
            return true;
        }

        @Override
        public boolean isVisible() {
            return !FilterTopicForm.this.modoFilterTopic.equals(ModoOperacion.CONSULTA);
        }

        @Override
        public void ejecutar() {
            saveFilterTopic();
        }
    }

    protected void saveFilterTopic() {
        if (!this.areRulesValid(FilterTopic.Type.valueOf(this.selectedType.getKey()), this.selectedFilter)) {
            return;
        }

        try {
            Topico topic = obtenerTopicoService().buscarTopico(this.nombreTopico);
            if (this.modoFilterTopic.equals(ModoOperacion.ALTA) && getFilterTopicService().findFirstByFilterAndTopic(this.selectedFilter, topic) != null) {
                 showError("FilterTopic.exist.relation");
            } else {
                FilterTopic filterTopic = new FilterTopic();
                if (this.idFilterTopic != null) {
                    filterTopic = getFilterTopicService().getFilterTopic(this.idFilterTopic);
                }
                
                filterTopic.setFilter(this.selectedFilter);
                filterTopic.setTopic(topic);
                filterTopic.setType(FilterTopic.Type.valueOf(this.selectedType.getKey()));
                if (FilterTopic.Type.valueOf(this.selectedType.getKey()) == FilterTopic.Type.MESSAGE) {
                    filterTopic.setMaximumOccurrences(0);
                } else {
                    filterTopic.setMaximumOccurrences(this.filterTopicMaximumOccurrences);
                }

                this.getFilterTopicService().saveFilterTopic(filterTopic);

                PageParameters parameters = new PageParameters();
                parameters.add(MODE_PARAM_NAME, modo);
                parameters.add(FILER_MODE_FILTER_PARAM_NAME, modoFilterTopic);
                parameters.add(NOMBRE_TOPICO_PARAM_NAME, this.nombreTopico);
                parameters.add(FILTER_TOPIC_MESSAGE_SUCCESS, "Operacion.exitosa");

                setResponsePage(TrabajarConTopico.class, parameters);
            }
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
            showError("FilterTopic.error.associate");
        }
    }

    private void searchFilters() {
        this.listFilter.clear();
        this.listFilter.addAll(getFilterService().getAllFilters());
    }

    private boolean areRulesValid(FilterTopic.Type filterTopicType, Filter filter) {
        List<FilterRule> filterRules = filter.getRules();

        if (filterRules != null && !filterRules.isEmpty()) {
            if (filterTopicType == FilterTopic.Type.MESSAGE) {
                for(FilterRule filterRule: filterRules) {
                    if (
                            filterRule.getLeftFactor().getType() == Factor.Type.SUBSCRIBER ||
                                    filterRule.getRightFactor().getType() == Factor.Type.SUBSCRIBER
                    ) {
                        logger.info("El filtro contiene reglas de tipo SUSCRIPTOR que no corresponden con la asociaci&oacute;n de tipo MENSAJE a un t&oacute;pico");
                        showError("FilterTopic.save.warning.subscriptor");
                        return false;
                    }
                }
            }
        } else {
            logger.info("El filtro que intenta asociar no tiene reglas definidas");
            showError("FilterTopic.save.warning.not.rules");
            return false;
        }
        return true;
    }
}
