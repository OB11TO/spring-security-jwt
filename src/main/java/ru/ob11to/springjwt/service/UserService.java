package ru.ob11to.springjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ob11to.springjwt.dto.UserCreateDto;
import ru.ob11to.springjwt.dto.UserReadDto;
import ru.ob11to.springjwt.mapper.UserMapper;
import ru.ob11to.springjwt.repository.UserRepository;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .map(user -> new User(
                        user.getLogin(),
                        user.getPassword(),
                        Collections.singleton(user.getUserRole())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to user: " + login));
    }

    @Transactional
    public List<UserReadDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(toList());
    }

    @Transactional
    public Optional<UserReadDto> findById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }


    @Transactional
    public UserReadDto createUser(UserCreateDto userCreateDto) {
        return Optional.of(userCreateDto)
                .map(userMapper::toEntity)
                .map(user -> {
                    user.setCreatedAt(ZonedDateTime.now());
                    return user;
                })
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(userCreateDto.password()));
                    return user;
                })
                .map(userRepository::save)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Failed to create user"));
    }

    @Transactional
    public boolean deleteUser(Integer id) {
        return userRepository.findById(id)
                .map(entity -> {
                    userRepository.delete(entity);
                    userRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}
