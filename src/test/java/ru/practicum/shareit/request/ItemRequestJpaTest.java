package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestJpaTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository repository;
    private final ItemRequest itemRequest1 = new ItemRequest();
    private final User user = new User();

    @BeforeEach
    public void init() {
        itemRequest1.setDescription("test1desc");
        itemRequest1.setCreated(LocalDateTime.now());
        user.setName("user1");
        user.setEmail("user1@test.ru");
        itemRequest1.setRequester(user);
        entityManager.persist(user);
        entityManager.persist(itemRequest1);
        entityManager.flush();
    }

    @Test
    public void findAllByRequester() {
        List<ItemRequest> found = repository.findAllByRequesterId(1L);
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(itemRequest1.getDescription(), found.get(0).getDescription());
        Assertions.assertEquals(itemRequest1.getCreated(), found.get(0).getCreated());
        Assertions.assertEquals(itemRequest1.getRequester(), user);
    }

    @Test
    public void findAll() {
        Pageable pageable = PageRequest.of(0, 5);
        List<ItemRequest> found = repository.findAll(pageable).getContent();
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(itemRequest1.getDescription(), found.get(0).getDescription());
        Assertions.assertEquals(itemRequest1.getCreated(), found.get(0).getCreated());
        Assertions.assertEquals(itemRequest1.getRequester(), user);
    }
}