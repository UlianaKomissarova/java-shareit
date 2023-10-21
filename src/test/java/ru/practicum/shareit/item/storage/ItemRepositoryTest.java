package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    private ItemRepository itemRepository;
    private TestEntityManager entityManager;
    private User user;
    private Item foundItem;
    private Item incorrectItem;

    @Autowired
    public ItemRepositoryTest(ItemRepository itemRepository, TestEntityManager entityManager) {
        this.itemRepository = itemRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void beforeEach() {
        user = User.builder()
            //.id(1L)
            .name("user")
            .email("test@mail.ru")
            .build();

        foundItem = Item.builder()
            .name("tool")
            .description("cool tool")
            .available(true)
            .owner(1L)
            .build();

        incorrectItem = Item.builder()
            .name("egg")
            .description("so-so")
            .available(true)
            .owner(1L)
            .build();
    }

    @Test
    void search() {
        this.entityManager.persist(user);
        this.entityManager.persist(foundItem);
        this.entityManager.persist(incorrectItem);
        List<Item> actual = itemRepository.search("tool", PageRequest.of(0, 10));

        assertEquals(1, actual.size());
        assertEquals("tool", actual.get(0).getName());
    }
}