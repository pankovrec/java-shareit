package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemDtoWithBookingCommentDtoTest {

    @Test
    void testConstructor() {
        User author = new User(1L, "user1", "user1@mail.ru");
        Item item = new Item(1L, "item1", "item1_desc", true, author, null);
        Comment comment = new Comment(1L, "comment1", item, author, LocalDateTime.of(1, 2,
                3, 4, 5));
        ItemDtoWithBooking.CommentDto commentDto = ItemMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}
