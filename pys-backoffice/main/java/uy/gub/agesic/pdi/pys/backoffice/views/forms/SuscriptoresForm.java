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
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroSuscriptorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConSuscriptor;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@java.lang.SuppressWarnings({"squid:S1450", "squid:S1068"})
public class SuscriptoresForm extends BaseEntitiesForm {

    private static final long serialVersionUID = 1L;

    // Suscriptores recuperados y seleccionados
    private ArrayList<Suscriptor> suscriptores;
    private ArrayList<Suscriptor> suscriptoresSeleccion;

    public SuscriptoresForm () {
        super("suscriptoresForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        this.suscriptores = new ArrayList<>();
        this.suscriptoresSeleccion = new ArrayList<>();

        super.initForm();

        // Agregamos el cuerpo de la grilla
        CheckGroup<Suscriptor> group = new CheckGroup<>("group", this.suscriptoresSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        CheckGroupSelector groupSelector = new CheckGroupSelector("groupSelector");
        group.add(groupSelector);
        SuscriptoresDataView dataView = new SuscriptoresDataView("rows", new ListDataProvider<>(this.suscriptores));
        group.add(dataView);

        buscar(null);
    }

    private class SuscriptoresDataView extends DataView<Suscriptor> {

        public SuscriptoresDataView (String id, ListDataProvider<Suscriptor> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Suscriptor> item) {
            final Suscriptor info = item.getModelObject();

            // Checkbox de seleccion
            Check<Suscriptor> chkSelector = new Check<>(DATA_ROW_SELECTOR, item.getModel());
            chkSelector.add(new AjaxEventBehavior("onclick") {

                protected void onEvent(AjaxRequestTarget target) {
                    Suscriptor suscriptor = (Suscriptor) this.getComponent().getDefaultModelObject();
                    evaluateEliminarButtonVisibility(target, suscriptor.getNombre());
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
                    PageParameters parametrosFiltro = SuscriptoresForm.this.filtrosAParametrosPagina();
                    parameters.mergeWith(parametrosFiltro);

                    setResponsePage(TrabajarConSuscriptor.class, parameters);
                }
            };
            item.add(dataRowLink);

            dataRowLink.add(new Label(DATA_ROW_NOMBRE, info.getNombre()));

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView(DATA_ROW);
            repeatingView.add(new org.apache.wicket.markup.html.basic.Label(repeatingView.newChildId(), info.getDn()));

            String habilitadoStr = "NO";

            if (info.getHabilitado())
                habilitadoStr = "SI";

            repeatingView.add(new org.apache.wicket.markup.html.basic.Label(repeatingView.newChildId(), habilitadoStr));

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            String fecha = info.getFechaCreacion() == null ? null : dateFormat.format(info.getFechaCreacion());
            repeatingView.add(new Label(repeatingView.newChildId(), fecha));

            item.add(repeatingView);
        }
    }

    @Override
    public void buscar(Integer pagina) {
        super.buscar(pagina);

        FiltroSuscriptorDTO filtro = new FiltroSuscriptorDTO(pagina, super.getPageSize());
        filtro.setNombre(this.nombre);
        filtro.setDn(this.dn);

        if (this.estado != null) {
            this.habilitado = this.estado.equalsIgnoreCase(HABILITADO_VALUE);
        } else {
            this.habilitado = null;
        }

        filtro.setHabilitado(this.habilitado);

        ResultadoPaginadoDTO<Suscriptor> resultado = null;
        try {
            resultado = obtenerSuscriptorService().buscarSuscriptores(filtro);
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }

        super.cantRegistrosDescription(resultado);

        this.suscriptores.clear();

        if (pagina == null) {
            this.suscriptoresSeleccion.clear();
        }

        if (resultado != null) {
            this.suscriptores.addAll(resultado.getResultado());
        }

        updateNavigator(pagina, resultado);

        if (suscriptores.isEmpty()) {
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

        setResponsePage(TrabajarConSuscriptor.class, parameters);

    }

    @Override
    public void modificar() {
        if (this.suscriptoresSeleccion == null || this.suscriptoresSeleccion.isEmpty()) {
            showError("Suscriptores.seleccion.vacia");
            return;
        }

        Suscriptor suscriptor = this.suscriptoresSeleccion.get(0);

        PageParameters parameters = new PageParameters();
        parameters.add(MODE_PARAM_NAME, ModoOperacion.MODIFICACION);
        parameters.add(NOMBRE_PARAMETER, suscriptor.getNombre());

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConSuscriptor.class, parameters);
    }

    @Override
    public void eliminar() {
        if (this.suscriptoresSeleccion == null || this.suscriptoresSeleccion.isEmpty()) {
            showError("Suscriptores.seleccion.vacia");
            return;
        }

        List<String> ids = this.suscriptoresSeleccion.stream().map(Suscriptor::getNombre).collect(Collectors.toList());

        try {
            this.obtenerSuscriptorService().eliminarSuscriptores(ids);

            this.showSuccess("Operacion.exitosa");
        } catch (PSException e) {
            showError("Suscriptores.error.eliminar");
        }

    }

}
