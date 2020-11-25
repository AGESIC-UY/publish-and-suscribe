package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroAlertaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.AlertaService;
import uy.gub.agesic.pdi.pys.domain.Alerta;

@Service
public class AlertaRepositoryImpl implements AlertaRepository {

    private AlertaService alertaService;

    @Autowired
    public AlertaRepositoryImpl (AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @Override
    public ResultadoPaginadoDTO<Alerta> buscarAlertaFiltro(FiltroAlertaConsultaDTO filtro) throws PSException {
        return this.alertaService.buscarAlertaFiltro(filtro);
    }

    @Override
    public Alerta buscarAlerta(String id) throws PSException {
        return this.alertaService.buscarAlerta(id);
    }

}
