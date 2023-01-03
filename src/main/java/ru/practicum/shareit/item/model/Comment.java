package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Comment model.
 */

@Data
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Item item;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User author;
    private LocalDateTime created = LocalDateTime.now();
}