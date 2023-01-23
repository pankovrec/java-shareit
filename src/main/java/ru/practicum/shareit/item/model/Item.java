package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * Item model.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User owner;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private ItemRequest request;
}
