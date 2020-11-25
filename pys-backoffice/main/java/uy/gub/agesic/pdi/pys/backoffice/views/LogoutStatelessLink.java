package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.markup.html.link.StatelessLink;
import uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions.BackofficeException;

class LogoutStatelessLink extends StatelessLink {
    private static final long serialVersionUID = 1L;

    private PaginaBase components;

    public LogoutStatelessLink(PaginaBase components) {
        super(PaginaBase.LOGOUTLINK);
        this.components = components;
    }

    @Override
    public void onClick() {
        try {
            components.logout();
        } catch (BackofficeException e) {
            components.getCurrentForm().showError(e);
        }
    }
}
