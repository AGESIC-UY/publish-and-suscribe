package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;
import uy.gub.agesic.pdi.pys.backoffice.views.forms.FilterTopicForm;

public class PageFilterTopic extends PaginaBase {
    private static final long serialVersionUID = 1L;

    private FilterTopicForm form;

    public PageFilterTopic() {
        this.initPage();
    }

    public PageFilterTopic(final PageParameters parameters) {
        super(parameters);

        if (parameters.getNamedKeys().contains("nombreTopicoParameter")) {
            String nameTopic = parameters.get("nombreTopicoParameter").toString();
            this.initPageWithParam(nameTopic);
        } else {
            this.initPage();
        }

        this.form.setParameters(parameters);
    }

    private void initPage() {
        this.form = new FilterTopicForm();
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Filtro del tópico");
    }

    private void initPageWithParam(String nameTopic) {
        this.form = new FilterTopicForm(nameTopic);
        this.add(this.form);
        this.form.initForm();
        this.setPageTitle("AGESIC - Backoffice de P&S - Filtro del tópico");
    }

    @Override
    public BackofficeForm getCurrentForm() {
        return this.form;
    }
}
