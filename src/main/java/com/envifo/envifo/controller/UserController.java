package com.envifo.envifo.controller;

import com.envifo.envifo.dao.UserDao;
import com.envifo.envifo.model.User;
import com.envifo.envifo.utils.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JWTUtil jwtUtil;


    @RequestMapping(value = "api/usuarios", method = RequestMethod.GET)
    public List<User> getUsers(@RequestHeader(value="Authorization") String token) {
        if (!validarToken(token)) { return null; }

        return userDao.getUsers();
    }

    @RequestMapping(value = "api/usuario/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestHeader(value = "Authorization") String token,
                                     @PathVariable Long id) {
        if (!validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        try {
            User user = userDao.getUser(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            user.setPassword("");
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el usuario: " + e.getMessage());
        }
    }


    private boolean validarToken(String token) {
        String usuarioId = jwtUtil.getKey(token);
        return usuarioId != null;
    }

    @RequestMapping(value = "api/usuario/{id}", method = RequestMethod.DELETE)
    public void eliminar(@RequestHeader(value="Authorization") String token,
                         @PathVariable Long id) {
        if (!validarToken(token)) { return; }
        userDao.eliminar(id);
    }

    @RequestMapping(value = "api/usuario", method = RequestMethod.PUT)
    public ResponseEntity<?> editUser(@RequestHeader(value = "Authorization") String token,
                                      @RequestBody User updatedUser) {
        if (!validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
        try {
            // Si se proporciona una contraseña, cifrarla antes de actualizar
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
                String hash = argon2.hash(1, 1024, 1, updatedUser.getPassword());
                updatedUser.setPassword(hash);
            }

            userDao.editUser(updatedUser);
            return ResponseEntity.ok("Usuario actualizado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }




    @RequestMapping(value = "api/usuarios", method = RequestMethod.POST)
    public void registrar(@RequestBody User user) {
        // @RequestBody se encargar de convertir el JSON a un usuario para la clase User

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, user.getPassword());
        user.setPassword(hash);

        userDao.registrar(user);
    }
}
