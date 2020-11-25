package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.TrabajarConTopicoForm;

public class TrabajarConTopico extends PaginaBase {

    private TrabajarConTopicoForm form;

    public TrabajarConTopico() {
        this.initPage();
    }

    public TrabajarConTopico(final PageParameters parameters) {
        super(parameters);
        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new TrabajarConTopicoForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Trabajar con T\u00F3pico");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
