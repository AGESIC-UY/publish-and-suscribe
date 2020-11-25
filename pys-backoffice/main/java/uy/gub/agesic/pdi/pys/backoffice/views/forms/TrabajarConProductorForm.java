package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.repository.ProductorRepository;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GuardarButton;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.LinkVolver;
import uy.gub.agesic.pdi.pys.backoffice.views.Productores;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Productor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class TrabajarConProductorForm extends TrabajarConBaseEntityForm {

    // Atributos del Productor
    private String dn;
    private String fechaCreacion;
    private Boolean habilitado;

    public TrabajarConProductorForm() {
        super("trabajarConProductorForm");
    }

    @Override
    public void initForm() {
        super.initForm();

        // Guardamos cambios
        this.add(new TrabajarConProductorForm.TrbGuardarButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        // Se agrega el link de volver
        this.add(new LinkVolver(Productores.class));
    }

    protected void guardarProductor () {
        try {
            ProductorRepository productorService = this.obtenerProductorService();

            if (ModoOperacion.ALTA.equals(this.modo) || ModoOperacion.MODIFICACION.equals(this.modo)) {

                if (!Pattern.matches(Constants.PATRON_NOMBRE, this.nombre)) {
                    showError("Datos inv\u00E1lidos, verifique el campo Nombre");
                    return;
                }
                Productor productor = getProductor(productorService);
                updateProductor(productor);

                if (ModoOperacion.ALTA.equals(this.modo)) {

                    if(productorService.buscarProductor(this.nombre) != null) {
                        this.showError("Ya existe el productor: " + this.nombre);
                        return;
                    }

                    productorService.crearProductor(productor);
                    getSession().success("Productor creado exitosamente");
                    setResponsePage(Productores.class);

                } else {
                    productorService.modificarProductor(productor);
                    getSession().success("Productor modificado exitosamente");
                    setResponsePage(Productores.class);
                }
                // Cambio al modo a edicion
                this.modo = ModoOperacion.MODIFICACION;
                this.nombre = productor.getNombre();
                this.definirValoresIniciales();
            }

        } catch (PSException ex) {
            this.showError(ex);
        }
    }

    private void updateProductor(Productor productor) {
        productor.setNombre(this.nombre);
        productor.setDn(this.dn);
        productor.setHabilitado(true);

        try {
            if (this.fechaCreacion != null) {
                productor.setFechaCreacion(new SimpleDateFormat(Constants.PATRON_FECHA_HORA).parse(this.fechaCreacion));
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        productor.setHabilitado(this.estado.equals(HABILITADO_VALUE));
    }

    private Productor getProductor(ProductorRepository productorService) throws PSException {
        return ModoOperacion.ALTA.equals(this.modo) ? new Productor() : productorService.buscarProductor(this.nombre);
    }

    private class TrbGuardarButton extends GuardarButton {

        public TrbGuardarButton(String mensajeConfirmacion) {
            super(mensajeConfirmacion, TrabajarConProductorForm.this);
        }

        @Override
        public void ejecutar() {
            guardarProductor();
        }
    }

    protected void definirValoresIniciales() {
        try {

            Productor productor = obtenerProductorService().buscarProductor(this.nombre) ;
            this.nombre = productor.getNombre();
            this.dn = productor.getDn();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            this.fechaCreacion = productor.getFechaCreacion() == null ? null : dateFormat.format(productor.getFechaCreacion());

            this.habilitado = productor.getHabilitado();
            this.get(NOMBRE_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FECHA_CREACION).setVisible(false);
            this.get(LABEL_FECHA_CREACION).setVisible(false);

            if (this.habilitado) {
                this.get(HABILITADO_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            }

            this.estado = productor.getHabilitado() ? HABILITADO_VALUE : DESHABILITADO_VALUE;

        } catch (PSException ex) {
            this.showError(ex);
        }

    }

}
