package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.sesion.BackofficeAuthenticationSession;

@java.lang.SuppressWarnings("squid:S1068")
public class PasswordForm extends BackofficeForm {

    private static final long serialVersionUID = 1L;

    private String login;

    private String password;

    private String newPassword;

    public PasswordForm() {
        super("passwordForm");
    }

    @Override
    public void initForm() {
        this.setDefaultModel(new CompoundPropertyModel<>(this));

        super.initForm();

        final FormComponent<String> loginCmp = new TextField<>("login");
        loginCmp.add(StringValidator.maximumLength(200));
        loginCmp.setEnabled(false);
        this.add(new ComponentCustomFeedbackPanel("loginFeedback", new ComponentFeedbackMessageFilter(loginCmp)));
        loginCmp.setLabel(new Model("Usuario"));
        this.add(loginCmp);

        FormComponent<String> passwordCmp = new PasswordTextField("password").setRequired(true);
        this.add(new ComponentCustomFeedbackPanel("passwordFeedback", new ComponentFeedbackMessageFilter(passwordCmp)));
        passwordCmp.setLabel(new Model("Contraseña"));
        this.add(passwordCmp);

        FormComponent<String> newPasswordCmp = new PasswordTextField("newPassword").setRequired(true);
        this.add(new ComponentCustomFeedbackPanel("newPasswordFeedback", new ComponentFeedbackMessageFilter(newPasswordCmp)));
        newPasswordCmp.add(StringValidator.maximumLength(15));
        newPasswordCmp.setLabel(new Model("Nueva Contraseña"));
        this.add(newPasswordCmp);

        this.add(new Button("confirmar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(){
                changePassword();
            }
        });

        this.login = ((BackofficeAuthenticationSession)AuthenticatedWebSession.get()).getUsername();
    }

    private void changePassword() {
        try {
            boolean autorizado = this.obtenerUsuarioService().authenticate(this.login, this.password);

            if (!autorizado) {
                this.showError("Contraseña anterior inv\u00E1lida.");
            } else if (this.password.equals(this.newPassword)) {
                    getSession().error("Debe ingresar una contraseña distinta a la anterior.");
                    return;

                } else {
                    boolean success = this.obtenerUsuarioService().cambiarContrasena(this.login, this.password, this.newPassword);

                    if (success) {
                        this.showSuccess("Operacion.exitosa");
                        AuthenticatedWebSession.get().invalidateNow();
                        setResponsePage(getApplication().getHomePage());

                    }
                }

        } catch (BackofficeException e){
            this.showError(e);
        }
    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        //Never used
    }

}
