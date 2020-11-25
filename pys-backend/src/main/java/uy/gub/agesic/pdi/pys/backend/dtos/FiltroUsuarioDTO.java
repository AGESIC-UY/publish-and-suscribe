package uy.gub.agesic.pdi.pys.backend.dtos;

import uy.gub.agesic.pdi.common.utiles.dtos.FiltroDTO;

public class FiltroUsuarioDTO extends FiltroDTO {

    private String login;
    private String nombre;
    private String apellido;
    private String password;
    private String permiso;

    public FiltroUsuarioDTO () {}

    public FiltroUsuarioDTO (Integer currentPage, Integer pageSize) {
        super(currentPage, pageSize);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermiso() {
        return permiso;
    }

    public void setPermiso(String permiso) {
        this.permiso = permiso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltroUsuarioDTO)) return false;
        if (!super.equals(o)) return false;

        FiltroUsuarioDTO that = (FiltroUsuarioDTO) o;

        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;
        if (apellido != null ? !apellido.equals(that.apellido) : that.apellido != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return permiso != null ? permiso.equals(that.permiso) : that.permiso == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (apellido != null ? apellido.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (permiso != null ? permiso.hashCode() : 0);
        return result;
    }
}
