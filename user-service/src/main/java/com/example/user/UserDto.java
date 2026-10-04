package com.example.user;

import java.io.Serializable;

/** Serializable because Redis caching uses JDK serialization by default. */
public record UserDto(Long id, String name, String email) implements Serializable {

    static UserDto from(User u) {
        return new UserDto(u.getId(), u.getName(), u.getEmail());
    }
}
