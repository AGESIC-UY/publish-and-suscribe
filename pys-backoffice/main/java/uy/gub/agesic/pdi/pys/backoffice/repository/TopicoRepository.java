package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroTopicoDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Topico;

import java.util.List;

public interface TopicoRepository {
    ResultadoPaginadoDTO<Topico> buscarTopicos(FiltroTopicoDTO filtro) throws PSException;

    Topico buscarTopico(String nombre) throws PSException;

    void crearTopico(Topico topico) throws PSException;

    void eliminarTopicos(List<String> nombres) throws PSException;

    void modificarTopico(Topico topico) throws PSException;

    List<Topico> getAll() throws PSException;
}
