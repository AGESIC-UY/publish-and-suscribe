package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroAlertaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Alerta;

public interface AlertaService {

    void crear(Alerta item) throws PSException;

    void modificar(Alerta item) throws PSException;

    ResultadoPaginadoDTO<Alerta> buscarAlertaFiltro(FiltroAlertaConsultaDTO filtro) throws PSException;

    Alerta buscarAlerta(String id) throws PSException;

}
