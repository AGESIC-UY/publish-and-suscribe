package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
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
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroEntregaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListDateFilterForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.TopicFilterUpdatingBehavior;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.TopicSuscriptorComponentUpdating;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConEntrega;
import uy.gub.agesic.pdi.pys.domain.DeliveryMode;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.EstadoEntrega;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.ArrayList;
import java.util.List;

@java.lang.SuppressWarnings("squid:S1068")
public class EntregasForm extends ListDateFilterForm implements TopicSuscriptorComponentUpdating {

    private static final long serialVersionUID = 1L;

    // Filtros
    private String uuid;
    private Topico topico;
    private EstadoEntrega estado;
    private String tipoEntrega;
    private Suscriptor suscriptor;

    // Entregas recuperados y seleccionados
    private ArrayList<Entrega> entregas;
    private ArrayList<Entrega> entregasSeleccion;

    //Lista de productores y tópicos para cargar datos filtros
    private ArrayList<EstadoEntrega> listaEstados;
    private ArrayList<String> listaTiposEntrega;
    private ArrayList<Topico> listaTopicos;
    private ArrayList<Suscriptor> listaSuscriptores;

    public EntregasForm() {
        super("entregasForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        this.entregas = new ArrayList<>();
        this.entregasSeleccion = new ArrayList<>();
        this.listaEstados = new ArrayList<>();
        this.listaTiposEntrega = new ArrayList<>();
        this.listaTopicos = new ArrayList<>();
        this.listaSuscriptores = new ArrayList<>();

        buscarTipoEntrega();
        buscarEstados();
        buscarTopicos();
        buscarSuscriptores();

        DropDownChoice<EstadoEntrega> estadoFiltro = new DropDownChoice<>("estado", new PropertyModel<>(this, "estado"), listaEstados);
        estadoFiltro.setNullValid(true);
        estadoFiltro.setLabel(new Model<>("Estado"));
        this.add(estadoFiltro);

        DropDownChoice<Suscriptor> suscriptorFiltro = new DropDownChoice<>("suscriptor", new PropertyModel<>(this, "suscriptor"), listaSuscriptores);
        suscriptorFiltro.setNullValid(false);
        suscriptorFiltro.setOutputMarkupId(true);
        suscriptorFiltro.setOutputMarkupPlaceholderTag(true);
        suscriptorFiltro.setLabel(new Model<>("Suscriptor"));
        this.add(suscriptorFiltro);

        DropDownChoice<Topico> topicoFiltro = new DropDownChoice<>("topico", new PropertyModel<>(this, "topico"), listaTopicos);
        topicoFiltro.setNullValid(false);
        topicoFiltro.setLabel(new Model<>("Tópico"));
        this.add(topicoFiltro);

        topicoFiltro.add(new TopicFilterUpdatingBehavior(suscriptorFiltro, this));

        DropDownChoice<String> tipoEntregaFiltro = new DropDownChoice<>("tipoEntrega", new PropertyModel<>(this, "tipoEntrega"), listaTiposEntrega);
        tipoEntregaFiltro.setNullValid(true);
        tipoEntregaFiltro.setLabel(new Model<>("Tipo de Entrega"));
        this.add(tipoEntregaFiltro);

        FormComponent<String> uuidField = new TextField<>("uuid");
        this.add(uuidField);

        addDateTimeFilters();
        Component dateTimeDesdeField = this.get("dateDesde");
        dateTimeDesdeField.setOutputMarkupId(true);
        dateTimeDesdeField.setOutputMarkupPlaceholderTag(true);

        //Date and time fields
        Component dateTimeHastaField = this.get("dateHasta");
        dateTimeHastaField.setOutputMarkupId(true);
        dateTimeHastaField.setOutputMarkupPlaceholderTag(true);

        agregarBotones(false);

        this.remove(LIMPIAR_FILTRO_COMPONENT_NAME);

        Button limpiarFiltrosButton = new Button(LIMPIAR_FILTRO_COMPONENT_NAME) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                limpiarFiltros();
                EntregasForm.this.clearInput();
            }
        };
        limpiarFiltrosButton.setDefaultFormProcessing(false);
        this.add(limpiarFiltrosButton);

