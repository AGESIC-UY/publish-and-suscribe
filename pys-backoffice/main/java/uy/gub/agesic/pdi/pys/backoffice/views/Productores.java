package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.ProductoresForm;

public class Productores extends PaginaBase {

    private static final long serialVersionUID = 1L;

    private ProductoresForm form;

    public Productores() {
        this.initPage();
    }

    public Productores(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new ProductoresForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Productores");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
