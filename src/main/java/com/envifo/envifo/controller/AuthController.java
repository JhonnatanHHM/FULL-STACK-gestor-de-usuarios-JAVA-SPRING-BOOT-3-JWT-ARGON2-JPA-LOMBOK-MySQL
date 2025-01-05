package com.envifo.envifo.controller;

import com.envifo.envifo.dao.UserDao;
import com.envifo.envifo.model.User;
import com.envifo.envifo.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JWTUtil jwtUtil;

    @RequestMapping(value = "api/login", method = RequestMethod.POST)
    public String login(@RequestBody User user) {

        User usuarioLogueado = userDao.obtenerUsuarioPorCredenciales(user);
        if (usuarioLogueado != null) {

            System.out.println("Usuario logueado: " + usuarioLogueado);
            System.out.println("Nombre del usuario logueado: " + usuarioLogueado.getNombre());

            String tokenJwt = jwtUtil.create(String.valueOf(usuarioLogueado.getId()), usuarioLogueado.getEmail()
                    ,usuarioLogueado.getNombre());

            System.out.println("Token generado: " + tokenJwt);

            return tokenJwt;
        }
        return "FAIL";
    }
}
