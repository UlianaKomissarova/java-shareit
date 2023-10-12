package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i " +
        "where upper(i.name) like upper(concat('%', ?1, '%')) " +
        " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByOwner(Long userId, Pageable pageable);
}