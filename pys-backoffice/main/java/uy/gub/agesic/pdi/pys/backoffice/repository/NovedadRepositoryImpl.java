package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroNovedadConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.NovedadService;
import uy.gub.agesic.pdi.pys.domain.Novedad;

@Service
public class NovedadRepositoryImpl implements NovedadRepository {

    private static Logger logger = LoggerFactory.getLogger(NovedadRepositoryImpl.class);

    private NovedadService novedadService;

    @Autowired
    public NovedadRepositoryImpl(NovedadService novedadService) {
        this.novedadService = novedadService;
    }

    @Override
    public ResultadoPaginadoDTO<Novedad> buscarNovedadesFiltro(FiltroNovedadConsultaDTO filtro) throws PSException {
        try {
            return novedadService.buscarNovedadesFiltro(filtro);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible recuperar la lista de novedades", ex.toString());
        }
    }

    @Override
    public Novedad buscarNovedad(String uuidNovedad) throws PSException {
        try {
            return novedadService.buscarNovedad(uuidNovedad);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new PSException("No ha sido posible recuperar la novedad", ex.toString());
        }
    }
}
