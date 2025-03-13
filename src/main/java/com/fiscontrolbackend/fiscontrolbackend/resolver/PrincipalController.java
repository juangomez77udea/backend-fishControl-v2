package com.fiscontrolbackend.fiscontrolbackend.resolver;

import com.fiscontrolbackend.fiscontrolbackend.models.ERole;
import com.fiscontrolbackend.fiscontrolbackend.models.RoleEntity;
import com.fiscontrolbackend.fiscontrolbackend.models.UserEntity;
import com.fiscontrolbackend.fiscontrolbackend.repositories.UserRepository;
import com.fiscontrolbackend.fiscontrolbackend.request.CreateUserDTO;
import com.fiscontrolbackend.fiscontrolbackend.request.LoginRequest;
import com.fiscontrolbackend.fiscontrolbackend.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class PrincipalController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.getEnabled()) { // 🔹 Verificar si el usuario está activo
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El usuario está deshabilitado.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateAccessToken(authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("username", authentication.getName());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/createUser")
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

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado con éxito");
    }
}