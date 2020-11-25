package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroEntregaConsultaDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Entrega;
import uy.gub.agesic.pdi.pys.domain.ReporteEntregas;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

public interface EntregaService {

    void upsert(Entrega item) throws PSException;

    void crearColeccion(String topico, String suscriptor) throws PSException;

    void eliminar(Entrega item) throws PSException;

    int cancelar(Entrega item) throws PSException;

    void cancelarEntregasSuscriptor(Suscriptor suscriptor) throws  PSException;

    void cancelarEntregasTopicoSuscriptor(Suscriptor suscriptor, Topico topico) throws PSException;

    void eliminarEntregasSuscriptor(Suscriptor suscriptor) throws  PSException;

    Entrega buscarPrimera(Suscriptor suscriptor, Topico topico) throws PSException;

    Entrega buscarPrimera(String suscriptor, String topico) throws PSException;

    Entrega buscarEntrega(String idEntrega, String suscriptor, String topico) throws PSException;

    List<Entrega> buscarEntregasTopicoSuscriptor(String suscriptor, String topico) throws PSException;

    List<Entrega> buscarEntregasNovedad(String uuidNovedad) throws PSException;

    ResultadoPaginadoDTO<Entrega> buscarEntregaFiltro(FiltroEntregaConsultaDTO filtro) throws PSException;

    ResultadoPaginadoDTO<ReporteEntregas> reporteEntregas(FiltroEntregaConsultaDTO filtro) throws PSException;



}
