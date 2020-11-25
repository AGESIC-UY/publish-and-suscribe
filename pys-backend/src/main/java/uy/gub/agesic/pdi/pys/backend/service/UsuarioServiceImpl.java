package uy.gub.agesic.pdi.pys.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroUsuarioDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Usuario;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private MongoTemplate mongoTemplate;

    @Autowired
    public UsuarioServiceImpl (MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResultadoPaginadoDTO<Usuario> getUsuarios(FiltroUsuarioDTO filtro) throws PSException {
        try {

            Query query = new Query();

            if (filtro.getNombre() != null) {
                query.addCriteria(Criteria.where(BackendServiceUtil.NOMBRE_PARAM).is(filtro.getNombre()));
            }

            if (filtro.getApellido() != null) {
                query.addCriteria(Criteria.where(BackendServiceUtil.APELLIDO_PARAM).regex(filtro.getApellido(), BackendServiceUtil.NO_CASE_SENSITIVE));
            }

            if (filtro.getLogin() != null) {
                query.addCriteria(Criteria.where(BackendServiceUtil.LOGIN_PARAM).regex(filtro.getLogin(), BackendServiceUtil.NO_CASE_SENSITIVE));
            }

            List<String> props = new ArrayList<>();
            props.add(BackendServiceUtil.FECHA_PARAM);
            Sort sort = new Sort(Sort.Direction.DESC, props);
            query.with(sort);

            ResultadoPaginadoDTO<Usuario> resultado = new ResultadoPaginadoDTO<>();
            resultado.setTotalTuplas(mongoTemplate.count(query, Usuario.class));

            QueryUtil.applySizeLimit(query, filtro);

            List<Usuario> listUsuarios = mongoTemplate.find(query, Usuario.class);
            resultado.setResultado(listUsuarios);

            return resultado;
        } catch (java.util.regex.PatternSyntaxException e) {
            throw new PSException("Fueron ingresados datos invalidos", null, "INVALIDFILTERDATA", e);
        }

    }

    @Override
    public Usuario getUsuario(String login) throws PSException {
        Query query = new Query();
        query.addCriteria(Criteria.where(BackendServiceUtil.LOGIN_PARAM).is(login));
        return this.mongoTemplate.findOne(query, Usuario.class);

    }

    @Override
    public void insertarUsuario(Usuario usuario) throws PSException {
        this.mongoTemplate.save(usuario);
    }

    @Override
    public void eliminarUsuario(String login) throws PSException {
        Usuario usuario = getUsuario(login);
        if(usuario != null){
         this.mongoTemplate.remove(usuario);
        }
    }

}
