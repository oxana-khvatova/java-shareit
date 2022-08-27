package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    @Query("select b from Booking b left join Item i on b.itemId = i.id where i.owner = ?1")
    List<Booking> findByOwnerId(Long ownerId);

    List<Booking> findByItemId(Long ownerId);
}