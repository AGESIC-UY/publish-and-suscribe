package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroUsuarioDTO;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.ListBackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.TrabajarConUsuario;
import uy.gub.agesic.pdi.pys.domain.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class UsuariosForm extends ListBackofficeForm {

    private static final long serialVersionUID = 1L;

    private String login;
    
    private String nombre;

    private String apellido;

    // Rutas recuperadas y seleccionados
    private List<Usuario> usuarios;
    private List<Usuario> usuariosSeleccion;

    public UsuariosForm() {
        super("usuariosForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        this.usuarios = new ArrayList<>();
        this.usuariosSeleccion = new ArrayList<>();

        FormComponent<String> loginField = new TextField<>("login");
        loginField.setLabel(new Model("Usuario"));
        this.add(loginField);

        FormComponent<String> nombreField = new TextField<>("nombre");
        nombreField.setLabel(new Model("Nombre"));
        this.add(nombreField);

        FormComponent<String> apellidoField = new TextField<>("apellido");
        apellidoField.setLabel(new Model("Apellido"));
        this.add(apellidoField);

        agregarBotones(true);

        BackofficeProperties properties = ((BackofficePage)this.getPage()).getProperties();

        eliminarButton = new UsuariosForm.EliminarButton(this.getFinalMessage(properties.getMensajeConfirmacion()));
        this.add(eliminarButton);

        // Agregamos el cuerpo de la grilla
        CheckGroup<Usuario> group = new CheckGroup<>("group", this.usuariosSeleccion);
        group.setOutputMarkupId(true);
        this.add(group);

        CheckGroupSelector groupSelector = new CheckGroupSelector("groupSelector");
        group.add(groupSelector);
        UsuariosForm.UsuariosDataView dataView = new UsuariosForm.UsuariosDataView("rows", new ListDataProvider<Usuario>(this.usuarios));
        group.add(dataView);

        if(permiso != null && permiso.equals(PermisoUsuario.LECTURA.name())) {
            this.get("agregar").setVisible(false);
            this.get("modificar").setVisible(false);
        }

        buscar(null);

    }

    private class UsuariosDataView extends DataView<Usuario> {

        public UsuariosDataView(String id, ListDataProvider<Usuario> dataProvider) {
            super(id, dataProvider);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected void populateItem(Item<Usuario> item) {
            final Usuario info = item.getModelObject();

            // Checkbox de seleccion
            Check<Usuario> chkSelector = new Check<>("dataRowSelector", item.getModel());
            chkSelector.add(new AjaxEventBehavior("onclick") {

                protected void onEvent(AjaxRequestTarget target) {
                    Usuario usuario = (Usuario) this.getComponent().getDefaultModelObject();
                    evaluateEliminarButtonVisibility(target, usuario.getNombre());
                }

            });
            item.add(chkSelector);

            // Enlace para acceder a la consulta/modificacion del servicio
            StatelessLink dataRowLink = new StatelessLink("dataRowLink") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    PageParameters parameters = new PageParameters();
                    parameters.add("modo", ModoOperacion.CONSULTA);
                    parameters.add(LOGIN_PARAMETER, info.getLogin());

                    // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
                    PageParameters parametrosFiltro = UsuariosForm.this.filtrosAParametrosPagina();
                    parameters.mergeWith(parametrosFiltro);

                    setResponsePage(TrabajarConUsuario.class, parameters);
                }
            };
            item.add(dataRowLink);

            dataRowLink.add(new Label("dataRowLogin", info.getLogin()));

            // Datos puramente visuales
            RepeatingView repeatingView = new RepeatingView("dataRow");
            repeatingView.add(new Label(repeatingView.newChildId(), info.getNombre()));
            repeatingView.add(new Label(repeatingView.newChildId(), info.getApellido()));

            String admin = "NO";

            if (info.getPermiso().equals(PermisoUsuario.ESCRITURA.name())) {
                admin = "SI";
            }

            repeatingView.add(new Label(repeatingView.newChildId(),admin));

            item.add(repeatingView);
        }
    }

    @Override
    public void buscar(Integer pagina) {
        super.buscar(pagina);

        FiltroUsuarioDTO filtro = new FiltroUsuarioDTO(pagina, super.getPageSize());
        filtro.setLogin(this.login);
        filtro.setNombre(this.nombre);
        filtro.setApellido(this.apellido);

        ResultadoPaginadoDTO<Usuario> resultado = null;
        try {
            resultado = this.obtenerUsuarioService().buscarUsuarios(filtro);
        } catch (BackofficeException e) {
            warn(e.getMessage());
        }

        super.cantRegistrosDescription(resultado);

        this.usuarios.clear();

        if (pagina == null) {
            this.usuariosSeleccion.clear();
        }

        if (resultado != null) {
            this.usuarios.addAll(resultado.getResultado());
        }

        updateNavigator(pagina, resultado);

        if(usuarios.isEmpty()) {
            getSession().success("No se encontraron resultados para la b\u00FAsqueda");
            return;
        }

    }

    @Override
    public void agregar() {
        PageParameters parameters = new PageParameters();
        parameters.add("modo", ModoOperacion.ALTA);
        parameters.add(LOGIN_PARAMETER, "");

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConUsuario.class, parameters);
    }

    @Override
    public void modificar() {
        if (this.usuariosSeleccion == null || this.usuariosSeleccion.isEmpty()) {
            showError("Usuarios.seleccion.vacia");
            return;
        }

        Usuario usuario = this.usuariosSeleccion.get(0);

        PageParameters parameters = new PageParameters();
        parameters.add("modo", ModoOperacion.MODIFICACION);
        parameters.add(LOGIN_PARAMETER, usuario.getLogin());

        // Se cargan los filtros en forma de parametros para utilizarlos a la vuelta y se anexan a los parametros pertenecientes a la siguiente pagina
        PageParameters parametrosFiltro = this.filtrosAParametrosPagina();
        parameters.mergeWith(parametrosFiltro);

        setResponsePage(TrabajarConUsuario.class, parameters);

    }

    @Override
    public void eliminar() {
        if (this.usuariosSeleccion == null || this.usuariosSeleccion.isEmpty()) {
            showError("Usuarios.seleccion.vacia");
            return;
        }

        List<String> logins = this.usuariosSeleccion.stream().map(Usuario::getLogin).collect(Collectors.toList());

        if(logins.contains(logedUser)){
            showError("Usuarios.error.eliminar.admin");
            return;
        }

        try {

            this.obtenerUsuarioService().eliminarUsuarios(logins);
            this.showSuccess("Operacion.exitosa");
        } catch (BackofficeException e) {
            showError("Usuarios.error.eliminar");
        }
    }

    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();

        if (this.login != null && !this.login.trim().isEmpty()) {
            parameters.add(FILTRO_LOGIN, this.login);
        }

        if (this.nombre != null && !this.nombre.trim().isEmpty()) {
            parameters.add(FILTRO_NOMBRE, this.nombre);
        }

        if (this.apellido != null && !this.apellido.trim().isEmpty()) {
            parameters.add(FILTRO_APELLIDO, this.apellido);
        }

        return (parameters);
    }

    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);

        try {
            if (parameters.getNamedKeys().contains(FILTRO_LOGIN)) {
                this.login = parameters.get(FILTRO_LOGIN).toString();
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_NOMBRE)) {
                this.nombre = parameters.get(FILTRO_NOMBRE).toString();
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_APELLIDO)) {
                this.apellido = parameters.get(FILTRO_APELLIDO).toString();
                filtroCargado = true;
            }
        } catch (Exception ex) {
            filtroCargado = false;
        }

        return (filtroCargado);
    }

    @Override
    public void limpiarFiltros() {
        this.get("login").getDefaultModel().setObject(null);
        this.get("nombre").getDefaultModel().setObject(null);
        this.get("apellido").getDefaultModel().setObject(null);
    }

}