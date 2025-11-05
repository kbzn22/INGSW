package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Usuario;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UsuarioRepository {
    private final Map<String, Usuario> byUsername = new ConcurrentHashMap<>();

    public void save(Usuario u) { byUsername.put(u.getUsuario(), u); }
    public Optional<Usuario> findByUsername(String username){
        return Optional.ofNullable(byUsername.get(username));
    }
    public void clear(){ byUsername.clear(); }
}
