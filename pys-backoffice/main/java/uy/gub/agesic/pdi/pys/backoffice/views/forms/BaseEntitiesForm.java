package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListBackofficeForm;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"squid:S1450", "squid:S1068"})
public class BaseEntitiesForm extends ListBackofficeForm {

    private static final long serialVersionUID = 1L;

    // Filtros
    protected String nombre;
    protected String dn;

    protected Boolean habilitado;
    protected static final List<String> estados = Arrays.asList(HABILITADO_VALUE, DESHABILITADO_VALUE);
    protected String estado;

    public BaseEntitiesForm(String id) {
        super(id);
    }

    @Override
    public void initForm() {

        super.initForm();

        FormComponent<String> nombreField = new TextField<>("nombre");
        this.add(nombreField);

        FormComponent<String> dnField = new TextField<>("dn");
        this.add(dnField);

        DropDownChoice<String> habilitadoChoice = new DropDownChoice<>(
                HABILITADO_NAME, new PropertyModel<>(this, "estado"), estados);
        habilitadoChoice.setLabel(new Model<>("Estado"));
        habilitadoChoice.setNullValid(true);
        this.add(habilitadoChoice);

        agregarBotones(true);

        BackofficeProperties properties = ((BackofficePage)this.getPage()).getProperties();
        eliminarButton = new BaseEntitiesForm.EliminarButton(this.getFinalMessage(properties.getMensajeConfirmacion()));
        this.add(eliminarButton);

        if(permiso != null && permiso.equals(PermisoUsuario.LECTURA.name())) {
            this.get("agregar").setVisible(false);
            this.get("modificar").setVisible(false);
        }

    }

    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();

        if (this.nombre != null && !this.nombre.trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE, this.nombre);
        }

        if (this.dn != null && !this.dn.trim().isEmpty()) {
            parameters.add(FILTRO_DN, this.dn);
        }

        if (this.habilitado != null) {
            parameters.add(FILTRO_HABILITADO, this.habilitado);
        }

        return (parameters);
    }

    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);

        try {
            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE)) {
                this.nombre = parameters.get(FILTRO_NOMBRE).toString();
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_DN)) {
                this.dn = parameters.get(FILTRO_DN).toString();
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_HABILITADO)) {
                this.habilitado = Boolean.valueOf(parameters.get(FILTRO_HABILITADO).toString());
                filtroCargado = true;
            }

        } catch (Exception ex) {
            filtroCargado = false;
        }

        return filtroCargado;
    }

    @Override
    public void limpiarFiltros() {
        this.get("nombre").getDefaultModel().setObject(null);
        this.get("dn").getDefaultModel().setObject(null);
        this.get(HABILITADO_NAME).getDefaultModel().setObject(null);
        buscar(null);
    }

}
