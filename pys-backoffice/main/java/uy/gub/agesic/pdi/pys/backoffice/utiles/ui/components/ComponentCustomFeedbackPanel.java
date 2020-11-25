package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComponentCustomFeedbackPanel extends BaseCustomFeedbackPanel {

	private static final long serialVersionUID = 1L;

	public ComponentCustomFeedbackPanel(String id) {
        super(id);
    }

    public ComponentCustomFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        return getComponentCSSClass(message);
    }

}