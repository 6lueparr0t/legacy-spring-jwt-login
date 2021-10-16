package com.spring_jwt_server.controller;

import com.spring_jwt_server.domain.user.User;
import com.spring_jwt_server.service.sign.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
@Slf4j
public class SignController {

    private static final String AUTHORIZATION = "Authorization";
    private static final String APPLICATION_JSON_VALUE = "application/json";
    private final SignService signService;

    @PostMapping("/up")
    public ResponseEntity<Map<String, Object>> signUpUser(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          @RequestBody User user) throws IOException {

        final URI uri = linkTo(methodOn(this.getClass()).signUpUser(request, response, user)).toUri();

        try {
            Map<String, Object> data = signService.signUp(user, request);

            response.setContentType(APPLICATION_JSON_VALUE);
            return ResponseEntity.created(uri).body(data);
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new IOException(e);
        }
    }

    @GetMapping("/out")
    public ResponseEntity<Map<String, String>> signOutUser(HttpServletRequest request,
                                                          HttpServletResponse response) throws IOException {

        final URI uri = linkTo(methodOn(this.getClass()).signOutUser(request, response)).toUri();

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        try {
            Map<String, String> data = signService.signOut(authorizationHeader);

            response.setContentType(APPLICATION_JSON_VALUE);
            return ResponseEntity.created(uri).body(data);
        } catch (Exception e) {
            response.setHeader("error", e.getMessage());
            response.setStatus(FORBIDDEN.value());

            throw new IOException(e);
        }

    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, String>> signCheckUser(HttpServletRequest request,
                                                           HttpServletResponse response) throws IOException {

        final URI uri = linkTo(methodOn(this.getClass()).signCheckUser(request, response)).toUri();

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        try {
            Map<String, String> data = signService.signCheck(authorizationHeader);

            response.setContentType(APPLICATION_JSON_VALUE);
            return ResponseEntity.created(uri).body(data);
        } catch (Exception e) {
            response.setHeader("error", e.getMessage());
            response.setStatus(FORBIDDEN.value());

            throw new IOException(e);
        }

    }
}
