package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroEntregaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListDateFilterForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.TopicFilterUpdatingBehavior;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.TopicSuscriptorComponentUpdating;
import uy.gub.agesic.pdi.pys.backoffice.views.Entregas;
import uy.gub.agesic.pdi.pys.domain.EstadoEntrega;
import uy.gub.agesic.pdi.pys.domain.ReporteEntregas;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.ArrayList;

@java.lang.SuppressWarnings("squid:S1068")
public class ColeccionesForm extends ListDateFilterForm implements TopicSuscriptorComponentUpdating {

    private static final long serialVersionUID = 1L;

    // Filtros
    private Topico topico;
    private EstadoEntrega estado = EstadoEntrega.PENDIENTE;
    private Suscriptor suscriptor;

    // Entregas recuperados y seleccionados
    private ArrayList<ReporteEntregas> entregas;
    private ArrayList<ReporteEntregas> entregasSeleccion;

    //Lista de productores y tópicos para cargar datos filtros
    private ArrayList<EstadoEntrega> listaEstados;
    private ArrayList<Topico> listaTopicos;
    private ArrayList<Suscriptor> listaSuscriptores;

    public ColeccionesForm() {
        super("coleccionesForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        this.entregas = new ArrayList<>();
        this.entregasSeleccion = new ArrayList<>();
        this.listaEstados = new ArrayList<>();
        this.listaTopicos = new ArrayList<>();
        this.listaSuscriptores = new ArrayList<>();

        buscarEstados();
        buscarTopicos();
        buscarSuscriptores();

        DropDownChoice<EstadoEntrega> estadoFiltro = new DropDownChoice<>("estado", new PropertyModel<>(this, "estado"), listaEstados);
        estadoFiltro.setNullValid(false);
        estadoFiltro.setLabel(new Model<>("Estado"));
        this.add(estadoFiltro);

        DropDownChoice<Suscriptor> suscriptorFiltro = new DropDownChoice<>("suscriptor", new PropertyModel<>(this, "suscriptor"), listaSuscriptores);
        suscriptorFiltro.setNullValid(true);
        suscriptorFiltro.setOutputMarkupId(true);
        suscriptorFiltro.setOutputMarkupPlaceholderTag(true);
        suscriptorFiltro.setLabel(new Model<>("Suscriptor"));
        this.add(suscriptorFiltro);

        DropDownChoice<Topico> topicoFiltro = new DropDownChoice<>("topico", new PropertyModel<>(this, "topico"), listaTopicos);
        topicoFiltro.setNullValid(false);
        topicoFiltro.setLabel(new Model<>("Tópico"));
        this.add(topicoFiltro);

        topicoFiltro.add(new TopicFilterUpdatingBehavior(suscriptorFiltro, this));

        addDateTimeFilters();

        Button limpiarFiltrosButton = new Button(LIMPIAR_FILTRO_COMPONENT_NAME) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                limpiarFiltros();
                ColeccionesForm.this.clearInput();
            }
        };
        limpiarFiltrosButton.setDefaultFormProcessing(false);
        this.add(limpiarFiltrosButton);

        // Agregamos el cuerpo de la grilla
        CheckGroup<ReporteEntregas> group = new CheckGroup<>("group", this.entregasSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        ColeccionesForm.ColeccionesDataView dataView = new ColeccionesForm.ColeccionesDataView("rows", new ListDataProvider<ReporteEntregas>(this.entregas));
        group.add(dataView);

        if (permiso != null && !permiso.equals((PermisoUsuario.LECTURA.name()))) {
            String msg = "Sin permisos";
            logger.warn(msg);
        }

    }

    private class ColeccionesDataView extends DataView<ReporteEntregas> {

        public ColeccionesDataView(String id, ListDataProvider<ReporteEntregas> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<ReporteEntregas> item) {
            final ReporteEntregas info = item.getModelObject();

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView("dataRow");

            String nombreTopico = null;
            String nombreSuscriptor = null;
            String estadoInfo = null;
            String total = null;

            if (info.getTopico() != null) {
                nombreTopico = info.getTopico();
            }

            if (info.getSuscriptor() != null){
                nombreSuscriptor = info.getSuscriptor();
            }

            if (info.getEstado() != null) {
                estadoInfo = info.getEstado();
            }

            if (info.getTotal() != null) {
                total = info.getTotal();
            }

            //Link para acceder a visualizar entregas
            StatelessLink linkEntregas = new StatelessLink("linkVerEntregas") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {

                    visualizarEntregas(info);
                }
            };
            linkEntregas.add(new Label("verEntregas", "ver entregas"));

            item.add(linkEntregas);

            repeatingView.add(new Label(repeatingView.newChildId(), nombreTopico));
            repeatingView.add(new Label(repeatingView.newChildId(), nombreSuscriptor));
            repeatingView.add(new Label(repeatingView.newChildId(), estadoInfo));
            repeatingView.add(new Label(repeatingView.newChildId(), total));

