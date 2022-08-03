package ru.practicum.shareit.requests;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_requests")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 300)
    private String description;
    @NotNull
    @Column(name = "requester_id")
    private Long userRequesterId;
    private LocalDateTime created;

}