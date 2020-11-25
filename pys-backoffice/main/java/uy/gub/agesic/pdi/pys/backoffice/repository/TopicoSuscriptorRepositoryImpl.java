package uy.gub.agesic.pdi.pys.backoffice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.backend.service.TopicoSuscriptorService;
import uy.gub.agesic.pdi.pys.domain.Suscriptor;
import uy.gub.agesic.pdi.pys.domain.TopicoSuscriptor;

import java.util.List;

@Service
public class TopicoSuscriptorRepositoryImpl implements TopicoSuscriptorRepository {

    private TopicoSuscriptorService topicoSuscriptorService;

    @Autowired
    public TopicoSuscriptorRepositoryImpl(TopicoSuscriptorService topicoSuscriptorService){ this.topicoSuscriptorService = topicoSuscriptorService; }


    @Override
    public void crear(TopicoSuscriptor item) throws PSException {
        topicoSuscriptorService.crear(item);
    }

    @Override
    public void eliminar(String topico, String suscriptor) throws PSException {
        topicoSuscriptorService.eliminar(topico, suscriptor);
    }

    @Override
    public List<TopicoSuscriptor> buscarTopicosSuscriptor(String topico) throws PSException {
        return topicoSuscriptorService.buscarTopicosSuscriptor(topico);
    }

    @Override
    public List<Suscriptor> buscarSuscriptoresPorTopico(String topico) throws PSException {
        return topicoSuscriptorService.buscarSuscriptoresPorTopico(topico);
    }

    @Override
    public TopicoSuscriptor buscarTopicoSuscriptor(String suscriptor, String topico) throws PSException {
        return topicoSuscriptorService.buscarTopicoSuscriptor(suscriptor, topico);
    }
}
