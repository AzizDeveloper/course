package dev.aziz.course.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.aziz.course.dtos.UserDto;
import dev.aziz.course.entities.Role;
import dev.aziz.course.exceptions.AppException;
import dev.aziz.course.repositories.RoleRepository;
import dev.aziz.course.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {
    
    @Value("${jwt.secret:secret-value}")
    private String secretKey;

    private final UserService userService;
    private final RoleRepository roleRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3_600_000);
        UserDto userDto = userService.findByEmail(email);
        List<String> roles = userDto.getRoles().stream().map(Role::getName).toList();
        return JWT.create()
                .withIssuer(email)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Authentication validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        UserDto user = userService.findByEmail(decodedJWT.getIssuer());

        List<String> rolesNames = decodedJWT.getClaim("roles").asList(String.class);

        Set<Role> roles = rolesNames.stream()
                .map(roleName -> roleRepository.findRoleByName(roleName)
                        .orElseThrow(() -> new AppException("Role not found.", HttpStatus.NOT_FOUND)))
                .collect(Collectors.toSet());

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    public Long getUserId(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        UserDto user = userService.findByEmail(decodedJWT.getIssuer());
        return user.getId();
    }

}













