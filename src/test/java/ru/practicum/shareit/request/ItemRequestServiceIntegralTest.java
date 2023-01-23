package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.request.dto.InputRequestItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegralTest {
    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;

    private final InputRequestItemDto itemRequest1 = new InputRequestItemDto();
    private final InputRequestItemDto itemRequest2 = new InputRequestItemDto();

    @BeforeEach
    public void init() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE;").executeUpdate();
        em.createNativeQuery("TRUNCATE table items restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table users restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table requests restart identity;").executeUpdate();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE;").executeUpdate();
        itemRequest1.setDescription("Test request 1");
        itemRequest2.setDescription("Test request 2");
    }

    @Test
    public void createRequest() {
        UserDto user = new UserDto(1L, "user1", "user1@email.com");

        InputRequestItemDto itemRequest = new InputRequestItemDto();
        itemRequest.setDescription("Test description");
        userService.createUser(user);
        service.createRequest(1, itemRequest);
        TypedQuery<ItemRequest> query = em.createQuery(
                "select i from ItemRequest i where i.description = :description",
                ItemRequest.class);
        ItemRequest newItemRequest = query.setParameter("description", itemRequest.getDescription())
                .getSingleResult();
        assertThat(newItemRequest.getId(), notNullValue());
        assertThat(newItemRequest.getId(), equalTo(1L));
        assertThat(newItemRequest.getRequester(), equalTo(UserMapper.toUser(user)));
        assertThat(newItemRequest.getCreated(), notNullValue());
        assertThat(newItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
    }
}