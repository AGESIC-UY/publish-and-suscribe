package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.TrabajarConProductorForm;

public class TrabajarConProductor extends PaginaBase {

    private TrabajarConProductorForm form;

    public TrabajarConProductor() {
        this.initPage();
    }

    public TrabajarConProductor(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new TrabajarConProductorForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Trabajar con Productor");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }

}
