package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backend.service.FilterService;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GuardarButton;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.sesion.BackofficeAuthenticationSession;
import uy.gub.agesic.pdi.pys.backoffice.views.*;
import uy.gub.agesic.pdi.pys.domain.Filter;
import uy.gub.agesic.pdi.pys.domain.FilterRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"squid:S1068", "squid:S1450"})
public class FilterEntityForm extends BackofficeForm {
    private String name;
    private Filter.Operator operator;
    private Filter.DocumentType documentType;

    String currentFilterRuleName;

    private ArrayList<FilterRule> rules;

    protected BackofficeProperties properties;

    private static final List<String> DOCUMENT_TYPE_OPTIONS = Stream.of(Filter.DocumentType.values())
            .map(Filter.DocumentType::name)
            .collect(Collectors.toList());
    private String selectedDocumentType = Filter.DocumentType.valueOf("XML").toString();

    private static final List<String> OPERATORS_OPTIONS = Stream.of(Filter.Operator.values())
            .map(Filter.Operator::name)
            .collect(Collectors.toList());
    private String selectedOperator = Filter.Operator.valueOf("AND").toString();

    public FilterEntityForm() {
        super("filterEntityForm");
    }

    @Override
    public void initForm() {
        this.rules = new ArrayList<>();
        this.properties = ((BackofficePage) this.getPage()).getProperties();
        this.setDefaultModel(new CompoundPropertyModel<>(this));

        String logedUser = ((BackofficeAuthenticationSession) AuthenticatedWebSession.get()).getUsername();
        try {
            if (logedUser != null) {
                permiso = this.obtenerUsuarioService().permisoUsuario(logedUser);
            }
        } catch (BackofficeException e) {
            showError("Error.General");
        }

        final Label labelnameField = new Label(FILTER_LABEL_NAME_FIELD, "Nombre *");
        labelnameField.setOutputMarkupId(true);
        labelnameField.setOutputMarkupPlaceholderTag(true);
        labelnameField.setVisible(true);
        this.add(labelnameField);

        final FormComponent<String> nameField = new TextField<String>(FILTER_NAME_FIELD).setRequired(true);
        nameField.add(StringValidator.maximumLength(250));
        this.add(new ComponentCustomFeedbackPanel("nameFeedback", new ComponentFeedbackMessageFilter(nameField)));
        nameField.setLabel(new Model("Nombre"));
        this.add(nameField);

        final Label labelSelectDocumentType = new Label(FILTER_LABEL_DOCUMENT_TYPE_FIELD, "Tipo de documento *");
        labelSelectDocumentType.setOutputMarkupId(true);
        labelSelectDocumentType.setOutputMarkupPlaceholderTag(true);
        labelSelectDocumentType.setVisible(false);
        this.add(labelSelectDocumentType);

        final DropDownChoice<String> selectDocumentTypeChoiceField = new DropDownChoice<String>(
                FILTER_DOCUMENT_TYPE_FIELD, new PropertyModel<String>(this, "selectedDocumentType"), DOCUMENT_TYPE_OPTIONS);
        selectDocumentTypeChoiceField.setNullValid(false);
        selectDocumentTypeChoiceField.setVisible(false);
        this.add(selectDocumentTypeChoiceField);

        final Label labelSelectOperatorField = new Label(FILTER_LABEL_OPERATOR_FIELD, "Operador *");
        labelSelectOperatorField.setOutputMarkupId(true);
        labelSelectOperatorField.setOutputMarkupPlaceholderTag(true);
        labelSelectOperatorField.setVisible(true);
        this.add(labelSelectOperatorField);

        final DropDownChoice<String> selectOperatorChoiceField = new DropDownChoice<String>(
                FILTER_OPERATOR_FIELD, new PropertyModel<String>(this, "selectedOperator"), OPERATORS_OPTIONS);
        selectOperatorChoiceField.setNullValid(false);
        this.add(selectOperatorChoiceField);

        this.add(new FilterEntityForm.FormGuardarButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        Button btnNewFilterRule = (new Button(FILTER_BTN_NEW_FILTER_RULE) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                createFilterRule();
            }
        });
        this.add (btnNewFilterRule);

