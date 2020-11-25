package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.FilterEntityForm;

public class PageFilterForm extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private FilterEntityForm form;

    public PageFilterForm() {
        this.initPage();
    }

    public PageFilterForm(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new FilterEntityForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Filtros");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
