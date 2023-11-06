package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .text(comment.getText())
            .authorName(comment.getAuthor().getName())
            .created(comment.getCreated())
            .build();
    }

    public static Comment toComment(CommentDto dto) {
        return Comment.builder()
            .id(dto.getId())
            .text(dto.getText())
            .created(dto.getCreated())
            .build();
    }
}