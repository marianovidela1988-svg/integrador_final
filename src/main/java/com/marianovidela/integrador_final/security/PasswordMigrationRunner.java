package com.marianovidela.integrador_final.security;

import com.marianovidela.integrador_final.model.Administrador;
import com.marianovidela.integrador_final.repository.AdministradorRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Migra en el arranque cualquier contraseña de administrador que todavía esté
 * en texto plano (de antes de introducir BCrypt) a un hash BCrypt, sin
 * necesidad de tocar la base de datos a mano.
 */
@Component
public class PasswordMigrationRunner implements ApplicationRunner {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$.*");

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Administrador> administradores = administradorRepository.findAll();
        long migrados = 0;

        for (Administrador administrador : administradores) {
            String pass = administrador.getPass();
            if (pass != null && !BCRYPT_PATTERN.matcher(pass).matches()) {
                administrador.setPass(passwordEncoder.encode(pass));
                administradorRepository.save(administrador);
                migrados++;
            }
        }

        if (migrados > 0) {
            System.out.println("[PasswordMigrationRunner] Se migraron " + migrados + " contraseña(s) de administrador a BCrypt.");
        }
    }
}
