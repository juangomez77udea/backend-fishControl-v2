package com.fiscontrolbackend.fiscontrolbackend.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiscontrolbackend.fiscontrolbackend.security.jwt.JwtUtils;
import com.fiscontrolbackend.fiscontrolbackend.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Lista de rutas públicas que no requieren autenticación
    private final List<String> publicPaths = Arrays.asList(
            "/api/login",
            "/api/health/**",
            "/api/test/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Verificar si la ruta actual coincide con alguna de las rutas públicas
        String path = request.getServletPath();
        return publicPaths.stream()
                .anyMatch(p -> pathMatcher.match(p, path));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);

            try {
                if (jwtUtils.isTokenValid(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);

                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        // Continuar con la cadena de filtros
                        filterChain.doFilter(request, response);
                        return;
                    } catch (UsernameNotFoundException e) {
                        log.error("Usuario no encontrado: {}", username);
                        sendErrorResponse(response, "Usuario no encontrado", HttpStatus.UNAUTHORIZED);
                        return;
                    }
                } else {
                    // Si el token no es válido, verificar si está expirado para dar un mensaje específico
                    if (jwtUtils.isTokenExpired(token)) {
                        log.error("Token expirado");
                        sendErrorResponse(response, "Token expirado", HttpStatus.UNAUTHORIZED);
                    } else {
                        log.error("Token inválido");
                        sendErrorResponse(response, "Token inválido", HttpStatus.UNAUTHORIZED);
                    }
                    return;
                }
            } catch (ExpiredJwtException e) {
                log.error("Token expirado: {}", e.getMessage());
                sendErrorResponse(response, "Token expirado", HttpStatus.UNAUTHORIZED);
                return;
            } catch (Exception e) {
                log.error("Error al procesar token: {}", e.getMessage());
                sendErrorResponse(response, "Error al procesar token", HttpStatus.UNAUTHORIZED);
                return;
            }
        }

        // Si no hay token o no comienza con "Bearer ", continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Envía una respuesta de error al cliente.
     *
     * @param response La respuesta HTTP
     * @param message El mensaje de error
     * @param status El código de estado HTTP
     */
    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
