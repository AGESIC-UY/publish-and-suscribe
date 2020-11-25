package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroSuscriptorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.SuscriptorService;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;

import java.util.List;

@Service
public class SuscriptorRepositoryImpl implements SuscriptorRepository {

    private static Logger logger = LoggerFactory.getLogger(SuscriptorRepositoryImpl.class);

    private static final String NO_SUS_FOUND_MSG = "No ha sido posible recuperar los suscriptores";

    private SuscriptorService suscriptorService;

    @Autowired
    public SuscriptorRepositoryImpl(SuscriptorService suscriptorService){ this.suscriptorService = suscriptorService; }


    @Override
    public ResultadoPaginadoDTO<Suscriptor> buscarSuscriptores(FiltroSuscriptorDTO filtro) throws PSException {
        try {
            return suscriptorService.buscarSuscriptoresFiltro(filtro);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException(NO_SUS_FOUND_MSG, ex.toString());
        }
    }

    @Override
    public Suscriptor buscarSuscriptor(String nombre) throws PSException {
        try{
            return suscriptorService.buscar(nombre);
        } catch (Exception ex){
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible recuperar los datos del suscriptor: " + nombre, ex.toString());

        }
    }

    @Override
    public void crearSuscriptor(Suscriptor suscriptor) throws PSException {
        try {
            suscriptorService.crear(suscriptor);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible crear al suscriptor: " + suscriptor.getNombre(), ex.toString());

        }
    }

    @Override
    public void eliminarSuscriptores(List<String> nombres) throws PSException {
        for (String nombre : nombres) {
            try {
                //La eliminación es lógica, pasa a estado deshabilitado
                suscriptorService.deshabilitarSuscriptor(nombre);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new PSException("No ha sido posible eliminar al suscriptor: " + nombre, ex.toString());

            }
        }
    }

    @Override
    public void modificarSuscriptor(Suscriptor suscriptor) throws PSException {
        try {
            suscriptorService.modificar(suscriptor);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible modificar al suscriptor: " + suscriptor.getNombre(), ex.toString());

        }
    }

    @Override
    public List<Suscriptor> getAll() throws PSException {
        try {
            return suscriptorService.getAll();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException(NO_SUS_FOUND_MSG, ex.toString());
        }
    }

    @Override
    public List<Suscriptor> obtenerHabilitados() throws PSException {
        try {
            return suscriptorService.obtenerHabilitados();
        } catch (Exception ex) {
            throw new PSException(NO_SUS_FOUND_MSG, ex.toString());
        }
    }
}