        this.searchFilterRules();

        FilterRuleDataView filterRuleDataView = new FilterRuleDataView("rowsRule", new ListDataProvider<>(this.rules));
        this.add(filterRuleDataView);

        this.add(new FilterEntityForm.LinkVolver());

        if (permiso != null && permiso.equals(PermisoUsuario.LECTURA.name())) {
            this.get(FILTER_BTN_NEW_FILTER_RULE).setVisible(false);
        }
    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        ((LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }

        if (parameters.getNamedKeys().contains(FILTER_PARAM_NAME)) {
            this.name = parameters.get(FILTER_PARAM_NAME).toString();
        }

        if (!this.modo.equals(ModoOperacion.ALTA)) {
            this.initializeValues();
        }

        if (this.modo.equals(ModoOperacion.ALTA)) {
            this.get(FILTER_BTN_NEW_FILTER_RULE).setVisible(false);
        }

        if (this.modo.equals(ModoOperacion.CONSULTA)) {
            this.get(FILTER_NAME_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FILTER_DOCUMENT_TYPE_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_OPERATOR_FIELD).add(AttributeModifier.replace(DISABLED_ATTRIBUTE_NAME, DISABLED_ATTRIBUTE_NAME));
            this.get(FILTER_BTN_NEW_FILTER_RULE).setVisible(false);
        }
    }

    private void saveFilter () {
        FilterService filterService = this.getFilterService();

        if (ModoOperacion.ALTA.equals(this.modo) || ModoOperacion.MODIFICACION.equals(this.modo)) {
            if (ModoOperacion.ALTA.equals(this.modo)) {
                if (filterService.getFilter(this.name).isPresent()) {
                    this.showError("Ya existe el filtro: " + this.name);
                    return;
                }
            }

            Filter filter = getFilter(filterService);
            updateFilter(filter);

            filterService.saveFilter(filter);

            if (ModoOperacion.ALTA.equals(this.modo)) {
                getSession().success("Filtro creado exitosamente");
            } else {
                getSession().success("Filtro modificado exitosamente");
            }

            setResponsePage(PageFilterList.class);

            this.modo = ModoOperacion.MODIFICACION;
            this.name = filter.getName();
            this.initializeValues();

            PageParameters parameters = new PageParameters();
            parameters.add(MODE_PARAM_NAME, ModoOperacion.MODIFICACION);
            parameters.add(FILTER_PARAM_NAME, this.name);

            setResponsePage(PageFilterForm.class, parameters);
        }
    }

    private void updateFilter(Filter filter) {
        this.documentType = Filter.DocumentType.valueOf(this.selectedDocumentType);
        this.operator = Filter.Operator.valueOf(this.selectedOperator);

        filter.setName(this.name);
        filter.setDocumentType(this.documentType);
        filter.setOperator(this.operator);
    }

    private Filter getFilter(FilterService filterService) {
        Filter filter = new Filter();;

        if (!ModoOperacion.ALTA.equals(this.modo)) {
            Optional<Filter> filterOptional = filterService.getFilter(name);
            if (filterOptional.isPresent()) {
                filter = filterOptional.get();
            }
        }

        return filter;
    }

    private class FormGuardarButton extends GuardarButton {
        public FormGuardarButton(String mensajeConfirmacion) {
            super(mensajeConfirmacion, FilterEntityForm.this);
        }

        @Override
        public void ejecutar() {
            saveFilter();
        }
    }

    private class LinkVolver extends StatelessLink {
        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {
            setResponsePage(PageFilterList.class, parametersCallback);
        }

        public PageParameters getParametersCallback() {
            return parametersCallback;
        }

        public void setParametersCallback(PageParameters parametersCallback) {
            this.parametersCallback = parametersCallback;
        }
    }

    private void initializeValues() {
        Optional<Filter> filterOptional = getFilterService().getFilter(this.name);
        if (filterOptional.isPresent()) {
            Filter filter = filterOptional.get();

            this.name = filter.getName();
            this.documentType = filter.getDocumentType();
            this.operator = filter.getOperator();

            this.selectedDocumentType = this.documentType.toString();
            this.selectedOperator = this.operator.toString();

            this.get(FILTER_NAME_FIELD).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
        }
    }

