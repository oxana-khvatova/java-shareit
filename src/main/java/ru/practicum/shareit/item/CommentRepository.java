package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comments;

public interface CommentRepository extends JpaRepository<Comments, Long> {
}