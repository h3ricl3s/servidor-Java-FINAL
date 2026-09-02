package com.labanta.servidorlocal.controller;

import com.labanta.servidorlocal.dto.GeoLocationResponseDTO;
import com.labanta.servidorlocal.dto.LoginRequestDTO;
import com.labanta.servidorlocal.dto.RegistoRequestDTO;
import com.labanta.servidorlocal.model.Utilizador;
import com.labanta.servidorlocal.service.AuthService;
import com.labanta.servidorlocal.service.EmailService;
import com.labanta.servidorlocal.service.GeoService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private  final GeoService geoService;

    public AuthController(AuthService authService, EmailService emailService, GeoService geoService) {
        this.authService = authService;
        this.emailService = emailService;
        this.geoService = geoService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO login){
        return authService.login(login.getUsername(), login.getPassword());
    }

    @PostMapping("/registar")
    public Utilizador registar(@RequestBody RegistoRequestDTO registo){
        return authService.registarUtilizador(registo.getUsername(), registo.getEmail(), registo.getPassword());
    }

    @PostMapping("/alerta-login")
    public String alertaLogin(@RequestParam String email , @RequestParam String ip){
        GeoLocationResponseDTO res = geoService.localizarIp(ip);
        //emailService.enviarAlertaSeguranca(email, res.getCity(), res.getCountry_name());
        return "ALERTA SEGURANCA";
    }

}
