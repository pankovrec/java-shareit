package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BookingRepository
 */

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.status = ?2 order by b.start desc")
    List<Booking> findAllStatusByItemsOwnerId(long userId, Status status, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBefore(long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndIsBefore(long bookerId, LocalDateTime end, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentBooking(long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findBookingByOwnerAndStatus(long bookerId, State state, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findBookingByOwnerPast(long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findBookingByOwnerFuture(long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join b.item i " +
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findBookingByOwnerCurrent(long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "left join b.item i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingByOwner(long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.item.id = ?1")
    List<Booking> findAllByItemsId(long itemId);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and b.start <= ?3 order by b.end desc ")
    Booking findLastItemBooking(long itemId, long userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and b.start >= ?3 order by b.start asc")
    Booking findNextItemBooking(long itemId, long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime now, Pageable pageable);
}
