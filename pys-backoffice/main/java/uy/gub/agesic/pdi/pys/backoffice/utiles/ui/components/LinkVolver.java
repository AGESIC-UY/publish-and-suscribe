package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LinkVolver<C extends IRequestablePage> extends StatelessLink {

    protected static final String LINK_VOLVER_NAME = "linkVolver";

    private Class<C> type;

    private PageParameters parametersCallback;

    public LinkVolver(Class<C> type) {
        super(LINK_VOLVER_NAME);
        this.type = type;
    }

    @Override
    public void onClick() {
        setResponsePage(type, parametersCallback);
    }

    public PageParameters getParametersCallback() {
        return parametersCallback;
    }

    public void setParametersCallback(PageParameters parametersCallback) {
        this.parametersCallback = parametersCallback;
    }
}

