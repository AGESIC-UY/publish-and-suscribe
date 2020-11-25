package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.List;

public interface TopicoSuscriptorRepository {

    void crear(TopicoSuscriptor item) throws PSException;
    void eliminar(String topico, String suscriptor) throws PSException;
    List<TopicoSuscriptor> buscarTopicosSuscriptor(String topico) throws PSException;
    List<Suscriptor> buscarSuscriptoresPorTopico (String topico) throws PSException;
    TopicoSuscriptor buscarTopicoSuscriptor(String suscriptor, String topico) throws PSException;

}
