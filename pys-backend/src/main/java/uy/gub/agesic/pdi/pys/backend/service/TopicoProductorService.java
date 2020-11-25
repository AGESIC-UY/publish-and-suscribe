package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Productor;
import uy.gub.agesic.pdi.pys.domain.TopicoProductor;

import java.util.List;

public interface TopicoProductorService {

    void crear(TopicoProductor item) throws PSException;

    void modificar(TopicoProductor item) throws PSException;

    void eliminar(String topico, String productor) throws PSException;

    List<TopicoProductor> buscarTopicoProductor(String topico) throws PSException;

    List<Productor> buscarProductoresPorTopico (String topico) throws PSException;

    TopicoProductor buscarTopicoProductor(String topico, String productor) throws PSException;

}
