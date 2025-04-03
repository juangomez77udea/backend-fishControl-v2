package com.fiscontrolbackend.fiscontrolbackend.resolver;

import com.fiscontrolbackend.fiscontrolbackend.models.user.ERole;
import com.fiscontrolbackend.fiscontrolbackend.models.user.RoleEntity;
import com.fiscontrolbackend.fiscontrolbackend.models.user.UserEntity;
import com.fiscontrolbackend.fiscontrolbackend.repositories.user.UserRepository;
import com.fiscontrolbackend.fiscontrolbackend.request.CreateUserDTO;
import com.fiscontrolbackend.fiscontrolbackend.request.LoginRequest;
import com.fiscontrolbackend.fiscontrolbackend.security.jwt.JwtUtils;
import com.fiscontrolbackend.fiscontrolbackend.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
@Slf4j
public class PrincipalController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> {
                        log.error("Intento de login fallido: Usuario no encontrado - Username: {}",
                                loginRequest.getUsername());
                        return new UsernameNotFoundException("Usuario no encontrado");
                    });

            if (!user.getEnabled()) {
                log.error("Intento de login fallido: Usuario deshabilitado - Username: {}",
                        loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El usuario está deshabilitado.");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener los detalles del usuario para incluir los roles en el token
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String jwt = jwtUtils.generateAccessToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", authentication.getName());

            log.info("Login exitoso - Username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.error("Intento de login fallido: Credenciales inválidas - Username: {}",
                    loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error en login - Username: {} - Error: {}",
                    loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación");
        }
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {

        if (userRepository.findByUsername(createUserDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
                .map(role -> RoleEntity.builder()
                        .name(ERole.valueOf(role)).build())
                .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .email(createUserDTO.getEmail())
                .enabled(createUserDTO.getEnabled())
                .roles(roles)
                .build();

        userRepository.save(userEntity);

        return ResponseEntity.ok("Usuario creado correctamente");
    }

    // Método para eliminar un usuario por ID - Solo para administradores
    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado con éxito");
    }

    // Método para eliminar un usuario por nombre de usuario - Solo para administradores
    @DeleteMapping("/deleteUserByUsername/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("Usuario '" + username + "' eliminado con éxito");
    }

    // Método para desactivar un usuario (eliminación lógica) - Solo para administradores
    @PatchMapping("/disableUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        user.setEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok("Usuario desactivado con éxito");
    }

    // Método para activar un usuario (eliminación lógica) - Solo para administradores
    @PatchMapping("/enableUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok("Usuario activado con éxito");
    }

    // Método para obtener todos los usuarios - Solo para administradores
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();

        // Ocultar las contraseñas en la respuesta
        users.forEach(user -> user.setPassword("[PROTECTED]"));

        return ResponseEntity.ok(users);
    }

    // Método para obtener un usuario por ID - Solo para administradores o el propio usuario
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Ocultar la contraseña en la respuesta
        user.setPassword("[PROTECTED]");

        return ResponseEntity.ok(user);
    }
}