        this.add(new Button("cancelar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                mostrar(ModoOperacion.CANCELAR);
            }
        });

        this.add(new Button("reenviar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                mostrar(ModoOperacion.REENVIAR);
            }
        });

        // Agregamos el cuerpo de la grilla
        CheckGroup<Entrega> group = new CheckGroup<>("group", this.entregasSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        CheckGroupSelector groupSelector = new CheckGroupSelector("groupSelector");
        group.add(groupSelector);
        EntregasDataView dataView = new EntregasDataView("rows", new ListDataProvider<>(this.entregas));
        group.add(dataView);

        if (permiso != null && permiso.equals((PermisoUsuario.LECTURA.name()))) {
            this.get("cancelar").setVisible(false);
            this.get("reenviar").setVisible(false);
        }

    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        Boolean aplicarFiltros = this.cargarFiltrosPorParametrosPagina(parameters);

        if (aplicarFiltros) {

            if (this.getNroPagina() > 0) {
                this.buscar(this.getNroPagina());
            } else {
                this.buscar(null);
            }

            if (topico != null) {
                buscarSuscriptoresTopico();
            }
        }
    }

    private class EntregasDataView extends DataView<Entrega> {

        public EntregasDataView(String id, ListDataProvider<Entrega> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Entrega> item) {
            final Entrega info = item.getModelObject();

            // Checkbox de seleccion
            Check<Entrega> chkSelector = new Check<>("dataRowSelector", item.getModel());
            item.add(chkSelector);

            // Enlace para acceder a la consulta/modificacion del servicio
            StatelessLink dataRowLink = new StatelessLink("dataRowLink") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(MODE_PARAM_NAME, ModoOperacion.CONSULTA);
                    parameters.add("idEntregaParameter", info.getId());
                    parameters.add("TopicoParameter", info.getNovedad().getTopico() == null ? "" : info.getNovedad().getTopico().getNombre());
                    parameters.add("SuscriptorParameter", info.getSuscriptor() == null ? "" : info.getSuscriptor().getNombre());

                    // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
                    PageParameters parametrosFiltro = EntregasForm.this.filtrosAParametrosPagina();
                    parameters.mergeWith(parametrosFiltro);

                    setResponsePage(TrabajarConEntrega.class, parameters);
                }
            };
            item.add(dataRowLink);

            dataRowLink.add(new Label("dataRowId", info.getUuid()));

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView("dataRow");

            String tipoEstado = null;
            String tipoEntregaInfo = null;
            String nombreTopico = null;
            String nombreSuscriptor = null;
            String nombreProductor = null;

            if (info.getEstado() != null) {
                tipoEstado = info.getEstado();
            }

            if (info.getTipoEntrega() != null) {
                tipoEntregaInfo = info.getTipoEntrega();
            }

            if (info.getNovedad().getTopico() != null) {
                nombreTopico = info.getNovedad().getTopico().getNombre();
            }

            if (info.getSuscriptor() != null){
                nombreSuscriptor = info.getSuscriptor().getNombre();
            }

            if (info.getNovedad().getProductor() != null){
                nombreProductor = info.getNovedad().getProductor().getNombre();
            }

            repeatingView.add(new Label(repeatingView.newChildId(), nombreSuscriptor));
            repeatingView.add(new Label(repeatingView.newChildId(), nombreProductor));
            repeatingView.add(new Label(repeatingView.newChildId(), nombreTopico));
            repeatingView.add(new Label(repeatingView.newChildId(), info.getFechaCreado() == null ? null : dateFormat.format(info.getFechaCreado())));
            repeatingView.add(new Label(repeatingView.newChildId(), info.getFechaEnviado() == null ? null : dateFormat.format(info.getFechaEnviado())));
            repeatingView.add(new Label(repeatingView.newChildId(), tipoEstado));
            repeatingView.add(new Label(repeatingView.newChildId(), tipoEntregaInfo));
            repeatingView.add(new Label(repeatingView.newChildId(), info.getCantidadReintentos()));
            repeatingView.add(new Label(repeatingView.newChildId(), info.getFechaUltimoIntento() == null ? null : dateFormat.format(info.getFechaUltimoIntento())));

            item.add(repeatingView);
        }
    }

    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();

        if (this.estado != null) {
            parameters.add(FILTRO_ESTADO, this.estado);
        }

        if (this.tipoEntrega != null && !this.tipoEntrega.trim().isEmpty()) {
            parameters.add(FILTRO_TIPO_ENTREGA, this.tipoEntrega);
        }

        if (this.topico != null && !this.topico.getNombre().trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE_TOPICO, this.topico.getNombre());
        }

        if (this.suscriptor != null && !this.suscriptor.getNombre().trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE_SUSCRIPTOR, this.suscriptor.getNombre());
        }

        if (this.uuid != null && !this.uuid.trim().isEmpty()) {
            parameters.add(FILTRO_UUID, this.uuid);
        }

        return (parameters);
    }

    @SuppressWarnings("squid:S3776")
    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);

        try {

            if (parameters.getNamedKeys().contains(FILTRO_ESTADO)) {
                String nombreEstado = parameters.get(FILTRO_ESTADO).toString();
                this.estado = EstadoEntrega.valueOf(nombreEstado);
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE_TOPICO)) {
                String nombreTopico = parameters.get(FILTRO_NOMBRE_TOPICO).toString();
                this.topico = getEntity(listaTopicos, nombreTopico);
                filtroCargado = this.topico != null || filtroCargado;
                if (this.topico != null) {
                    buscarSuscriptoresTopico();
                }

            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE_SUSCRIPTOR)) {
                String nombreSuscriptor = parameters.get(FILTRO_NOMBRE_SUSCRIPTOR).toString();
                this.suscriptor = getEntity(listaSuscriptores, nombreSuscriptor);
                filtroCargado = this.suscriptor != null || filtroCargado;
            }

            if (parameters.getNamedKeys().contains(FILTRO_TIPO_ENTREGA)) {
                this.tipoEntrega = parameters.get(FILTRO_TIPO_ENTREGA).toString();
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_UUID)) {
                this.uuid = parameters.get(FILTRO_UUID).toString();
                filtroCargado = true;
            }

        } catch (Exception ex) {
            filtroCargado = false;
        }

        return filtroCargado;
    }

    @Override
    public void limpiarFiltros() {
        this.get("uuid").getDefaultModel().setObject(null);
        this.get("dateDesde").getDefaultModel().setObject(null);
        this.get("dateHasta").getDefaultModel().setObject(null);
        this.estado = null;
        this.tipoEntrega = null;
        this.topico = null;
        this.suscriptor = null;

        getEntregas().clear();

        getEntregasSeleccion().clear();

        super.limpiarFiltros();

        buscarSuscriptores();
    }


    private void mostrar(ModoOperacion modo) {
        if (this.entregasSeleccion == null || this.entregasSeleccion.isEmpty()) {
            showError("Entrega.seleccion.vacia");
            return;
        }

        Entrega entrega = this.entregasSeleccion.get(0);
        if (modo.equals(ModoOperacion.CANCELAR) && !entrega.getEstado().equals(EstadoEntrega.PENDIENTE.name())) {
            showError("Entrega.cancelar.pendiente");
            return;
        } else if (modo.equals(ModoOperacion.REENVIAR) && (!entrega.getEstado().equals(EstadoEntrega.PENDIENTE.name()) || !suscripcionPush(entrega))) {
            showError("Entrega.reenviar.pendiente");
            return;
        }


        PageParameters parameters = new PageParameters();
        parameters.add("idEntregaParameter", entrega.getId());
        parameters.add("TopicoParameter", entrega.getNovedad().getTopico().getNombre());
        parameters.add("SuscriptorParameter", entrega.getSuscriptor().getNombre());
        parameters.add("EstadoParameter", entrega.getEstado());
        parameters.add(MODE_PARAM_NAME, modo.name());

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConEntrega.class, parameters);
    }

    @Override
    public void buscar(Integer pagina) {
        super.buscar(pagina);

        FiltroEntregaConsultaDTO filtro = new FiltroEntregaConsultaDTO(pagina, super.getPageSize());
        filtro.setEstado(this.estado == null ? null : this.estado.name());
        filtro.setTipoEntrega(this.tipoEntrega);
        filtro.setTopico(this.topico);
        filtro.setSuscriptor(this.suscriptor);
        filtro.setNovedadId(this.uuid);
        filtro.setFechaDesde(this.fechaDesde);

        fechaHasta = DateFilterUtil.getToDate(fechaHasta);

        filtro.setFechaHasta(this.fechaHasta);

        ResultadoPaginadoDTO<Entrega> resultado = null;
        try {
            resultado = obtenerEntregaService().buscarEntregaFiltro(filtro);
        } catch (PSException e) {
            warn(e.getMessage());
        }

        cantRegistrosDescription(resultado);

        getEntregas().clear();

        if (pagina == null) {
            getEntregasSeleccion().clear();
        }

        if (resultado != null) {
            getEntregas().addAll(resultado.getResultado());
        }

        updateNavigator(pagina, resultado);

        if (getEntregas().isEmpty()) {
            if (this.topico == null || this.suscriptor == null) {
                super.setCeroCantRegistros();
            } else {
                getSession().success("No se encontraron resultados para la b\u00FAsqueda");
            }
            return;
        }

    }

    private List<Entrega> getEntregas() {
        if (this.entregas == null) {
            this.entregas = new ArrayList<>();
        }
        return this.entregas;
    }

    private List<Entrega> getEntregasSeleccion() {
        if (this.entregasSeleccion == null) {
            this.entregasSeleccion = new ArrayList<>();
        }
        return this.entregasSeleccion;
    }

    public void buscarEstados() {
        this.listaEstados = new ArrayList<>();
        this.listaEstados.add(EstadoEntrega.PENDIENTE);
        this.listaEstados.add(EstadoEntrega.CANCELADO);
        this.listaEstados.add(EstadoEntrega.ENVIADO);
    }

    public void buscarTipoEntrega() {
        this.listaTiposEntrega = new ArrayList<>();
        this.listaTiposEntrega.add(DeliveryMode.PUSH);
        this.listaTiposEntrega.add(DeliveryMode.PULL);
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

    private Boolean suscripcionPush(Entrega entrega) {
        TopicoSuscriptor ts = null;
        try {
            ts = obtenerTopicoSuscriptorService().buscarTopicoSuscriptor(entrega.getSuscriptor().getNombre(), entrega.getNovedad().getTopico().getNombre());
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
        return (ts != null ? ts.getDeliveryMode().equalsIgnoreCase(DeliveryMode.PUSH) : false);
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