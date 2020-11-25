package uy.gub.agesic.pdi.pys.backoffice.repository;

import uy.gub.agesic.pdi.common.utiles.dtos.ResultadoPaginadoDTO;
import uy.gub.agesic.pdi.pys.backend.dtos.FiltroUsuarioDTO;
import uy.gub.agesic.pdi.pys.backend.exceptions.PSException;
import uy.gub.agesic.pdi.pys.domain.Usuario;

public interface UsuarioRepository {

    ResultadoPaginadoDTO<Usuario> getUsuarios(FiltroUsuarioDTO filtro) throws PSException;

    Usuario getUsuario(String login) throws PSException;

    void insertarUsuario(Usuario usuario) throws PSException;

    void eliminarUsuario(String login) throws PSException;
}
