package uy.gub.agesic.pdi.pys.backoffice.utiles.ui;

import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Date;

@SuppressWarnings({"squid:S1068", "squid:S1450"})
public abstract class ListDateFilterForm extends ListBackofficeForm {

    protected Date fechaDesde;

    protected Date fechaHasta;

    public ListDateFilterForm(String id) {
        super(id);
    }

    protected void addDateTimeFilters() {
        DateTimeField dateTimeDesdeField = new DateTimeField("dateDesde", new PropertyModel<>(this, "fechaDesde")) {
            @Override
            protected boolean use12HourFormat() {
                //this will force to use 24 hours format
                return false;
            }
        };

        add(dateTimeDesdeField);

        //Date and time fields
        DateTimeField dateTimeHastaField = new DateTimeField("dateHasta", new PropertyModel<>(this, "fechaHasta")) {
            @Override
            protected boolean use12HourFormat() {
                //this will force to use 24 hours format
                return false;
            }
        };
        add(dateTimeHastaField);

    }

    @Override
    protected PageParameters filtrosAParametrosPagina() {
        PageParameters parameters = super.filtrosAParametrosPagina();

        if (this.fechaDesde != null) {
            parameters.add(FILTRO_FECHA_DESDE, dateFormat.format(this.fechaDesde));
        }

        if (this.fechaHasta != null ) {
            parameters.add(FILTRO_FECHA_HASTA, dateFormat.format(this.fechaHasta));
        }

        return (parameters);
    }

    @Override
    protected Boolean cargarFiltrosPorParametrosPagina(PageParameters parameters) {
        Boolean filtroCargado = super.cargarFiltrosPorParametrosPagina(parameters);

        try {

            if (parameters.getNamedKeys().contains(FILTRO_FECHA_DESDE)) {
                String desde = parameters.get(FILTRO_FECHA_DESDE).toString();
                this.fechaDesde = dateFormat.parse(desde);
                filtroCargado = true;
            }

            if (parameters.getNamedKeys().contains(FILTRO_FECHA_HASTA)) {
                String hasta = parameters.get(FILTRO_FECHA_HASTA).toString();
                this.fechaHasta = dateFormat.parse(hasta);
                filtroCargado = true;
            }

        } catch (Exception ex) {
            filtroCargado = false;
        }

        return (filtroCargado);
    }

}
