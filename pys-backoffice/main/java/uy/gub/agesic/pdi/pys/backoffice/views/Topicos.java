package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.TopicosForm;

public class Topicos extends PaginaBase {

    private static final long serialVersionUID = 1L;

    private TopicosForm form;

    public Topicos() {
        this.initPage();
    }

    public Topicos (final PageParameters parameters) {
        super(parameters);

        this.initPage();
        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new TopicosForm();
        this.add(this.form);
        this.form.initForm();

        this.setPageTitle("AGESIC - Backoffice de P&S - Tópicos");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }

}
