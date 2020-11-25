package uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components;

import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.BackofficeForm;

public abstract class GuardarButton extends BotonAccion {

    private BackofficeForm form;

    public GuardarButton(String mensajeConfirmacion, BackofficeForm form) {
        super("btnGuardar", Error.class, false, mensajeConfirmacion);
        this.form = form;
    }

    @Override
    public boolean poseePermisoEjecucion() {
        // El control en este caso ya fue realizado al ingresar a la pagina, segun el modo apropiado
        return true;
    }

    @Override
    public boolean isVisible() {
        return !this.form.getModo().equals(ModoOperacion.CONSULTA);
    }

    @Override
    public abstract void ejecutar();

}
