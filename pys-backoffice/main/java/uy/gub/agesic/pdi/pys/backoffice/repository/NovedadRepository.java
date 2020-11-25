package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroNovedadConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Novedad;

public interface NovedadRepository {

    ResultadoPaginadoDTO<Novedad> buscarNovedadesFiltro(FiltroNovedadConsultaDTO filtro) throws PSException;

    Novedad buscarNovedad(String uuidNovedad) throws PSException;

}
