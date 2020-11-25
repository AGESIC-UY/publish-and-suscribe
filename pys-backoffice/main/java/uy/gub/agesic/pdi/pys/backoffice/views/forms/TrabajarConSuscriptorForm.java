package uy.gub.agesic.pdi.pys.backoffice.views.forms;

import org.apache.wicket.AttributeModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backoffice.repository.SuscriptorRepository;
import uy.gub.agesic.pdi.pys.backoffice.utiles.enumerados.ModoOperacion;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.GuardarButton;
import uy.gub.agesic.pdi.pys.backoffice.utiles.ui.components.LinkVolver;
import uy.gub.agesic.pdi.pys.backoffice.views.Suscriptores;
import uy.gub.agesic.pdi.pys.common.util.Constants;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@java.lang.SuppressWarnings({"squid:S1068", "squid:S1450"})
public class TrabajarConSuscriptorForm extends TrabajarConBaseEntityForm {

    private static final Logger logger = LoggerFactory.getLogger(TrabajarConSuscriptorForm.class);

    // Atributos del Suscriptor
    private String dn;
    private String fechaCreacion;
    private Boolean habilitado;

    public TrabajarConSuscriptorForm() {
        super("trabajarConSuscriptorForm");
    }

    @Override
    public void initForm() {
        super.initForm();

        // Guardamos cambios
        this.add(new TrabajarConSuscriptorForm.TrbGuardarButton(this.getFinalMessage(properties.getMensajeConfirmacion())));

        // Se agrega el link de volver
        this.add(new LinkVolver(Suscriptores.class));
    }

    @java.lang.SuppressWarnings("squid:S3776")
    protected void guardarSuscriptor () {
        try {
            SuscriptorRepository suscriptorService = this.obtenerSuscriptorService();

            if (ModoOperacion.ALTA.equals(this.modo) || ModoOperacion.MODIFICACION.equals(this.modo)) {

                if (!Pattern.matches(Constants.PATRON_NOMBRE, this.nombre)) {
                    showError("Datos inv\u00E1lidos, verifique el campo Nombre");
                    return;
                }
                Suscriptor suscriptor = getSuscriptor(suscriptorService);
                updateSuscriptor(suscriptor);

                if (ModoOperacion.ALTA.equals(this.modo)) {

                    if(suscriptorService.buscarSuscriptor(this.nombre) != null) {
                        this.showError("Ya existe el suscriptor: " + this.nombre);
                        return;
                    }

                    suscriptorService.crearSuscriptor(suscriptor);
                    getSession().success("Suscriptor creado exitosamente");
                    setResponsePage(Suscriptores.class);

                } else {
                    suscriptorService.modificarSuscriptor(suscriptor);
                    getSession().success("Suscriptor modificado exitosamente");
                    setResponsePage(Suscriptores.class);
                }
                // Cambio al modo a edicion
                this.modo = ModoOperacion.MODIFICACION;
                this.nombre = suscriptor.getNombre();
                this.definirValoresIniciales();

            }

        } catch (PSException ex) {
            this.showError(ex);
        }
    }

    private Suscriptor getSuscriptor(SuscriptorRepository suscriptorService) throws PSException {
        return ModoOperacion.ALTA.equals(this.modo) ? new Suscriptor() : suscriptorService.buscarSuscriptor(this.nombre);
    }

    private void updateSuscriptor(Suscriptor suscriptor) {
        suscriptor.setNombre(this.nombre);
        suscriptor.setDn(this.dn);
        suscriptor.setHabilitado(true);

        try {
            if (this.fechaCreacion != null) {
                suscriptor.setFechaCreacion(new SimpleDateFormat(Constants.PATRON_FECHA_HORA).parse(this.fechaCreacion));
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }

        suscriptor.setHabilitado(this.estado.equals(HABILITADO_VALUE));
    }

    private class TrbGuardarButton extends GuardarButton {

        public TrbGuardarButton(String mensajeConfirmacion) {
            super(mensajeConfirmacion, TrabajarConSuscriptorForm.this);
        }

        @Override
        public void ejecutar() {
            guardarSuscriptor();
        }
    }

    protected void definirValoresIniciales() {
        try {

            Suscriptor suscriptor = obtenerSuscriptorService().buscarSuscriptor(this.nombre) ;
            this.nombre = suscriptor.getNombre();
            this.dn = suscriptor.getDn();

            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PATRON_FECHA_HORA);
            this.fechaCreacion = suscriptor.getFechaCreacion() != null ? dateFormat.format(suscriptor.getFechaCreacion()) : null;

            this.habilitado = suscriptor.getHabilitado();
            this.get(NOMBRE_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            this.get(FECHA_CREACION).setVisible(false);
            this.get(LABEL_FECHA_CREACION).setVisible(false);

            if (this.habilitado) {
                this.get(HABILITADO_NAME).add(AttributeModifier.replace(READONLY_ATTRIBUTE_NAME, READONLY_ATTRIBUTE_NAME));
            }

            this.estado = suscriptor.getHabilitado() ? HABILITADO_VALUE : DESHABILITADO_VALUE;

        } catch(PSException ex) {
            this.showError(ex);
        }

    }

}
