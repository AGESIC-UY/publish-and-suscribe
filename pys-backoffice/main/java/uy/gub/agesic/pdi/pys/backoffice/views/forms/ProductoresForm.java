package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroProductorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConProductor;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Productor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@java.lang.SuppressWarnings({"squid:S1450", "squid:S1068"})
public class ProductoresForm extends BaseEntitiesForm {

    private static final long serialVersionUID = 1L;

    // Productores recuperados y seleccionados
    private ArrayList<Productor> productores;
    private ArrayList<Productor> productoresSeleccion;

    public ProductoresForm () {
        super("productoresForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        this.productores = new ArrayList<>();
        this.productoresSeleccion = new ArrayList<>();

        super.initForm();

        // Agregamos el cuerpo de la grilla
        CheckGroup<Productor> group = new CheckGroup<>("group", this.productoresSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        CheckGroupSelector groupSelector = new CheckGroupSelector("groupSelector");
        group.add(groupSelector);
        ProductoresDataView dataView = new ProductoresDataView("rows", new ListDataProvider<>(this.productores));
        group.add(dataView);

        buscar(null);
    }

    private class ProductoresDataView extends DataView<Productor> {

        public ProductoresDataView(String id, ListDataProvider<Productor> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Productor> item) {
            final Productor info = item.getModelObject();

            item.setModel(new CompoundPropertyModel<>(info));
            // Checkbox de seleccion
            Check<Productor> chkSelector = new Check<>(DATA_ROW_SELECTOR, item.getModel());
            chkSelector.add(new AjaxEventBehavior("onclick") {

                protected void onEvent(AjaxRequestTarget target) {
                    Productor productor = (Productor) this.getComponent().getDefaultModelObject();
                    evaluateEliminarButtonVisibility(target, productor.getNombre());
                }

            });
            item.add(chkSelector);


            // Enlace para acceder a la consulta/modificacion del servicio
            StatelessLink dataRowLink = new StatelessLink(DATA_ROW_LINK) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add(MODE_PARAM_NAME, ModoOperacion.CONSULTA);
                    parameters.add(NOMBRE_PARAMETER, info.getNombre());

                    // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
                    PageParameters parametrosFiltro = ProductoresForm.this.filtrosAParametrosPagina();
                    parameters.mergeWith(parametrosFiltro);

                    setResponsePage(TrabajarConProductor.class, parameters);
                }
            };
            dataRowLink.add(new Label(DATA_ROW_NOMBRE, info.getNombre()));
            item.add(dataRowLink);

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView(DATA_ROW);
            repeatingView.add(new Label(repeatingView.newChildId(), info.getDn()));
            String habilitadoStr = "NO";

            if (info.getHabilitado())
                habilitadoStr = "SI";

            repeatingView.add(new Label(repeatingView.newChildId(), habilitadoStr));

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            String fecha = info.getFechaCreacion() == null ? null : dateFormat.format(info.getFechaCreacion());
            repeatingView.add(new Label(repeatingView.newChildId(), fecha));

            item.add(repeatingView);
        }
    }

    @Override
    public void buscar(Integer pagina) {
        super.buscar(pagina);

        FiltroProductorDTO filtro = new FiltroProductorDTO(pagina, super.getPageSize());
        filtro.setNombre(this.nombre);
        filtro.setDn(this.dn);

        if (this.estado != null) {
            this.habilitado = this.estado.equalsIgnoreCase(HABILITADO_VALUE);
        } else {
            this.habilitado = null;
        }

        filtro.setHabilitado(this.habilitado);

        ResultadoPaginadoDTO<Productor> resultado = null;
        try {
            resultado = obtenerProductorService().buscarProductores(filtro);
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }

        super.cantRegistrosDescription(resultado);

        this.productores.clear();

        if (pagina == null) {
            this.productoresSeleccion.clear();
        }

        if (resultado != null) {
            this.productores.addAll(resultado.getResultado());
        }

        updateNavigator(pagina, resultado);

        if(productores.isEmpty()){
            getSession().success("No se encontraron resultados para la b\u00FAsqueda");
            return;
        }

    }

    @Override
    public void agregar() {
        PageParameters parameters = new PageParameters();
        parameters.add(MODE_PARAM_NAME, ModoOperacion.ALTA);
        parameters.add(NOMBRE_PARAMETER, "");

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConProductor.class, parameters);

    }

    @Override
    public void modificar() {
        if (this.productoresSeleccion == null || this.productoresSeleccion.isEmpty()) {
            showError("Productores.seleccion.vacia");
            return;
        }

        Productor productor = this.productoresSeleccion.get(0);

        PageParameters parameters = new PageParameters();
        parameters.add(MODE_PARAM_NAME, ModoOperacion.MODIFICACION);
        parameters.add(NOMBRE_PARAMETER, productor.getNombre());

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConProductor.class, parameters);
    }

    @Override
    public void eliminar() {
        if (this.productoresSeleccion == null || this.productoresSeleccion.isEmpty()) {
            showError("Productores.seleccion.vacia");
            return;
        }

        List<String> ids = this.productoresSeleccion.stream().map(Productor::getNombre).collect(Collectors.toList());

        try {
            this.obtenerProductorService().eliminarProductores(ids);

            this.showSuccess("Operacion.exitosa");
        } catch (PSException e) {
            showError("Productores.error.eliminar");
        }

    }

}
