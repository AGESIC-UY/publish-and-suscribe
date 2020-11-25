package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.NovedadesForm;

public class Novedades extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private NovedadesForm form;

    public Novedades () {
        this.initPage();
    }

    public Novedades (final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new NovedadesForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Novedades");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
