package ru.practicum.shareit.booking;

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
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ShareItApp.class)
public class BookingJpaTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository repository;
    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();
    private final Booking booking1 = new Booking();
    private final Booking booking2 = new Booking();

    @BeforeEach
    public void init() {
        user1.setName("User1");
        user1.setEmail("user1@mail.ru");
        user2.setName("User2");
        user2.setEmail("user2@mail.ru");
        item1.setOwner(user1);
        item1.setName("Item1");
        item1.setAvailable(true);
        item1.setDescription("Item1_desc");
        item2.setOwner(user2);
        item2.setName("Item2");
        item2.setAvailable(true);
        item2.setDescription("Item2_desc");
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(1));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        booking2.setStatus(Status.WAITING);
        booking2.setItem(item2);
        booking2.setBooker(user1);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();
    }

    @Test
    public void findEndIsBefore() {
        List<Booking> bookings = repository.findByBooker_IdAndEndIsBefore(user2.getId(), LocalDateTime.now()
                .plusDays(5));
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void findByBooker() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> bookings = repository.findAllByBookerIdOrderByStartDesc(user1.getId(), pageable);
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking2, bookings.get(0));
    }

    @Test
    public void findCurrent() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b " +
                        "where b.booker.id = :id " +
                        "and b.start < :dateTime " +
                        "and b.end > :dateTime", Booking.class);
        query.setParameter("id", 2L);
        query.setParameter("dateTime", LocalDateTime.now());
        List<Booking> bookingList = query.getResultList();
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking1, bookingList.get(0));
    }

    @Test
    public void findBookingByBookerAndStatus() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b " +
                        "left join b.item i " +
                        "where b.booker.id = :id " +
                        "and b.status = :status", Booking.class);
        query.setParameter("id", 1L);
        query.setParameter("status", Status.WAITING);
        List<Booking> bookingList = query.getResultList();
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking2, bookingList.get(0));
    }

    @Test
    public void findBookingByBookerAndOwner() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b " +
                        "left join b.item i " +
                        "where i.owner.id = :id " +
                        "and b.status = :status", Booking.class);
        query.setParameter("id", 1L);
        query.setParameter("status", Status.APPROVED);
        List<Booking> bookingList = query.getResultList();
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking1, bookingList.get(0));
    }

    @Test
    public void findByOwnerPast() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b " +
                        "left join b.item i " +
                        "where i.owner.id = :id " +
                        "and b.end < :end", Booking.class);
        query.setParameter("id", 1L);
        query.setParameter("end", LocalDateTime.now().plusDays(2));
        List<Booking> bookingList = query.getResultList();
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking1, bookingList.get(0));
    }

    @Test
    public void findByOwnerFuture() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b " +
                        "left join b.item i " +
                        "where i.owner.id = :id " +
                        "and b.start > :start", Booking.class);
        query.setParameter("id", 2L);
        query.setParameter("start", LocalDateTime.now());
        List<Booking> bookingList = query.getResultList();
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking2, bookingList.get(0));
    }

    @Test
    public void findByOwnerCurrent() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b " +
                        "left join b.item i " +
                        "where i.owner.id = :id " +
                        "and b.start < :time " +
                        "and b.end > :time", Booking.class);
        query.setParameter("id", 1L);
        query.setParameter("time", LocalDateTime.now());
        List<Booking> bookingList = query.getResultList();
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking1, bookingList.get(0));
    }

    @Test
    public void findByOwner() {
        TypedQuery<Booking> query = entityManager.getEntityManager()
                .createQuery("select b from Booking b " +
                        "left join b.item i " +
                        "where i.owner.id = :id", Booking.class);
        query.setParameter("id", 1L);
        List<Booking> bookingList = query.getResultList();
        Assertions.assertEquals(1, bookingList.size());
        Assertions.assertEquals(booking1, bookingList.get(0));
    }

    @Test
    public void findLast() {
        Query nativeQuery = entityManager.getEntityManager()
                .createNativeQuery("SELECT * from booking " +
                        "left join items i on booking.item_id = i.id " +
                        "where item_id = :itemId " +
                        "and end_date < :end " +
                        "order by end_date desc " +
                        "limit 1", Booking.class);
        nativeQuery.setParameter("itemId", 1L);
        nativeQuery.setParameter("end", LocalDateTime.now().plusDays(2));
        Booking result = (Booking) nativeQuery.getSingleResult();
        Assertions.assertEquals(booking1, result);
    }

    @Test
    public void findNext() {
        Query nativeQuery = entityManager.getEntityManager()
                .createNativeQuery("select * from booking " +
                        "left join items i on booking.item_id = i.id " +
                        "where item_id = :itemId " +
                        "and booking.start_date > :start " +
                        "order by end_date limit 1", Booking.class);
        nativeQuery.setParameter("itemId", 2L);
        nativeQuery.setParameter("start", LocalDateTime.now());
        Booking result = (Booking) nativeQuery.getSingleResult();
        Assertions.assertEquals(booking2, result);
    }
}