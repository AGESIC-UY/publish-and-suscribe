package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.AlertasForm;

public class Alertas extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private AlertasForm form;

    public Alertas() {
        this.initPage();
    }

    public Alertas(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new AlertasForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Alertas");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
