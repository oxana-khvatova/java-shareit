package ru.practicum.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select ir from ItemRequest ir left join User u on ir.userRequesterId = u.id where ir.userRequesterId = ?1")
    List<ItemRequest> findByOwnerId(Long ownerId);
}
