package com.envifo.envifo.dao;

import com.envifo.envifo.model.User;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional //Permite la capacidad de consultar a la base de datos
public class UserDaoImp implements UserDao{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<User> getUsers() {
        String query = "FROM User";
        return entityManager.createQuery(query).getResultList();
        //entity administra la consulta
        //Crea la consulta y con getResul transforma la consulta en una lista
    }

    @Override
    public void editUser(User updatedUser) {
        try {
            User existingUser = entityManager.find(User.class, updatedUser.getId());
            if (existingUser == null) {
                throw new IllegalArgumentException("Usuario no encontrado para el ID: " + updatedUser.getId());
            }

            existingUser.setNombre(updatedUser.getNombre());
            existingUser.setApellido(updatedUser.getApellido());
            existingUser.setTelefono(updatedUser.getTelefono());
            existingUser.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(updatedUser.getPassword());
            }

            entityManager.merge(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el usuario: " + e.getMessage(), e);
        }
    }


    @Override
    public User getUser(Long id) {
        try {
            String query = "FROM User WHERE id = :id";
            return entityManager.createQuery(query, User.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Usuario no encontrado
            throw new IllegalArgumentException("Usuario no encontrado para el ID: " + id);
        } catch (Exception e) {
            // Otros errores
            throw new RuntimeException("Error al obtener el usuario: " + e.getMessage(), e);
        }
    }



    @Override
    public void eliminar(Long id) {
        User user = entityManager.find(User.class, id);
        entityManager.remove(user);
    }

    @Override
    public void registrar(User user) {
        entityManager.merge(user);
    }

    /*
    @Override
    public boolean autenticacion(User user) {
        String query = "FROM User WHERE email = :email";
        List<User> lista = entityManager.createQuery(query)
                .setParameter("email", user.getEmail())
                .getResultList();

        if (lista.isEmpty()) {
            return false;
        }

        String passwordHashed = lista.get(0).getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.verify(passwordHashed, user.getPassword());

    }

     */

    @Override
    public User obtenerUsuarioPorCredenciales(User user) {
        String query = "FROM User WHERE email = :email";
        List<User> lista = entityManager.createQuery(query)
                .setParameter("email", user.getEmail())
                .getResultList();


        if (lista.isEmpty()) {
            return null;
        }

        String passwordHashed = lista.get(0).getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if (argon2.verify(passwordHashed, user.getPassword())) {
            return lista.get(0);
        }


        return null;
    }


}
