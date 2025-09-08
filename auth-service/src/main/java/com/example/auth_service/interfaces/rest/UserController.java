package com.example.auth_service.interfaces.rest;

import com.example.auth_service.application.user.ListUsersHandler;
import com.example.auth_service.application.user.RegisterUserHandler;
import com.example.auth_service.interfaces.rest.dto.user.UserRequest;
import com.example.auth_service.interfaces.rest.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping( "/users")
@RequiredArgsConstructor
public class UserController {

    private final ListUsersHandler list;
    private final RegisterUserHandler register;

    @GetMapping()
    public ResponseEntity<Page<UserResponse>> list(Pageable pageable) {
        Page<UserResponse> page = list.handle(pageable);

        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest user) {
        UserResponse created = register.handle(user.name(), user.email(), user.password());

        return ResponseEntity.created(URI.create("users/" + created.id())).body(created);
    }
}
