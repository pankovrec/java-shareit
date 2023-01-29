package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ShareItApp.class)
public class ItemJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void search() {
        User user = new User(1, "user1", "user1@mail.ru");
        User secondUser = new User(2, "user2", "user2@mail.ru");
        userRepository.save(user);
        userRepository.save(secondUser);

        Item item = new Item(1, "item1", "item1_desc",
                true, user, null);
        Item secondItem = new Item(2, "item2", "item2_desc",
                true, secondUser, null);
        itemRepository.save(item);
        itemRepository.save(secondItem);

        Page<Item> items = itemRepository.searchItems("itEm", PageRequest.of(0, 5));
        assertThat(items.toList().size(), equalTo(2));
        for (Item foundedItem : items) {
            if (foundedItem.getId() == (1)) {
                assertThat(foundedItem.getId(), equalTo(1L));
                assertThat(foundedItem.getName(), equalTo("item1"));
                assertThat(foundedItem.getDescription(), equalTo("item1_desc"));
                assertThat(foundedItem.getAvailable(), equalTo(true));
                assertThat(foundedItem.getOwner().getId(), equalTo(1L));
            } else if (foundedItem.getId() == (2)) {
                assertThat(foundedItem.getId(), equalTo(2L));
                assertThat(foundedItem.getName(), equalTo("item2"));
                assertThat(foundedItem.getDescription(), equalTo("item2_desc"));
                assertThat(foundedItem.getAvailable(), equalTo(true));
                assertThat(foundedItem.getOwner().getId(), equalTo(2L));
            }
        }
    }
}
