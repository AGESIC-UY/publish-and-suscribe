package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.wicket.feedback.FeedbackMessage;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class GlobalCustomFeedbackPanel extends BaseCustomFeedbackPanel {

	public GlobalCustomFeedbackPanel(String id) {
        super(id);
        this.setOutputMarkupId(true);
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        return getGlobalCSSClass(message);
    }

}
