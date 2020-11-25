package uy.gub.agesic.pdi.pys.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "usuarios")
public class Usuario implements Serializable, Comparable<Usuario> {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    private String id;
    private String login;
    private String nombre;
    private String apellido;
    private String password;
    private String permiso;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public int compareTo(Usuario o) {
        if(login.compareTo(o.getLogin()) < 0)
            return -1;
        else if(login.compareTo(o.getLogin()) > 0)
            return 1;
        else return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;

        Usuario usuario = (Usuario) o;

        if (!id.equals(usuario.id)) return false;
        if (login != null ? !login.equals(usuario.login) : usuario.login != null) return false;
        if (nombre != null ? !nombre.equals(usuario.nombre) : usuario.nombre != null) return false;
        if (apellido != null ? !apellido.equals(usuario.apellido) : usuario.apellido != null) return false;
        if (password != null ? !password.equals(usuario.password) : usuario.password != null) return false;
        return permiso != null ? permiso.equals(usuario.permiso) : usuario.permiso == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (apellido != null ? apellido.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (permiso != null ? permiso.hashCode() : 0);
        return result;
    }

}
