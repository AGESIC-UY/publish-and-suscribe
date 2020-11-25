package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backoffice.services.UserService;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.views.Error;
import uy.gub.agesic.pdi.pys.backoffice.views.Usuarios;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Usuario;

import java.util.regex.Pattern;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class TrabajarConUsuarioForm extends BackofficeForm {

    // Parametros
    private String login;
    private String nombre;
    private String apellido;
    private String password;
    private Boolean admin;

    public TrabajarConUsuarioForm() {
        super("trabajarConUsuarioForm");
    }

    @Override
    public void initForm() {

        this.setDefaultModel(new CompoundPropertyModel<>(this));

        final FormComponent<String> loginCmp = new TextField<String>(LOGIN_CMP).setRequired(true);
        loginCmp.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("loginFeedback", new ComponentFeedbackMessageFilter(loginCmp)));
        loginCmp.setLabel(new Model("Usuario"));
        this.add(loginCmp);

        final FormComponent<String> nombreCmp = new TextField<String>("nombre").setRequired(false);
        nombreCmp.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("nombreFeedback", new ComponentFeedbackMessageFilter(nombreCmp)));
        nombreCmp.setLabel(new Model("Nombre"));
        this.add(nombreCmp);

        final FormComponent<String> apellidoCmp = new TextField<String>("apellido").setRequired(false);
        apellidoCmp.add(StringValidator.maximumLength(200));
        this.add(new ComponentCustomFeedbackPanel("apellidoFeedback", new ComponentFeedbackMessageFilter(apellidoCmp)));
        apellidoCmp.setLabel(new Model("Apellido"));
        this.add(apellidoCmp);

        final FormComponent<String> passwordCmp = new PasswordTextField(PASSWORD_CMP).setRequired(true);
        passwordCmp.add(StringValidator.maximumLength(15));
        this.add(new ComponentCustomFeedbackPanel("passwordFeedback", new ComponentFeedbackMessageFilter(passwordCmp)));
        passwordCmp.setLabel(new Model("Contraseña"));
        this.add(passwordCmp);

        this.add(new Label(LABEL_PASSWORD, "Contraseña *"));

        final CheckBox adminCmp = new CheckBox(ADMIN_CMP, new PropertyModel<>(this, ADMIN_CMP));
        this.add(new ComponentCustomFeedbackPanel("adminFeedback", new ComponentFeedbackMessageFilter(adminCmp)));
        this.add(adminCmp);

        // Guardamos cambios
        this.add(new TrabajarConUsuarioForm.GuardarButton());

        // Se agrega el link de volver
        this.add(new TrabajarConUsuarioForm.LinkVolver());

    }

    @Override
    public void setParametersInner(PageParameters parameters) {

        // Se cargan los datos del filtro de la pagina anterior en el link de volver, por si se quiere volver al mismo lugar de donde se vino
        ((TrabajarConUsuarioForm.LinkVolver) this.get(LINK_VOLVER_NAME)).setParametersCallback(this.buscarParametrosDeFiltro(parameters));

        if (parameters.getNamedKeys().contains(MODE_PARAM_NAME)) {
            this.modo = parameters.get(MODE_PARAM_NAME).toEnum(ModoOperacion.class);
        }
        if (parameters.getNamedKeys().contains("loginParameter")) {
            this.login = parameters.get("loginParameter").toString();
        }

        if (!this.modo.equals(ModoOperacion.ALTA)) {
            this.definirValoresIniciales();
        }

        // Si el modo es consulta, definir componentes como Readonly
        if (this.modo.equals(ModoOperacion.CONSULTA)) {
            this.get(LOGIN_CMP).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get("nombre").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get("apellido").add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(PASSWORD_CMP).setVisible(false);
            this.get(LABEL_PASSWORD).setVisible(false);
            Component cmp = this.get(ADMIN_CMP);
            cmp.add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            cmp.add(new AttributeModifier("onclick", "return false;"));
        }

    }

    private class GuardarButton extends BotonAccion {

        public GuardarButton() {
            super("btnGuardar", Error.class, false, null);
        }

        @Override
        public boolean poseePermisoEjecucion() {
            // El control en este caso ya fue realizado al ingresar a la pagina, segun el modo apropiado
            return true;
        }

        @Override
        public boolean isVisible() {
            return !TrabajarConUsuarioForm.this.modo.equals(ModoOperacion.CONSULTA);
        }

        @Override
        public void ejecutar() {
          guardarUsuario();
        }
    }

    private class LinkVolver extends StatelessLink {

        private PageParameters parametersCallback;

        public LinkVolver() {
            super(LINK_VOLVER_NAME);
        }

        @Override
        public void onClick() {
            setResponsePage(Usuarios.class, parametersCallback);
        }

        public PageParameters getParametersCallback() {
            return parametersCallback;
        }

        public void setParametersCallback(PageParameters parametersCallback) {
            this.parametersCallback = parametersCallback;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @java.lang.SuppressWarnings("squid:S3776")
    protected void guardarUsuario () {
        try {
            UserService usuarioService = this.obtenerUsuarioService();

            if (ModoOperacion.ALTA.equals(this.modo) || ModoOperacion.MODIFICACION.equals(this.modo)) {
                Usuario usuario = ModoOperacion.ALTA.equals(this.modo) ? new Usuario() : usuarioService.obtenerUsuario(this.login);

                usuario.setLogin(this.login);
                usuario.setNombre(this.nombre);
                usuario.setApellido(this.apellido);
                usuario.setPassword(this.password);

                usuario.setPermiso(admin ? PermisoUsuario.ESCRITURA.name() : PermisoUsuario.LECTURA.name());

                String regex = Constants.PATRON_NOMBRE;

                if(!Pattern.matches(regex, login) || (!Pattern.matches(regex, nombre)) || (!Pattern.matches(regex, apellido))){
                    showError("Datos inv\u00E1lidos, verifique los campos ingresados");
                    return;
                }

                if (ModoOperacion.ALTA.equals(this.modo)) {

                    if(usuarioService.obtenerUsuario(this.login) != null) {
                        this.showError("Ya existe el usuario: " + this.login);
                        return;
                    }

                    usuarioService.crearUsuario (usuario);
                    getSession().success("Usuario creado exitosamente");
                    setResponsePage(Usuarios.class);

                } else {
                    getSession().success("Usuario modificado exitosamente");
                    setResponsePage(Usuarios.class);
                    usuarioService.modificarUsuario (usuario);
                }
                // Cambio al modo a edicion
                this.modo = ModoOperacion.MODIFICACION;
                this.login = usuario.getLogin();
                this.definirValoresIniciales();
            }
        } catch (BackofficeException ex) {
            this.showError(ex);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            showError("Datos inválidos, verifique los campos Usuario, Nombre y Apellido");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // AUXILIARES
    private void definirValoresIniciales() {
        try {
            Usuario usuario = this.obtenerUsuarioService().obtenerUsuario(this.login);

            this.login = usuario.getLogin();
            this.nombre = usuario.getNombre();
            this.apellido = usuario.getApellido();
            this.password = usuario.getPassword();

            this.admin = usuario.getPermiso().equals(PermisoUsuario.ESCRITURA.name());

            this.get(LOGIN_CMP).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(PASSWORD_CMP).setVisible(false);
            this.get(LABEL_PASSWORD).setVisible(false);

        } catch (BackofficeException e) {
            this.showError(e);
        }

    }

}


