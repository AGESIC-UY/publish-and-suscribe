package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroNovedadConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Novedad;

import java.text.ParseException;

public interface NovedadService {

    Novedad registrar(Novedad item) throws PSException;

    ResultadoPaginadoDTO<Novedad> buscarNovedadesFiltro(FiltroNovedadConsultaDTO filtro) throws PSException, ParseException;

    Novedad buscarNovedad(String uuidNovedad) throws PSException;

}
