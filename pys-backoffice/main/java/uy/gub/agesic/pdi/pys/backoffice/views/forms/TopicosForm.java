package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroTopicoDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListBackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConTopico;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class TopicosForm extends ListBackofficeForm {

    private static final long serialVersionUID = 1L;

    private static final String TOPICO_SELECCION_VACIA_PROP = "Topicos.seleccion.vacia";

    // Filtros
    private String nombre;

    private Boolean habilitado;

    private static final List<String> estados = Arrays.asList(HABILITADO_VALUE, DESHABILITADO_VALUE);

    private String estado;

    // Tópicos recuperados y seleccionados
    private ArrayList<Topico> topicos;

    private ArrayList<Topico> topicosSeleccion;

    public TopicosForm () { super("topicosForm"); }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        this.topicos = new ArrayList<>();
        this.topicosSeleccion = new ArrayList<>();

        FormComponent<String> nombreField = new TextField<>("nombre");
        this.add(nombreField);

        DropDownChoice<String> habilitadoLbl = new DropDownChoice<>(
                HABILITADO_NAME, new PropertyModel<>(this, "estado"), estados);
        habilitadoLbl.setLabel(new Model<>("Estado"));
        habilitadoLbl.setNullValid(true);
        this.add(habilitadoLbl);

        agregarBotones(true, topicosSeleccion, TOPICO_SELECCION_VACIA_PROP);

        // Agregamos el cuerpo de la grilla
        CheckGroup<Topico> group = new CheckGroup<>("group", this.topicosSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        CheckGroupSelector groupSelector = new CheckGroupSelector("groupSelector");
        group.add(groupSelector);
        TopicosDataView dataView = new TopicosDataView("rows", new ListDataProvider<>(this.topicos));
        group.add(dataView);

        if (permiso != null && permiso.equals(PermisoUsuario.LECTURA.name())) {
            this.get("agregar").setVisible(false);
            this.get("modificar").setVisible(false);
        }

        buscar(null);

    }

    private class TopicosDataView extends DataView<Topico> {

        public TopicosDataView(String id, ListDataProvider<Topico> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Topico> item) {
            final Topico info = item.getModelObject();

            // Checkbox de seleccion
            Check<Topico> chkSelector = new Check<>(DATA_ROW_SELECTOR, item.getModel());
            chkSelector.add(new AjaxEventBehavior("onclick") {

                protected void onEvent(AjaxRequestTarget target) {
                    Topico topico = (Topico) this.getComponent().getDefaultModelObject();
                    evaluateEliminarButtonVisibility(target, topico.getNombre());
                }

            });
            item.add(chkSelector);

            // Enlace para acceder a la consulta/modificacion del servicio
            StatelessLink dataRowLink = new DataRowStatelessLink(info);
            item.add(dataRowLink);

            dataRowLink.add(new Label(DATA_ROW_NOMBRE, info.getNombre()));

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView(DATA_ROW);
            repeatingView.add(new org.apache.wicket.markup.html.basic.Label(repeatingView.newChildId(), info.getElementoRaiz()));
            repeatingView.add(new org.apache.wicket.markup.html.basic.Label(repeatingView.newChildId(), info.getNamespace()));
            repeatingView.add(new org.apache.wicket.markup.html.basic.Label(repeatingView.newChildId(), info.getSoapAction()));

            String habilitadoStr = "NO";

            if (info.getHabilitado())
                habilitadoStr = "SI";

            repeatingView.add(new Label(repeatingView.newChildId(), habilitadoStr));

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            String fecha = info.getFechaCreacion() != null ? dateFormat.format(info.getFechaCreacion()) : "";
            repeatingView.add(new Label(repeatingView.newChildId(), fecha));

            item.add(repeatingView);
        }

        private class DataRowStatelessLink extends StatelessLink {
            private static final long serialVersionUID = 1L;
            private final Topico info;

            public DataRowStatelessLink(Topico info) {
                super(BackofficeForm.DATA_ROW_LINK);
                this.info = info;
            }

            @Override
            public void onClick() {
                PageParameters parameters = new PageParameters();
                parameters.add(MODE_PARAM_NAME, ModoOperacion.CONSULTA);
                parameters.add(MODE_SUC_PARAM_NAME, ModoOperacion.CONSULTA);
                parameters.add(NOMBRE_TOPICO_PARAM_NAME, info.getNombre());

                // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
                PageParameters parametrosFiltro = TopicosForm.this.filtrosAParametrosPagina();
                parameters.mergeWith(parametrosFiltro);

                setResponsePage(TrabajarConTopico.class, parameters);
            }
        }
    }

    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);

        try {

            if(parameters.getNamedKeys().contains(FILTRO_HABILITADO)){
                this.habilitado = Boolean.valueOf(parameters.get(FILTRO_HABILITADO).toString());
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE)) {
                this.nombre = parameters.get(FILTRO_NOMBRE).toString();
                filtroCargado = true;
            }

        } catch (Exception ex) {
            filtroCargado = false;
        }

        return (filtroCargado);
    }

    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();

        if (this.nombre != null && !this.nombre.trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE, this.nombre);
        }

        if (this.habilitado != null) {
            parameters.add(FILTRO_HABILITADO, this.habilitado);
        }

        return (parameters);
    }

    @Override
    public void limpiarFiltros() {
        this.get("nombre").getDefaultModel().setObject(null);
        this.get(HABILITADO_NAME).getDefaultModel().setObject(null);
        buscar(null);
    }

    @Override
    public void buscar(Integer pagina) {
        super.buscar(pagina);

        FiltroTopicoDTO filtro = new FiltroTopicoDTO(pagina, super.getPageSize());
        filtro.setNombre(this.nombre);

        if (this.estado != null) {
            this.habilitado = this.estado.equalsIgnoreCase(HABILITADO_VALUE);
        } else {
            this.habilitado = null;
        }

        filtro.setHabilitado(this.habilitado);

        ResultadoPaginadoDTO<Topico> resultado = null;
        try {
            resultado = obtenerTopicoService().buscarTopicos(filtro);
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }

        cantRegistrosDescription(resultado);

        this.topicos.clear();

        if (pagina == null) {
            this.topicosSeleccion.clear();
        }

        if (resultado != null) {
            this.topicos.addAll(resultado.getResultado());
        }

        updateNavigator(pagina, resultado);

        if (topicos.isEmpty()) {
            getSession().success("No se encontraron resultados para la b\u00FAsqueda");
            return;
        }

    }

    @Override
    public void agregar() {
        PageParameters parameters = new PageParameters();
        parameters.add(MODE_PARAM_NAME, ModoOperacion.ALTA);
        parameters.add(MODE_SUC_PARAM_NAME, ModoOperacion.CONSULTA);
        parameters.add(NOMBRE_TOPICO_PARAM_NAME, "");

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConTopico.class, parameters);

    }

    @Override
    public void modificar() {
        if (this.topicosSeleccion == null || this.topicosSeleccion.isEmpty()) {
            showError(TOPICO_SELECCION_VACIA_PROP);
            return;
        }

        Topico topico = this.topicosSeleccion.get(0);

        PageParameters parameters = new PageParameters();
        parameters.add(MODE_PARAM_NAME, ModoOperacion.MODIFICACION);
        parameters.add(MODE_SUC_PARAM_NAME, ModoOperacion.MODIFICACION);
        parameters.add(NOMBRE_TOPICO_PARAM_NAME, topico.getNombre());

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConTopico.class, parameters);
    }

    @Override
    public void eliminar() {
        if (this.topicosSeleccion == null || this.topicosSeleccion.isEmpty()) {
            showError(TOPICO_SELECCION_VACIA_PROP);
            return;
        }

        List<String> ids = this.topicosSeleccion.stream().map(Topico::getNombre).collect(Collectors.toList());

        try {

            this.obtenerTopicoService().eliminarTopicos(ids.subList(0,1));

            this.showSuccess("Operacion.exitosa");
        } catch (PSException e) {
            showError("Topicos.error.eliminar");
        }
    }

}
