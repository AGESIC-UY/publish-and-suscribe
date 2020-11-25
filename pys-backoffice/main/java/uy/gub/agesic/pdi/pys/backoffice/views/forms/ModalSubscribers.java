package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.SuscriptorService;
import uy.gub.agesic.pdi.pys.backoffice.utiles.spring.ApplicationContextProvider;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;

import java.util.ArrayList;
import java.util.List;

public class ModalSubscribers extends Panel {

    protected static final Logger logger = LoggerFactory.getLogger(ModalSubscribers.class);

    private final FilterRuleEntityForm form;
    private final ModalWindow window;
    private final boolean isLeft;

    private ArrayList<Suscriptor> selectedSubscribers;

    public ModalSubscribers(String id, final FilterRuleEntityForm form, final ModalWindow window, boolean isLeft) {
        super(id);
        this.form = form;
        this.window = window;
        this.isLeft = isLeft;
        this.selectedSubscribers = new ArrayList<Suscriptor>();
        this.initForm();
    }

    private void initForm() {
        ListMultipleChoice<Suscriptor> listSubscribers = new ListMultipleChoice<Suscriptor>(
                "subscribers",
                new Model(selectedSubscribers),
                this.getSubcribers()
        );
        listSubscribers.setOutputMarkupId(true);
        listSubscribers.setOutputMarkupPlaceholderTag(true);
        listSubscribers.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                logger.info("onUpdate list subscribers");
            }
        });
        this.add(listSubscribers);

        AjaxLink ajaxLink = new AjaxLink("saveList") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (form != null) {
                    List<String> list = new ArrayList<>();
                    for (Suscriptor subcriber : selectedSubscribers) {
                        list.add(subcriber.getNombre());
                    }
                    if (isLeft) {
                        form.updateLeftValue(String.join(";", list));
                    } else {
                        form.updateRightValue(String.join(";", list));
                    }
                }
                window.close(target);
            }
        };
        ajaxLink.setOutputMarkupId(true);
        ajaxLink.setOutputMarkupPlaceholderTag(true);
        this.add(ajaxLink);
    }

    private List<Suscriptor> getSubcribers() {
        List<Suscriptor> listaSuscriptores = new ArrayList<>();
        try {
            listaSuscriptores = this.getSubscriberService().getAll();
        } catch (PSException e) {
            this.logger.error(e.getMessage());
        }
        return listaSuscriptores;
    }

    protected SuscriptorService getSubscriberService() {
        return ApplicationContextProvider.getBean("suscriptorServiceImpl", SuscriptorService.class);
    }
}
