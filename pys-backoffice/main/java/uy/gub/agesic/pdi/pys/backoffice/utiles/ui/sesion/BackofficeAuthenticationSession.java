package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.sesion;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import uy.gub.agesic.pdi.pys.backoffice.services.UserService;

public class BackofficeAuthenticationSession extends AuthenticatedWebSession {

    private UserService usuarioService;
    private String username;

    public BackofficeAuthenticationSession(Request request, UserService usuarioService) {
        super(request);
        this.usuarioService = usuarioService;
        this.username = null;
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            this.username = username;
            return this.usuarioService.authenticate(username, password);
        } catch (Exception ex) {
            // Ha ocurrido un error autenticando al usuario
            this.username = null;
            return false;
        }
    }

    @Override
    public Roles getRoles() {
        return null;
    }

    public String getUsername() {
        return username;
    }
}