package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User.
 */
@Data
public class User {
    private Long id;
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friendIds = new HashSet<>();
    private Map<Long, FriendStatus> friendIdsWithStatus = new HashMap<>();


    public enum FriendStatus {
        PENDING,
        CONFIRMED
    }
}
