package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Long id;
    @NotEmpty
    @Column(nullable = false)
    private String name;
    @NotEmpty
    @Column(nullable = false)
    private String description;
    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Long owner;
}