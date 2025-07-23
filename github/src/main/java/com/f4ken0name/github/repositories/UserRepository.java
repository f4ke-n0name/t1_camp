package com.f4ken0name.github.repositories;

import com.f4ken0name.github.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);
}
