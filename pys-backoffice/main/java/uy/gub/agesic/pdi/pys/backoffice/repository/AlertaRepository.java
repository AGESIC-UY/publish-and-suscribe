package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroAlertaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Alerta;

public interface AlertaRepository {

    ResultadoPaginadoDTO<Alerta> buscarAlertaFiltro(FiltroAlertaConsultaDTO filtro) throws PSException;

    Alerta buscarAlerta(String id) throws PSException;

}
