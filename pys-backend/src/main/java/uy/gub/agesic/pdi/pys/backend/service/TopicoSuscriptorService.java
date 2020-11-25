package uy.gub.agesic.pdi.pys.backend.service;

import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.Topico;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;

import java.util.List;

public interface TopicoSuscriptorService {

    void crear(TopicoSuscriptor item) throws PSException;

    void modificar(TopicoSuscriptor item) throws PSException;

    void eliminar(String topico, String suscriptor) throws PSException;

    List<TopicoSuscriptor> buscar(Topico topico) throws PSException;

    List<TopicoSuscriptor> buscarTodos() throws PSException;

    List<TopicoSuscriptor> buscarTopicosSuscriptor(Suscriptor suscriptor);

    List<TopicoSuscriptor> buscarTopicosSuscriptor(String topico) throws PSException;

    List<Suscriptor> buscarSuscriptoresPorTopico (String topico) throws PSException;

    TopicoSuscriptor buscarTopicoSuscriptor(String suscriptor, String topico) throws PSException;

}
