package com.example.auth_service.domain.user;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

// CORREÇÃO: Removido "extends JpaRepository<User, UUID>"
// A interface de domínio agora define apenas os métodos necessários para a aplicação.
public interface UserRepository {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(UUID id);
    Page<User> findAll(Pageable pageable);
}