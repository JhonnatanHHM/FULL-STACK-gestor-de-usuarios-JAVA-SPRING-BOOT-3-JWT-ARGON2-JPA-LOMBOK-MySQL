package com.envifo.envifo.dao;

import com.envifo.envifo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> getUsers();

    void editUser(User updatedUser);

    User getUser(Long id);

    void eliminar(Long id);

    void registrar(User user);

    //boolean autenticacion(User user);

    User obtenerUsuarioPorCredenciales(User user);

}
