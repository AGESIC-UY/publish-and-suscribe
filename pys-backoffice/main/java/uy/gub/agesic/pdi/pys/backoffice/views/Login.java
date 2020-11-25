package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.validator.StringValidator;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.soporte.DateUtil;
import uy.gub.agesic.pdi.pys.backoffice.utiles.spring.ApplicationContextProvider;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.AllExceptFeedbackFilter;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.ComponentCustomFeedbackPanel;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GlobalCustomFeedbackPanel;

import java.util.ArrayList;
import java.util.List;

public class Login extends WebPage {

    private BackofficeProperties properties;

    private String username;
    private String password;

    public Login() {
        this.properties = ApplicationContextProvider.getBean("backofficeProperties", BackofficeProperties.class);
        this.initPage();
    }

    private void initPage() {
        this.setVersioned(false);
        this.setStatelessHint(true);

        // Etiquetas generales
        this.add(new Label("title", Model.of("AGESIC - Backoffice de la P&S")).setEscapeModelStrings(false));
        this.add(new Label("labelFechaVersion", Model.of(String.format("&copy; %s AGESIC - v%s", DateUtil.currentYear().toString(), this.getProperties().getAppVersion()))).setEscapeModelStrings(false));

        // Area general de feedback
        final GlobalCustomFeedbackPanel pageFeedback = new GlobalCustomFeedbackPanel("pageFeedback");
        pageFeedback.setFilter(new AllExceptFeedbackFilter() {
            private static final long serialVersionUID = 1L;
            @SuppressWarnings("unchecked")
            @Override
            protected IFeedbackMessageFilter[] getFilters() {
                final List filters = new ArrayList();
                getPage().visitChildren(FeedbackPanel.class, new IVisitor() {
                    public void component(Object object, IVisit visit) {
                        if (pageFeedback.equals(object)) {
                            visit.dontGoDeeper();
                        } else {
                            filters.add(((FeedbackPanel) object).getFilter());
                        }
                    }
                });
                return (IFeedbackMessageFilter[]) filters.toArray(new IFeedbackMessageFilter[filters.size()]);
            }
        });
        this.add(pageFeedback);

        // Generamos el form asociado a este objeto
        StatelessForm form = new StatelessForm<CompoundPropertyModel<Login>>("loginForm");
        form.setDefaultModel(new CompoundPropertyModel<Login>(this));
        this.add(form);

        FormComponent<String> usernameField = new TextField<String>("username").setRequired(true);
        usernameField.add(StringValidator.maximumLength(50));
        form.add(new ComponentCustomFeedbackPanel("usernameFeedback", new ComponentFeedbackMessageFilter(usernameField)));
        usernameField.setLabel(new Model("Usuario"));
        form.add(usernameField);

        FormComponent<String> passwordField = new PasswordTextField("password").setRequired(true);
        passwordField.add(StringValidator.maximumLength(50));
        form.add(new ComponentCustomFeedbackPanel("passwordFeedback", new ComponentFeedbackMessageFilter(passwordField)));
        passwordField.setLabel(new Model("Contraseña"));
        form.add(passwordField);

        // Boton de login
        form.add(new LoginButton());
    }

    private class LoginButton extends BotonAccion {

        public LoginButton() {
            super("btnLogin");
        }

        @Override
        public boolean poseePermisoEjecucion() {
            return true;
        }

        @Override
        public boolean isVisible() {
            return true;
        }

        @Override
        public void ejecutar() {
            boolean success = AuthenticatedWebSession.get().signIn(Login.this.username, Login.this.password);
            if (success) {
                setResponsePage(Index.class);
            } else {
                Login.this.showError("Credenciales incorrectas");
            }
        }
    }

    public void showError(String message) {
        this.error(message);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilidades

    public BackofficeProperties getProperties() {
        return this.properties;
    }
}
