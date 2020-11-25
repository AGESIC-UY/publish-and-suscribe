package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.EntregasForm;

public class Entregas extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private EntregasForm form;

    public Entregas() {
        this.initPage();
    }

    public Entregas(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new EntregasForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Entregas");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
