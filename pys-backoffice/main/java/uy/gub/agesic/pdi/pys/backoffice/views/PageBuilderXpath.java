package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.BuilderXpathForm;

public class PageBuilderXpath extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private BuilderXpathForm form;

    public PageBuilderXpath() {
        this.initPage();
    }

    public PageBuilderXpath(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new BuilderXpathForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Constructor XPath");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
