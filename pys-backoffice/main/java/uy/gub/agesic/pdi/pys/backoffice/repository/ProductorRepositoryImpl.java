package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroProductorDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.ProductorService;
import uy.gub.agesic.pdi.pys.domain.Productor;

import java.util.List;

@Service
public class ProductorRepositoryImpl implements ProductorRepository {

    private static final String NO_PROD_FOUND_MSG = "No ha sido posible recuperar los productores";

    private static Logger logger = LoggerFactory.getLogger(ProductorRepositoryImpl.class);

    private ProductorService productorService;

    @Autowired
    public ProductorRepositoryImpl(ProductorService productorService) {
        this.productorService = productorService;
    }

    @Override
    public ResultadoPaginadoDTO<Productor> buscarProductores(FiltroProductorDTO filtro) throws PSException {
        try {
            return productorService.buscarProductoresFiltro(filtro);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException(NO_PROD_FOUND_MSG, ex.toString());
        }
    }

    @Override
    public Productor buscarProductor(String nombre) throws PSException {
        try {
            return productorService.buscar(nombre);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible recuperar los datos del productor: " + nombre, ex.toString());
        }
    }

    @Override
    public void crearProductor(Productor productor) throws PSException {
        try {
            productorService.crear(productor);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible crear al productor: " + productor.getNombre(), ex.toString());

        }
    }

    @Override
    public void eliminarProductores(List<String> nombres) throws PSException {
        for (String nombre : nombres) {
            try {
                //La eliminación es lógica, pasa a estado deshabilitado
                productorService.deshabilitarProductor(nombre);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw new PSException("No ha sido posible eliminar al productor: " + nombre, ex.toString());

            }
        }
    }

    @Override
    public void modificarProductor(Productor productor) throws PSException {
        try {
            productorService.modificar(productor);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible modificar al productor: " + productor.getNombre(), ex.toString());

        }
    }

    @Override
    public List<Productor> getAll() throws PSException {
        try {
            return productorService.getAll();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException(NO_PROD_FOUND_MSG, ex.toString());
        }
    }

    @Override
    public List<Productor> obtenerHabilitados() throws PSException {
        try {
            return productorService.obtenerHabilitados();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException(NO_PROD_FOUND_MSG, ex.toString());
        }
    }
}
