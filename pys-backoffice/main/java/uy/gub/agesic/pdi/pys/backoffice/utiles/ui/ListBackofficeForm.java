package uy.gub.agesic.pdi.pys.backoffice.utiles.ui;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.PermisoUsuario;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.BotonAccion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.CustomPageNavigator;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public abstract class ListBackofficeForm extends BackofficeForm {

    protected SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);

    // Componentes visuales
    private CustomPageNavigator navigator;

    //Cantidad de registros que devuelve la búsqueda
    private String cantRegistros;

    private Integer nroPagina = 0;

    protected Button eliminarButton;

    protected HashMap<String, String> mapSelection;

    public ListBackofficeForm(String id) {
        super(id);
    }

    @Override
    public void initForm() {
        super.initForm();

        this.mapSelection = new HashMap<>();

        this.add(new Button("buscar") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                ListBackofficeForm.this.nroPagina = 0;
                buscar(null);
            }
        });

        // Agregamos un navigator
        this.navigator = new CustomPageNavigator("pagingNavigator") {
            private static final long serialVersionUID = 1L;

            public void gotoPage(Integer pagina) {
                setNroPagina(pagina);
                buscar(pagina);
            }
        };

        this.navigator.setCurrentPage(0);
        this.navigator.setTotalRows(0L);
        this.add(navigator);

        Label cantRegistrosLbl = new Label("cantRegistros");
        cantRegistrosLbl.setEnabled(false);
        this.add(cantRegistrosLbl);
    }

    protected void agregarBotones(boolean isWritable) {
        agregarBotones(isWritable, null, null);
    }

    protected void cantRegistrosDescription(ResultadoPaginadoDTO resultado) {
        this.cantRegistros = "(" + ((resultado == null || resultado.getTotalTuplas() == null) ? "0" : resultado.getTotalTuplas().toString()) + ")";
    }

    public void limpiarFiltros() {
        this.cantRegistros = "";
        this.navigator.setState(0,0,0L);
        this.navigator.render();
    }

    protected void setCeroCantRegistros() {
        this.cantRegistros = "(0)";
    }

    /**
     * Este método devuelve un objeto <code>org.apache.ui.request.mapper.parameter.PageParameter</code> en base a los filtros cargados del formulario
     */
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = new PageParameters();

        if (this.nroPagina != null) {
            parameters.add(FILTRO_NRO_PAGINA, this.nroPagina);
        }

        if (this.navigator != null) {
            parameters.add(FILTRO_PAGE_SIZE, this.navigator.getPageSize());
        }

        return parameters;
    }

    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = false;

        try {
            if (parameters.getNamedKeys().contains(FILTRO_NRO_PAGINA)) {
                this.nroPagina = parameters.get(FILTRO_NRO_PAGINA).toInteger();
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_PAGE_SIZE)) {
                navigator.setPageSize(parameters.get(FILTRO_PAGE_SIZE).toInteger());
                filtroCargado = true;
            }

        } catch (Exception ex) {
            filtroCargado = false;
        }

        return filtroCargado;
    }

    @Override
    public void setParametersInner(PageParameters parameters) {
        Boolean aplicarFiltros = this.cargarFiltrosPorParametrosPagina(parameters);
        if (aplicarFiltros) {
            if (this.getNroPagina() > 0) {
                this.buscar(this.getNroPagina());
            } else {
                this.buscar(null);
            }
        }
    }

    protected Integer getPageSize() {
        return this.navigator.getPageSize();
    }

    protected Integer getNroPagina() {
        return nroPagina;
    }

    protected void setNroPagina(Integer nroPagina) {
        this.nroPagina = nroPagina;
    }

    protected void updateNavigator(Integer pagina, ResultadoPaginadoDTO resultado) {
        Long totalRows = resultado == null ? 0 : resultado.getTotalTuplas();
        navigator.setTotalRows(totalRows);
        navigator.setState(pagina, totalRows);
    }

    protected void buscar(Integer pagina) {
        if (pagina == null && nroPagina != null && nroPagina.intValue() != 0) {
            nroPagina = 0;
        }

        if (eliminarButton != null) {
            eliminarButton.setVisible(false);
        }

        if (mapSelection != null) {
            mapSelection.clear();
        }

    }

    protected void agregarBotones(boolean isWritable, List seleccion, String errorMsg) {
        Button limpiarFiltrosButton = new Button(LIMPIAR_FILTRO_COMPONENT_NAME) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                limpiarFiltros();
                ListBackofficeForm.this.clearInput();
                buscar(null);
            }
        };
        limpiarFiltrosButton.setDefaultFormProcessing(false);
        this.add(limpiarFiltrosButton);

        if (isWritable) {
            this.add(new Button("agregar") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onSubmit() {
                    agregar();
                }
            });

            this.add(new Button("modificar") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onSubmit() {
                    modificar();
                }
            });
        }

        if (seleccion != null) {
            eliminarButton = (new Button("eliminar") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onSubmit() {

                    if (seleccion == null || seleccion.isEmpty()) {
                        showError(errorMsg);
                        return;
                    }

                    eliminar();
                    limpiarFiltros();
                    buscar(null);
                }
            });

            eliminarButton.setOutputMarkupId(true);
            eliminarButton.setOutputMarkupPlaceholderTag(true);
            eliminarButton.add(new AttributeModifier("onclick", "return confirm('¿Está seguro que desea confirmar la operación?');"));
            this.add(eliminarButton);
        }

    }

    protected void agregar() {
        //implementado por la super clase
    }

    protected void modificar() {
        //implementado por la super clase
    }

    protected void eliminar() {
        //implementado por la super clase
    }

    protected void evaluateEliminarButtonVisibility(AjaxRequestTarget target, String key) {
        if (permiso != null && permiso.equals(PermisoUsuario.LECTURA.name())) {
            eliminarButton.setVisible(false);
        } else {
            HashMap<String, String> objects = this.mapSelection;
            if (objects.containsKey(key)) {
                objects.remove(key);
            } else {
                objects.put(key, key);
            }
            eliminarButton.setVisible(objects.size() > 0);
            target.add(eliminarButton);
        }
    }

    protected void buscarProductores(ArrayList<Productor> listaProductores) {
        try {
            listaProductores.clear();
            listaProductores.addAll(obtenerProductorService().getAll());
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void buscarProductoresTopico(ArrayList<Productor> listaProductores, String topicName) {
        try {
            listaProductores.clear();
            listaProductores.addAll(obtenerTopicoProductorService().buscarProductoresPorTopico(topicName));
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void buscarTopicos(ArrayList<Topico> listaTopicos) {
        try {
            listaTopicos.clear();
            listaTopicos.addAll(obtenerTopicoService().getAll());
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void buscarSuscriptores(ArrayList<Suscriptor> listaSuscriptores) {
        try {
            listaSuscriptores.clear();
            listaSuscriptores.addAll(obtenerSuscriptorService().getAll());
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void buscarSuscriptoresTopico(ArrayList<Suscriptor> listaSuscriptores, String topicName) {
        try {
            listaSuscriptores.clear();
            listaSuscriptores.addAll(obtenerTopicoSuscriptorService().buscarSuscriptoresPorTopico(topicName));
        } catch (PSException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected class EliminarButton extends BotonAccion {

        private static final long serialVersionUID = 1L;

        public EliminarButton(String mensajeConfirmacion) {
            super("eliminar", true, mensajeConfirmacion);
            this.setOutputMarkupId(true);
            this.setOutputMarkupPlaceholderTag(true);
        }

        @Override
        public boolean poseePermisoEjecucion() {
            return true;
        }

        @Override
        public void ejecutar() {
            eliminar();
            limpiarFiltros();
            buscar(null);
        }
    }

}
