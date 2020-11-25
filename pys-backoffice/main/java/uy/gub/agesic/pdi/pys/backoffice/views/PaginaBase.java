package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.soporte.DateUtil;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficePage;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GlobalCustomFeedbackPanel;

public abstract class PaginaBase extends BackofficePage {

    public static final String LOGOUTLINK = "logoutLink";

    public PaginaBase() {
        this.initBasePage();
    }

    public PaginaBase(final PageParameters parameters) {
        super(parameters);

        this.initBasePage();
    }

    @SuppressWarnings("rawtypes")
    private void initBasePage() {

        this.setVersioned(false);
        this.setStatelessHint(true);

        // Etiquetas generales
        this.add(new Label("title", Model.of("AGESIC - Backoffice de P&S")).setEscapeModelStrings(false));
        this.add(new Label("labelFechaVersion", Model.of(String.format("&copy; %s AGESIC - v%s", DateUtil.currentYear().toString(),
                this.getProperties().getAppVersion()))).setEscapeModelStrings(false));

        // Items de menu para manipular luego
        this.add(new WebMarkupContainer("mnuItemUsuarios"));
        this.add(new WebMarkupContainer("mnuItemPassword"));
        this.add(new WebMarkupContainer("mnuItemProductores"));
        this.add(new WebMarkupContainer("mnuItemSuscriptores"));
        this.add(new WebMarkupContainer("mnuItemTopicos"));
        this.add(new WebMarkupContainer("mnuItemNovedades"));
        this.add(new WebMarkupContainer("mnuItemColecciones"));
        this.add(new WebMarkupContainer("mnuItemEntregas"));
        this.add(new WebMarkupContainer("mnuItemAlertas"));
        this.add(new WebMarkupContainer("mnuItemFilters"));
        this.add(new WebMarkupContainer("mnuItemBuildXpath"));

        // Link de salida
        WebMarkupContainer logoutMenuItem = new WebMarkupContainer("logoutMenuItem");
        logoutMenuItem.add(new LogoutStatelessLink(this));
        this.add(logoutMenuItem);

        // Area general de feedback
        final GlobalCustomFeedbackPanel pageFeedback = new GlobalCustomFeedbackPanel("pageFeedback");
        pageFeedback.setFilter(new PageFeedbackFilter(this, pageFeedback));
        this.add(pageFeedback);

        // Menu de funcionalidades
        this.buildHeaderElements();
    }

    protected void setPageTitle(String title) {
        this.get("title").setDefaultModelObject(title);
    }

    private void buildHeaderElements() {
        // Agregamos un ID para el link de logout
        this.get("logoutMenuItem").get(LOGOUTLINK).setMarkupId(LOGOUTLINK);
    }
}






