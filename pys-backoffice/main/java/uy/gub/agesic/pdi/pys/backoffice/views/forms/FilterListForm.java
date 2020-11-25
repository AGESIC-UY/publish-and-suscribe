package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FilterFilterDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListBackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.PageFilterForm;
import uy.gub.agesic.pdi.pys.domain.Filter;
import uy.gub.agesic.pdi.pys.domain.FilterTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"squid:S1450", "squid:S1068"})
public class FilterListForm extends ListBackofficeForm {
    private static final long serialVersionUID = 1L;

    private String name;

    private ArrayList<Filter> filters;
    private ArrayList<Filter> filtersSeleccion;

    public FilterListForm() {
        super("filterListForm");
    }

    @Override
    public void initForm() {
        this.setDefaultModel(new CompoundPropertyModel<>(this));

        this.filters = new ArrayList<>();
        this.filtersSeleccion = new ArrayList<>();

        super.initForm();

        FormComponent<String> nameField = new TextField<>("name");
        this.add(nameField);

        agregarBotones(true);

        BackofficeProperties properties = ((BackofficePage)this.getPage()).getProperties();
        eliminarButton = new BaseEntitiesForm.EliminarButton(this.getFinalMessage(properties.getMensajeConfirmacion()));
        this.add(eliminarButton);

        if (permiso != null && permiso.equals(PermisoUsuario.LECTURA.name())) {
            this.get("agregar").setVisible(false);
            this.get("modificar").setVisible(false);
        }

        CheckGroup<Filter> group = new CheckGroup<>("group", this.filtersSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        CheckGroupSelector groupSelector = new CheckGroupSelector("groupSelector");
        group.add(groupSelector);
        FilterListFormDataView dataView = new FilterListFormDataView("rows", new ListDataProvider<>(this.filters));
        group.add(dataView);

        buscar(null);
    }

    private class FilterListFormDataView extends DataView<Filter> {
        public FilterListFormDataView(String id, ListDataProvider<Filter> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Filter> item) {
            final Filter info = item.getModelObject();

            item.setModel(new CompoundPropertyModel<>(info));

            Check<Filter> chkSelector = new Check<>(DATA_ROW_SELECTOR, item.getModel());
            chkSelector.add(new AjaxEventBehavior("onclick") {
                protected void onEvent(AjaxRequestTarget target) {
                    Filter filter = (Filter) this.getComponent().getDefaultModelObject();
                    evaluateEliminarButtonVisibility(target, filter.getName());
                }
            });
            item.add(chkSelector);

            StatelessLink dataRowLink = new StatelessLink(DATA_ROW_LINK) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(MODE_PARAM_NAME, ModoOperacion.CONSULTA);
                    parameters.add(FILTER_PARAM_NAME, info.getName());
                    parameters.add(FILTER_PARAM_ID, info.getId());

                    PageParameters parametersFilter = FilterListForm.this.filtrosAParametrosPagina();
                    parameters.mergeWith(parametersFilter);

                    setResponsePage(PageFilterForm.class, parameters);
                }
            };

            dataRowLink.add(new Label(DATA_ROW_NAME, info.getName()));
            item.add(dataRowLink);

            RepeatingView repeatingView = new RepeatingView(DATA_ROW);
            repeatingView.add(new Label(repeatingView.newChildId(), info.getDocumentType()));
            repeatingView.add(new Label(repeatingView.newChildId(), info.getOperator()));

            item.add(repeatingView);
        }
    }

    @Override
    public void buscar(Integer pagina) {
        super.buscar(pagina);

        FilterFilterDTO filterFilterDTO = new FilterFilterDTO(pagina, super.getPageSize());
        filterFilterDTO.setName(this.name);

        ResultadoPaginadoDTO<Filter> result = null;
        try {
            result = getFilterService().searchFilters(filterFilterDTO);
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }

        super.cantRegistrosDescription(result);

        this.filters.clear();

        if (pagina == null) {
            this.filtersSeleccion.clear();
        }

        if (result != null) {
            this.filters.addAll(result.getResultado());
        }

        updateNavigator(pagina, result);

        if(filters.isEmpty()){
            getSession().success("No se encontraron resultados para la búsqueda");
            return;
        }
    }

    @Override
    public void agregar() {
        PageParameters parameters = new PageParameters();
        parameters.add(MODE_PARAM_NAME, ModoOperacion.ALTA);
        parameters.add(FILTER_PARAM_NAME, "");

        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(PageFilterForm.class, parameters);
    }

    @Override
    public void modificar() {
        if (this.filtersSeleccion == null || this.filtersSeleccion.isEmpty()) {
            showError("Filters.selection.empty");
            return;
        }

        Filter filter = this.filtersSeleccion.get(0);

        PageParameters parameters = new PageParameters();
        parameters.add(MODE_PARAM_NAME, ModoOperacion.MODIFICACION);
        parameters.add(FILTER_PARAM_NAME, filter.getName());

        PageParameters parametersFilter = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametersFilter);

        setResponsePage(PageFilterForm.class, parameters);
    }

    @Override
    public void eliminar() {
        if (this.filtersSeleccion == null || this.filtersSeleccion.isEmpty()) {
            showError("Filters.selection.empty");
            return;
        }

        List<String> names = this.filtersSeleccion.stream().map(Filter::getName).collect(Collectors.toList());
        for (String name : names) {
            Optional<Filter> filterOptional = getFilterService().getFilter(name);
            if (filterOptional.isPresent()) {
                Filter currentFilter = filterOptional.get();

                this.getFilterTopicService().deleteByFilter(currentFilter);
                this.getFilterService().deleteFilter(currentFilter);
            }
        }

        this.showSuccess("Operacion.exitosa");
    }

    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();
        if (this.name != null && !this.name.trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE, this.name);
        }
        return (parameters);
    }

    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);
        try {
            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE)) {
                this.name = parameters.get(FILTRO_NOMBRE).toString();
                filtroCargado = true;
            }
        } catch (Exception ex) {
            filtroCargado = false;
        }
        return filtroCargado;
    }

    @Override
    public void limpiarFiltros() {
        this.get("name").getDefaultModel().setObject(null);
        buscar(null);
    }
}
