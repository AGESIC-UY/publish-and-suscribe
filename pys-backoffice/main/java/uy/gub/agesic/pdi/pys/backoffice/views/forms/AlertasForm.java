package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
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
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroAlertaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListDateFilterForm;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConAlerta;
import uy.gub.agesic.pdi.pys.domain.Alerta;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.ArrayList;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class AlertasForm extends ListDateFilterForm {

    private static final long serialVersionUID = 1L;

    // Filtros
    private String uuid;
    private Topico topico;
    private Productor productor;
    private Suscriptor suscriptor;

    // Alertas recuperados y seleccionados
    private ArrayList<Alerta> alertas;
    private ArrayList<Alerta> alertasSeleccion;

    //Lista de productores y tópicos para cargar datos filtros
    private ArrayList<Productor> listaProductores;
    private ArrayList<Topico> listaTopicos;
    private ArrayList<Suscriptor> listaSuscriptores;

    public AlertasForm() {
        super("alertasForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        this.alertas = new ArrayList<>();
        this.alertasSeleccion = new ArrayList<>();
        this.listaProductores = new ArrayList<>();
        this.listaTopicos = new ArrayList<>();
        this.listaSuscriptores = new ArrayList<>();

        buscarProductores();
        buscarTopicos();
        buscarSuscriptores();

        DropDownChoice<Productor> productorFiltro = new DropDownChoice<>("productor", new PropertyModel<>(this, "productor"), listaProductores);
        productorFiltro.setNullValid(true);
        productorFiltro.setOutputMarkupId(true);
        productorFiltro.setOutputMarkupPlaceholderTag(true);
        productorFiltro.setLabel(new Model<>("Productor"));
        this.add(productorFiltro);

        DropDownChoice<Suscriptor> suscriptorFiltro = new DropDownChoice<>("suscriptor", new PropertyModel<>(this, "suscriptor"), listaSuscriptores);
        suscriptorFiltro.setNullValid(true);
        suscriptorFiltro.setOutputMarkupId(true);
        suscriptorFiltro.setOutputMarkupPlaceholderTag(true);
        suscriptorFiltro.setLabel(new Model<>("Suscriptor"));
        this.add(suscriptorFiltro);

        DropDownChoice<Topico> topicoFiltro = new DropDownChoice<>("topico", new PropertyModel<>(this, "topico"), listaTopicos);
        topicoFiltro.setNullValid(true);
        topicoFiltro.setLabel(new Model<>("Tópico"));
        this.add(topicoFiltro);

        topicoFiltro.add(new TopicFilterUpdatingBehavior(suscriptorFiltro, productorFiltro));

        FormComponent<String> uuidField = new TextField<>("uuid");
        this.add(uuidField);

        addDateTimeFilters();

        agregarBotones(false);

        // Agregamos el cuerpo de la grilla
        CheckGroup<Alerta> group = new CheckGroup<>("group", this.alertasSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        AlertasDataView dataView = new AlertasDataView("rows", new ListDataProvider<>(this.alertas));
        group.add(dataView);

        buscar(null);

    }

    private class AlertasDataView extends DataView<Alerta> {

        public AlertasDataView(String id, ListDataProvider<Alerta> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Alerta> item) {
            final Alerta info = item.getModelObject();

            // Enlace para acceder a la consulta/modificacion del servicio
            StatelessLink dataRowLink = new StatelessLink("dataRowLink") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(MODE_PARAM_NAME, ModoOperacion.CONSULTA);
                    parameters.add("idAlertaParameter", info.getId());

                    // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
                    PageParameters parametrosFiltro = AlertasForm.this.filtrosAParametrosPagina();
                    parameters.mergeWith(parametrosFiltro);

                    setResponsePage(TrabajarConAlerta.class, parameters);
                }
            };
            item.add(dataRowLink);

            dataRowLink.add(new Label("dataRowId", info.getUuid()));

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView(DATA_ROW);

            String nombreProductor = null;
            String nombreTopico = null;
            String nombreSuscriptor = null;

            if(info.getProductor() != null){
                nombreProductor = info.getProductor().getNombre();
            }

            if(info.getTopico() != null){
                nombreTopico = info.getTopico().getNombre();
            }

            if(info.getSuscriptor() != null){
                nombreSuscriptor = info.getSuscriptor().getNombre();
            }

            repeatingView.add(new Label(repeatingView.newChildId(), info.getError()));
            repeatingView.add(new Label(repeatingView.newChildId(), nombreTopico));
            repeatingView.add(new Label(repeatingView.newChildId(), nombreSuscriptor));
            repeatingView.add(new Label(repeatingView.newChildId(), nombreProductor));

            String fecha = info.getFecha() != null ? dateFormat.format(info.getFecha()) : "";
            repeatingView.add(new Label(repeatingView.newChildId(), fecha));

            item.add(repeatingView);
        }
    }


    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();

        if (this.productor != null && !this.productor.getNombre().trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE_PRODUCTOR, this.productor.getNombre());
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

    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);

        try {
            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE_TOPICO)) {
                String nombreTopico = parameters.get(FILTRO_NOMBRE_TOPICO).toString();
                this.topico = getEntity(listaTopicos, nombreTopico);
                filtroCargado = this.topico != null || filtroCargado;
                if (this.topico != null) {
                    buscarSuscriptoresTopico();
                    buscarProductoresTopico();
                }
            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE_SUSCRIPTOR)) {
                String nombreSuscriptor = parameters.get(FILTRO_NOMBRE_SUSCRIPTOR).toString();
                this.suscriptor = getEntity(listaSuscriptores, nombreSuscriptor);
                filtroCargado = this.suscriptor != null || filtroCargado;
            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE_PRODUCTOR)) {
                String nombreProductor = parameters.get(FILTRO_NOMBRE_PRODUCTOR).toString();
                this.productor = getEntity(listaProductores, nombreProductor);
                filtroCargado = this.productor != null || filtroCargado;
            }

            if (parameters.getNamedKeys().contains(FILTRO_UUID)) {
                this.uuid = parameters.get(FILTRO_UUID).toString();
                filtroCargado = true;
            }

        } catch (Exception ex) {
            filtroCargado = false;
        }

        return (filtroCargado);
    }

    @Override
    public void limpiarFiltros() {
        this.get("uuid").getDefaultModel().setObject(null);
        this.get("dateDesde").getDefaultModel().setObject(null);
        this.get("dateHasta").getDefaultModel().setObject(null);
        this.productor = null;
        this.topico = null;
        this.suscriptor = null;

        buscarProductores();
        buscarSuscriptores();
    }

    @Override
    public void buscar(Integer pagina) {

        FiltroAlertaConsultaDTO filtro = new FiltroAlertaConsultaDTO(pagina, getPageSize());
        filtro.setProductor(this.productor);
        filtro.setTopico(this.topico);
        filtro.setSuscriptor(this.suscriptor);
        filtro.setNovedadId(this.uuid);

        filtro.setFechaDesde(this.fechaDesde);

        fechaHasta = DateFilterUtil.getToDate(fechaHasta);

        filtro.setFechaHasta(this.fechaHasta);

        ResultadoPaginadoDTO<Alerta> resultado = null;
        try {
            resultado = obtenerAlertaService().buscarAlertaFiltro(filtro);
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }

        cantRegistrosDescription(resultado);

        this.alertas.clear();

        if (pagina == null) {
            this.alertasSeleccion.clear();
        }

        if (resultado != null) {
            this.alertas.addAll(resultado.getResultado());
        }

        updateNavigator(pagina, resultado);

        if(alertas.isEmpty()){
            getSession().success("No se encontraron resultados para la b\u00FAsqueda");
            return;
        }

    }

    public void buscarProductores() {
        super.buscarProductores(this.listaProductores);
    }

    public void buscarProductoresTopico() {
        super.buscarProductoresTopico(this.listaProductores, topico.getNombre());
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

    private class TopicFilterUpdatingBehavior extends AjaxFormComponentUpdatingBehavior {

        private final DropDownChoice<Suscriptor> suscriptorFiltro;
        private final DropDownChoice<Productor> productorFiltro;

        public TopicFilterUpdatingBehavior(DropDownChoice<Suscriptor> suscriptorFiltro, DropDownChoice<Productor> productorFiltro) {
            super("onchange");
            this.suscriptorFiltro = suscriptorFiltro;
            this.productorFiltro = productorFiltro;
        }

        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            if (target != null) {
                target.add(suscriptorFiltro);
                target.add(productorFiltro);

                if (topico != null) {
                    buscarSuscriptoresTopico();
                    buscarProductoresTopico();
                } else {
                    buscarSuscriptores();
                    buscarProductores();
                }

                suscriptorFiltro.setChoices(listaSuscriptores);
                productorFiltro.setChoices(listaProductores);
            }
        }
    }

}