    private class FilterRuleDataView extends DataView<FilterRule> {
        public FilterRuleDataView(String id, ListDataProvider<FilterRule> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<FilterRule> item) {
            final FilterRule info = item.getModelObject();

            Link linkDelete = new Link("linkDeleteRule") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    currentFilterRuleName = info.getName();
                    deleteFilterRule();
                }
            };

            linkDelete.add(new Label("deleteRule", "Eliminar"));
            if (permiso != null) {
                if (permiso.equals(PermisoUsuario.LECTURA.name()) || modo.equals(ModoOperacion.CONSULTA)) {
                    linkDelete.setVisible(false);
                } else {
                    linkDelete.add(new AttributeModifier("onclick", "return confirm('¿Está seguro que desea confirmar la operación?');"));
                }
            }
            item.add(linkDelete);

            Link linkUpdate = new Link("linkUpdateRule") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    currentFilterRuleName = info.getName();
                    updateFilterRule();
                }
            };
            linkUpdate.add(new Label("updateRule", "Editar"));
            if (permiso != null && (permiso.equals(PermisoUsuario.LECTURA.name()) || modo.equals(ModoOperacion.CONSULTA))) {
                linkUpdate.setVisible(false);
            }
            item.add(linkUpdate);

            Link dataRowLink = new Link("dataRowLinkRule") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(FILTER_PARAM_NAME, name);
                    parameters.add(FILTER_RULE_PARAM_NAME, info.getName());
                    parameters.add(MODE_PARAM_NAME, modo);
                    parameters.add(FILER_MODE_RULE_PARAM_NAME, ModoOperacion.CONSULTA);

                    setResponsePage(PageFilterRuleForm.class, parameters);
                }
            };
            dataRowLink.add(new Label("dataRowRuleName", info.getName()));
            item.add(dataRowLink);
        }
    }

    public void searchFilterRules() {
        this.rules.clear();
        String filterName = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(FILTER_PARAM_NAME).toString();
        Optional<Filter> filterOptional = getFilterService().getFilter(filterName);
        if (filterOptional.isPresent()) {
            Filter currentFilter = filterOptional.get();

            List<FilterRule> rulesList = currentFilter.getRules();
            if (rulesList != null && !rulesList.isEmpty()) {
                this.rules.addAll(rulesList);
            }
        }
    }

    public void createFilterRule() {
        PageParameters parameters = new PageParameters();
        parameters.add(FILTER_PARAM_NAME, this.name);
        parameters.add(MODE_PARAM_NAME, this.modo);
        parameters.add(FILER_MODE_RULE_PARAM_NAME, ModoOperacion.ALTA);
        setResponsePage(PageFilterRuleForm.class, parameters);
    }

    public void updateFilterRule() {
        PageParameters parameters = new PageParameters();
        parameters.add(FILTER_PARAM_NAME, this.name);
        parameters.add(FILTER_RULE_PARAM_NAME, this.currentFilterRuleName);
        parameters.add(MODE_PARAM_NAME, modo);
        parameters.add(FILER_MODE_RULE_PARAM_NAME, ModoOperacion.MODIFICACION);
        setResponsePage(PageFilterRuleForm.class, parameters);
    }

    public void deleteFilterRule() {
        if (this.currentFilterRuleName == null) {
            this.showError("Debe seleccionar una regla de filtro");
        } else {
            String filterName = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(FILTER_PARAM_NAME).toString();
            Optional<Filter> filterOptional = getFilterService().getFilter(filterName);
            if (filterOptional.isPresent()) {
                Filter currentFilter = filterOptional.get();

                List<FilterRule> rulesList = currentFilter.getRules();
                if(rulesList == null) {
                    rulesList = new ArrayList<>();
                }

                for (FilterRule rule : rulesList) {
                    if (rule.getName().equals(currentFilterRuleName)) {
                        rulesList.remove(rule);
                        break;
                    }
                }

                currentFilter.setRules(rulesList);
                FilterService filterService = this.getFilterService();
                filterService.saveFilter(currentFilter);

                searchFilterRules();
                this.showSuccess("Operacion.exitosa");
            }
        }
    }
}
