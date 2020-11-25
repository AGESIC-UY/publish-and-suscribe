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
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroNovedadConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.dtos.NovedadDTO;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListDateFilterForm;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConNovedad;
import uy.gub.agesic.pdi.pys.domain.Novedad;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.ArrayList;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class NovedadesForm extends ListDateFilterForm {

    private static final long serialVersionUID = 1L;

    // Filtros
    private String uuidNovedad;
    private Topico topico;
    private Productor productor;

    // Novedades recuperados y seleccionados
    private ArrayList<NovedadDTO> novedades;
    private ArrayList<NovedadDTO> novedadesSeleccion;

    //Lista de productores y tópicos para cargar datos filtros
    private ArrayList<Productor> listaProductores;
    private ArrayList<Topico> listaTopicos;

    public NovedadesForm () {
        super("novedadesForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        this.novedades = new ArrayList<>();
        this.novedadesSeleccion = new ArrayList<>();
        this.listaProductores = new ArrayList<>();
        this.listaTopicos = new ArrayList<>();

        buscarProductores();
        buscarTopicos();

        DropDownChoice<Productor> productorFiltro = new DropDownChoice<>("productor", new PropertyModel<>(this, "productor"), listaProductores);
        productorFiltro.setNullValid(true);
        productorFiltro.setOutputMarkupId(true);
        productorFiltro.setOutputMarkupPlaceholderTag(true);
        productorFiltro.setLabel(new Model<>("Productor"));
        this.add(productorFiltro);

        DropDownChoice<Topico> topicoFiltro = new DropDownChoice<>("topico", new PropertyModel<>(this, "topico"), listaTopicos);
        topicoFiltro.setNullValid(true);
        topicoFiltro.setLabel(new Model<>("Tópico"));
        this.add(topicoFiltro);

        topicoFiltro.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (target != null) {
                    target.add(productorFiltro);

                    if (topico != null) {
                        buscarProductoresTopico();
                    } else {
                        buscarProductores();
                    }

                    productorFiltro.setChoices(listaProductores);
                }
            }
        });

        FormComponent<String> uuidNovedadField = new TextField<>("uuidNovedad");
        this.add(uuidNovedadField);

        addDateTimeFilters();

        agregarBotones(false);

        // Agregamos el cuerpo de la grilla
        CheckGroup<NovedadDTO> group = new CheckGroup<>("group", this.novedadesSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        NovedadesDataView dataView = new NovedadesDataView("rows", new ListDataProvider<>(this.novedades));
        group.add(dataView);

        buscar(null);

    }

    private class NovedadesDataView extends DataView<NovedadDTO> {

        public NovedadesDataView(String id, ListDataProvider<NovedadDTO> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<NovedadDTO> item) {
            final NovedadDTO info = item.getModelObject();

            // Enlace para acceder a la consulta/modificacion del servicio
            StatelessLink dataRowLink = new StatelessLink(DATA_ROW_LINK) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(MODE_PARAM_NAME, ModoOperacion.CONSULTA);
                    parameters.add("uuidNovedadParameter", info.getUuid());

                    // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
                    PageParameters parametrosFiltro = NovedadesForm.this.filtrosAParametrosPagina();
                    parameters.mergeWith(parametrosFiltro);

                    setResponsePage(TrabajarConNovedad.class, parameters);
                }
            };
            item.add(dataRowLink);

            dataRowLink.add(new Label("dataRowId", info.getUuid()));

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView("dataRow");

            String nombreProductor = null;
            String nombreTopico = null;

            if (info.getProductor() != null) {
                nombreProductor = info.getProductor();
            }

            if (info.getTopico() != null) {
                nombreTopico = info.getTopico();
            }

            repeatingView.add(new Label(repeatingView.newChildId(), nombreProductor));
            repeatingView.add(new Label(repeatingView.newChildId(), nombreTopico));

            String fecha = info.getFecha() != null ? dateFormat.format(info.getFecha()) : null;
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

        if (this.uuidNovedad != null && !this.uuidNovedad.trim().isEmpty()) {
            parameters.add(FILTRO_UUID_NOVEDAD, this.uuidNovedad);
        }

        return (parameters);
    }

    @SuppressWarnings("squid:S3776")
    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);

        try {
            if (parameters.getNamedKeys().contains(FILTRO_UUID_NOVEDAD)) {
                this.uuidNovedad = parameters.get(FILTRO_UUID_NOVEDAD).toString();
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE_TOPICO)) {
                String nombreTopico = parameters.get(FILTRO_NOMBRE_TOPICO).toString();
                this.topico = getEntity(listaTopicos, nombreTopico);
                filtroCargado = this.topico != null || filtroCargado;
                if (this.topico != null) {
                    buscarProductoresTopico();
                }
            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE_PRODUCTOR)) {
                String nombreProductor = parameters.get(FILTRO_NOMBRE_PRODUCTOR).toString();
                this.productor = getEntity(listaProductores, nombreProductor);
                filtroCargado = this.productor != null || filtroCargado;
            }

        } catch (Exception ex) {
            filtroCargado = false;
        }

        return (filtroCargado);
    }

    @Override
    public void limpiarFiltros() {
        this.get("uuidNovedad").getDefaultModel().setObject(null);
        this.get("dateDesde").getDefaultModel().setObject(null);
        this.get("dateHasta").getDefaultModel().setObject(null);
        this.productor = null;
        this.topico = null;
        buscarProductores();
    }

    @Override
    public void buscar(Integer pagina) {

        FiltroNovedadConsultaDTO filtro = new FiltroNovedadConsultaDTO(pagina, super.getPageSize());
        filtro.setProductor(this.productor == null ? null : this.productor);
        filtro.setTopico(this.topico == null ? null : this.topico);
        filtro.setUuidNovedad(this.uuidNovedad);
        filtro.setFechaDesde(this.fechaDesde);

        fechaHasta = DateFilterUtil.getToDate(fechaHasta);

        filtro.setFechaHasta(this.fechaHasta);

        ResultadoPaginadoDTO<Novedad> resultado = null;
        try {
            resultado = obtenerNovedadService().buscarNovedadesFiltro(filtro);
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }

        cantRegistrosDescription(resultado);

        this.novedades.clear();

        if (pagina == null) {
            this.novedadesSeleccion.clear();
        }

        if (resultado != null) {
            for (Novedad novedad : resultado.getResultado()) {
                NovedadDTO dto = new NovedadDTO();
                dto.setFecha(novedad.getFecha());
                dto.setId(novedad.getId());
                dto.setProductor(novedad.getProductor() == null ? null : novedad.getProductor().getNombre());
                dto.setTopico(novedad.getTopico() == null ? null : novedad.getTopico().getNombre());
                dto.setUuid(novedad.getUuid());
                this.novedades.add(dto);
            }
        }

        updateNavigator(pagina, resultado);

        if (novedades.isEmpty()) {
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

}