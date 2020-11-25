package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.ColeccionesForm;

public class Colecciones extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private ColeccionesForm form;

    public Colecciones() {
        this.initPage();
    }

    public Colecciones(final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new ColeccionesForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Colecciones");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
