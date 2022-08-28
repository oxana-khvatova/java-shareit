package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapper {
    private final UserRepository userRepository;

    @Autowired
    public CommentMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CommentDto toCommentDto(Comments comments) {
        User user;
        if (userRepository.findById(comments.getAuthorId()).isPresent()) {
            user = userRepository.findById(comments.getAuthorId()).get();
            return new CommentDto(comments.getId(), comments.getText(), user.getId(),
                    comments.getItemId(), comments.getCreated());
        }
        return null;
    }

    public List<CommentDto> toCommentListDto(List<Comments> comments) {
        List<CommentDto> com = new ArrayList<>();
        for (Comments comment : comments) {
            com.add(toCommentDto(comment));
        }
        return com;
    }

    public Comments toComment(CommentDto commentsDto) {
        Comments comment = new Comments();
        comment.setId(commentsDto.getId());
        comment.setItemId(commentsDto.getItemId());
        comment.setText(commentsDto.getText());
        comment.setAuthorId(commentsDto.getAuthorId());
        comment.setCreated(commentsDto.getCreated());
        return comment;
    }
    public List<Comments> toCommentsList(List<CommentDto> commentsDto) {
        List<Comments> com = new ArrayList<>();
        for (CommentDto comment : commentsDto) {
            com.add(toComment(comment));
        }
        return com;
    }

}