package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroTopicoDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

public interface TopicoService {

    void crear(Topico item) throws PSException;

    void modificar(Topico item) throws PSException;

    void eliminar(String item) throws PSException;

    void habilitar(String item) throws PSException;

    void deshabilitar(String item) throws PSException;

    Topico buscar(String nombre) throws PSException;

    ResultadoPaginadoDTO<Topico> buscarTopicosFiltro(FiltroTopicoDTO filtro) throws PSException;

    List<Topico> getAll() throws PSException;

}
