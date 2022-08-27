package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @NotBlank
    private String text;
    @Column(name = "item_id")
    private Long itemId;
    @Column(name = "author_id")
    private Long authorId;
    private LocalDateTime created;

    public Comments() {
    }
}