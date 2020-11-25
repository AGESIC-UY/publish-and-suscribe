package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import uy.gub.agesic.pdi.pys.backoffice.config.BackofficeProperties;
import uy.gub.agesic.pdi.pys.backoffice.utiles.spring.ApplicationContextProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class CustomPageNavigator extends Panel {

	private static final long serialVersionUID = 1L;

    private List<Integer> predefinedSizes;

	private Integer pageSize;

	private Integer currentPage;

	private Long totalRows;

	private Component anchor;

	private ArrayList<Object> pageIndexes = new ArrayList<>();
	
	public CustomPageNavigator(String id) {
		super(id);
		this.inicializar();
	}
	
	private void inicializar() {
		// Tamanios predefinidos, los traemos del negocio porque pueden cambiar
		this.updatePredefinedSizes();
		
		// Esta lista contiene los indices generados segun los diferentes parametros del navegador
		this.pageIndexes = new ArrayList<>();

		DropDownChoice<Integer> cantidadesDrop = new DropDownChoice<Integer>("pageSizes", new PropertyModel<Integer>(this, "pageSize"), predefinedSizes) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

			@Override
			protected void onSelectionChanged(Integer newSelection) {
				pageSize = newSelection;
				gotoPage(null);
			}

		};
		cantidadesDrop.setEscapeModelStrings(false);
		cantidadesDrop.setNullValid(false);
		cantidadesDrop.setVersioned(false);

		this.add(cantidadesDrop);
		
		this.add(new PagesListView(this));

	}

    public Component getAnchor() {
        return anchor;
    }

    public List<Object> getPageIndexes() {
        return pageIndexes;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Setters y getters

	public Integer getTotalPages() {
		Long pages = totalRows / pageSize; 
		Long remainder = totalRows % pageSize; 
		if (remainder > 0) {
			pages = pages + 1;
		}
		
		return pages.intValue();
	}
	
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Long getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Long totalRows) {
		this.totalRows = totalRows;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Metodos utilitarios

	public void setState(Integer currentPage, Long totalRows) {
		if (currentPage == null) {
			this.currentPage = 0;
			this.totalRows = totalRows;
		} else {
			this.currentPage = currentPage;
		}
		
		this.updatePageIndexes();
	}

	public void setState(Integer currentPage, Integer pageSize, Long totalRows) {
		if (currentPage == null) {
			this.currentPage = 0;
			this.totalRows = totalRows;
		} else {
			this.currentPage = currentPage;
		}
		this.pageSize = pageSize;
		
		this.updatePageIndexes();
	}
	
	public void updatePageIndexes() {
		this.pageIndexes.clear();
		
		if (this.totalRows != null && this.pageSize != null && this.totalRows != 0 && this.pageSize != 0) {
			this.pageIndexes.add("PREVIOUS");

            int maxPage = getTotalPages();

			int init = currentPage > 7 ? currentPage - 7 : 0;
			int max = init + 12 ;
            if (max >  maxPage) {
                max = maxPage;
            }
			for (int i = init; i < max; i++) {
				this.pageIndexes.add(i);
			}

			this.pageIndexes.add("NEXT");
		}
	}
	
	private void updatePredefinedSizes() {
		List<Integer> sizesList = new LinkedList<>();

		try {
			BackofficeProperties properties = ApplicationContextProvider.getBean("backofficeProperties", BackofficeProperties.class);
			String[] sizesArray = properties.getFilasPorPagina().split(",");

			for(String size : sizesArray){
				sizesList.add(Integer.valueOf(size));
			}

			if (sizesList.isEmpty()) {
				sizesList.add(5);
				sizesList.add(10);
				sizesList.add(15);
			}
		} catch (Exception ex) {
			sizesList = Arrays.asList(5,10,50);
		}
		
		predefinedSizes = sizesList;
		
		this.pageSize = predefinedSizes.get(0);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Handlers de los eventos
	
	public abstract void gotoPage(Integer pagina);

}
