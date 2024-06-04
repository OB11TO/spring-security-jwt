package ru.ob11to.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ob11to.springjwt.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u " +
            "FROM User u " +
            "JOIN FETCH u.userRole " +
            "WHERE u.login = :login")
    Optional<User> findByLogin(String login);
}
