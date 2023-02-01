package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * ItemRequest.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")

public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User requester;
    private LocalDateTime created;
}
