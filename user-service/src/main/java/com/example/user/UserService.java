package com.example.user;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private static final String CACHE = "users";

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /** Cache-aside: Redis first, database on a miss. */
    @Cacheable(cacheNames = CACHE, key = "#id")
    public UserDto get(Long id) {
        return repository.findById(id).map(UserDto::from).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public UserDto create(UserRequest req) {
        return UserDto.from(repository.save(new User(req.name(), req.email())));
    }

    @Transactional
    @CachePut(cacheNames = CACHE, key = "#id")
    public UserDto update(Long id, UserRequest req) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setName(req.name());
        user.setEmail(req.email());
        return UserDto.from(user);
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE, key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
