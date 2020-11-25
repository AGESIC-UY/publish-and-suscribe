package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
class PagesListView extends ListView<Object> {

    private CustomPageNavigator customPageNavigator;

    private static final String PAGE_NUMBER = "pageNumber";

    private static final String PAGE_LINK = "pageLink";

    private static final String CLASS = "class";

    private static final long serialVersionUID = 1L;

    private static final int OFFSET = 3;

    private static final int CANT_PAG_MOSTRAR = OFFSET * 2 + 1;

    private static final int OFFSET_CANT_PAG_PTOS_SUSPENSIVOS = 5;

    public PagesListView(CustomPageNavigator customPageNavigator) {
        super("pager", customPageNavigator.getPageIndexes());
        this.customPageNavigator = customPageNavigator;
    }

    @Override
    protected void populateItem(ListItem<Object> item) {
        Object itemObject = item.getModelObject();

        if (itemObject instanceof String) {
            // Es el indicado de anterior o siguiente
            final String linkType = (String)itemObject;

            if ("PREVIOUS".equals(linkType)) {
                processPrevious(item);
            } else if ("NEXT".equals(linkType)) {
                processNext(item);
            }

        } else if (itemObject instanceof Integer) {
            // Es un indice de pagina
            processItemObject((Integer) itemObject, item);
        }
    }

    private void processPrevious(ListItem<Object> item) {
        Link<Void> pageLink = new Link<Void>(PAGE_LINK) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                if (customPageNavigator.getCurrentPage() > 0) {
                    customPageNavigator.gotoPage(0); //va a la primer página
                }
            }
        };
        item.add(pageLink);
        pageLink.setAnchor(this.customPageNavigator.getAnchor());

        Label pageNumber = new Label(PAGE_NUMBER, "&laquo;");
        pageNumber.setEscapeModelStrings(false);
        pageLink.add(pageNumber);

        if (customPageNavigator.getCurrentPage() == 0) {
            item.add(AttributeModifier.append(CLASS, Model.of("disabled")));
        }

    }

    private void processNext(ListItem<Object> item) {
        final Integer totalPages = customPageNavigator.getTotalPages();

        Link<Void> pageLink = new Link<Void>(PAGE_LINK) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                if (customPageNavigator.getCurrentPage() < totalPages - 1) {
                    customPageNavigator.gotoPage(totalPages -1); //va a la última página
                }
            }
        };
        item.add(pageLink);
        pageLink.setAnchor(this.customPageNavigator.getAnchor());

        Label pageNumber = new Label(PAGE_NUMBER, "&raquo;");
        pageNumber.setEscapeModelStrings(false);
        pageLink.add(pageNumber);

        if (customPageNavigator.getCurrentPage() == totalPages - 1) {
            item.add(AttributeModifier.append(CLASS, Model.of("disabled")));
        }

    }

    private void processItemObject(Integer itemObject, ListItem<Object> item) {
        final Integer pageIdx = itemObject;

        Link<Void> pageLink = new Link<Void>(PAGE_LINK) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                customPageNavigator.gotoPage(pageIdx);
            }
        };
        item.add(pageLink);
        pageLink.setAnchor(this.customPageNavigator.getAnchor());
        int totalPages = customPageNavigator.getTotalPages() - 2;

        if (totalPages <= CANT_PAG_MOSTRAR) {
            Label pageNumber = new Label(PAGE_NUMBER, pageIdx + 1);
            pageNumber.setEscapeModelStrings(false);
            pageLink.add(pageNumber);
        } else {
            int ini = customPageNavigator.getCurrentPage() - OFFSET;
            int fin = customPageNavigator.getCurrentPage() + OFFSET;

            if (ini <= 1) {
                ini = 0;
                fin = ini + CANT_PAG_MOSTRAR - 1;
            } else if (fin >= totalPages) {
                fin = totalPages + 1;
                ini = fin - CANT_PAG_MOSTRAR + 1;
            }

            int posPtosIni = ini - OFFSET_CANT_PAG_PTOS_SUSPENSIVOS <= 1 ? ini - Math.max((ini - 1) / 2, 1) : fin - CANT_PAG_MOSTRAR;
            int posPtosFin = fin + OFFSET_CANT_PAG_PTOS_SUSPENSIVOS >= totalPages ? fin + Math.max((totalPages - fin) / 2, 1) : ini + CANT_PAG_MOSTRAR;

            Label pageNumber = new Label(PAGE_NUMBER, (pageIdx == posPtosIni || pageIdx == posPtosFin)? "..." : pageIdx + 1);
            pageNumber.setEscapeModelStrings(false);
            pageLink.add(pageNumber);

            item.setVisible(pageIdx == posPtosIni || pageIdx == posPtosFin || (ini <= pageIdx && pageIdx <= fin));
        }

        if (customPageNavigator.getCurrentPage().equals(pageIdx)) {
            item.add(AttributeModifier.append(CLASS, Model.of("active")));
        }

    }
}