            item.add(repeatingView);
        }
    }


    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();

        if (this.estado != null) {
            parameters.add(FILTRO_ESTADO, this.estado);
        }

        if (this.topico != null && !this.topico.getNombre().trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE_TOPICO, this.topico.getNombre());
        }

        if (this.suscriptor != null && !this.suscriptor.getNombre().trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE_SUSCRIPTOR, this.suscriptor.getNombre());
        }

        if (this.fechaDesde != null) {
            parameters.add(FILTRO_FECHA_DESDE, this.fechaDesde);
        }

        if (this.fechaHasta != null ) {
            parameters.add(FILTRO_FECHA_HASTA, this.fechaHasta);
        }

        return (parameters);
    }

    @Override
    public void limpiarFiltros() {
        this.get("dateDesde").getDefaultModel().setObject(null);
        this.get("dateHasta").getDefaultModel().setObject(null);
        this.estado = EstadoEntrega.PENDIENTE;
        this.topico = null;
        this.suscriptor = null;
        this.entregas.clear();
        this.entregasSeleccion.clear();

        super.limpiarFiltros();
        buscarSuscriptores();

    }

    @Override
    protected void buscar(Integer pagina) {

        FiltroEntregaConsultaDTO filtro = new FiltroEntregaConsultaDTO(pagina, super.getPageSize());
        filtro.setEstado(this.estado == null ? null : this.estado.name());
        filtro.setTopico(this.topico);
        filtro.setSuscriptor(this.suscriptor);

        filtro.setFechaDesde(this.fechaDesde);

        fechaHasta = DateFilterUtil.getToDate(fechaHasta);

        filtro.setFechaHasta(this.fechaHasta);

        ResultadoPaginadoDTO<ReporteEntregas> resultado = null;
        try {
            resultado = obtenerEntregaService().reporteEntregas(filtro);
        } catch (PSException e) {
            warn(e.getMessage());
        }

        cantRegistrosDescription(resultado);

        this.entregas.clear();

        if (pagina == null) {
            this.entregasSeleccion.clear();
        }

        if (resultado != null) {
            this.entregas.addAll(resultado.getResultado());
        }

        updateNavigator(pagina, resultado);

        if (entregas.isEmpty()) {
            if (this.topico == null || this.suscriptor == null) {
                super.setCeroCantRegistros();
            }
            if (this.topico != null) {
                getSession().success("No se encontraron resultados para la b\u00FAsqueda");
            }
            return;
        }

    }

    public void buscarEstados() {
        this.listaEstados = new ArrayList<>();
        this.listaEstados.add(EstadoEntrega.PENDIENTE);
        this.listaEstados.add(EstadoEntrega.CANCELADO);
        this.listaEstados.add(EstadoEntrega.ENVIADO);
    }

    public void buscarTopicos() {
        super.buscarTopicos(this.listaTopicos);
    }

    public void buscarSuscriptores() {
        super.buscarSuscriptores(this.listaSuscriptores);
    }

    public void buscarSuscriptoresTopico() {
        super.buscarSuscriptoresTopico(this.listaSuscriptores, topico.getNombre());
    }

    public void visualizarEntregas(ReporteEntregas reporteEntregas) {
        PageParameters parameters = new PageParameters();

        parameters.add(MODE_PARAM_NAME, ModoOperacion.CONSULTA);

        parameters.add(FILTRO_ESTADO, reporteEntregas.getEstado());
        parameters.add(FILTRO_NOMBRE_TOPICO, reporteEntregas.getTopico());
        parameters.add(FILTRO_NOMBRE_SUSCRIPTOR, reporteEntregas.getSuscriptor());

        if (this.fechaDesde != null) {
            String str = dateFormat.format(this.fechaDesde);
            parameters.add(FILTRO_FECHA_DESDE, str);
        }

        if (this.fechaHasta != null ) {
            parameters.add(FILTRO_FECHA_HASTA, dateFormat.format(this.fechaHasta));
        }

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = ColeccionesForm.this.filtrosAParametrosPagina();

        if (this.fechaDesde != null) {
            parametrosFiltro.remove(FILTRO_FECHA_DESDE);
        }

        if (this.fechaHasta != null ) {
            parametrosFiltro.remove(FILTRO_FECHA_HASTA);
        }

        parameters.mergeWith(parametrosFiltro);

        setResponsePage(Entregas.class, parameters);
    }

    @Override
    public void onUpdate(AjaxRequestTarget target, DropDownChoice<Suscriptor> component) {
        if (target != null) {
            target.add(component);

            if (topico != null) {
                buscarSuscriptoresTopico();
            } else {
                buscarSuscriptores();
            }

            component.setChoices(listaSuscriptores);
        }
    }

}