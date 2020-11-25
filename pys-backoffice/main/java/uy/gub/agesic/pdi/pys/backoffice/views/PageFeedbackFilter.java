package uy.gub.agesic.pdi.pys.backoffice.views;

import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.AllExceptFeedbackFilter;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GlobalCustomFeedbackPanel;

import java.util.ArrayList;
import java.util.List;

class PageFeedbackFilter extends AllExceptFeedbackFilter {
    private static final long serialVersionUID = 1L;
    private PaginaBase components;
    private final GlobalCustomFeedbackPanel pageFeedback;

    public PageFeedbackFilter(PaginaBase components, GlobalCustomFeedbackPanel pageFeedback) {
        this.components = components;
        this.pageFeedback = pageFeedback;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected IFeedbackMessageFilter[] getFilters() {
        final List filters = new ArrayList();
        components.getPage().visitChildren(FeedbackPanel.class, new FeedbackIVisitor(filters));
        return (IFeedbackMessageFilter[]) filters.toArray(new IFeedbackMessageFilter[filters.size()]);
    }

    private class FeedbackIVisitor implements IVisitor {
        private final List filters;

        public FeedbackIVisitor(List filters) {
            this.filters = filters;
        }

        public void component(Object object, IVisit visit) {
            if (pageFeedback.equals(object)) {
                visit.dontGoDeeper();
            } else {
                filters.add(((FeedbackPanel) object).getFilter());
            }
        }
    }
}
