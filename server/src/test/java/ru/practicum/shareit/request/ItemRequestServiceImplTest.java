package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl requestService;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    User requester;
    User owner;
    ItemRequest request;
    Item response;

    @BeforeEach
    void setUp() {
        requester = new User(null, "Requester", "requester@mail.com");
        owner = new User(null, "Owner", "owner@mail.com");

        request = new ItemRequest(
                null,
                "Need a drill",
                requester,
                LocalDateTime.now().minusDays(1)
        );

        response = new Item(
                null,
                "Drill",
                "Powerful drill",
                true,
                owner,
                request
        );
    }

    @Test
    void createRequestShouldBeCreatedTest() {
        requester = userRepository.save(requester);

        ItemRequestDto dto = ItemRequestDto.builder()
                .description("New Request")
                .build();

        ItemRequestDto created = requestService.create(requester.getId(), dto);

        Optional<ItemRequest> fromDb = requestRepository.findById(created.getId());

        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getDescription()).isEqualTo(dto.getDescription());
        assertThat(fromDb.get().getRequester().getId()).isEqualTo(requester.getId());
    }

    @Test
    void createThrowIfUserNotFoundTest() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Need a chair")
                .build();

        assertThatThrownBy(() -> requestService.create(999L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getByUserShouldReturnRequestsWithItemsTest() {
        requester = userRepository.save(requester);
        owner = userRepository.save(owner);

        request = requestRepository.save(request);
        response = itemRepository.save(response);

        Collection<ItemRequestDto> result = requestService.getByUser(requester.getId());

        assertThat(result).isNotEmpty();
        ItemRequestDto dto = result.iterator().next();

        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getItems()).isNotEmpty();
        assertThat(dto.getItems().getFirst().getId()).isEqualTo(response.getId());
    }

    @Test
    void getByUserShouldReturnEmptyListTest() {
        requester = userRepository.save(requester);

        Collection<ItemRequestDto> result = requestService.getByUser(requester.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getByUserThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> requestService.getByUser(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllShouldReturnOtherUsersRequestsTest() {
        requester = userRepository.save(requester);
        owner = userRepository.save(owner);

        request = requestRepository.save(request);

        Collection<ItemRequestDto> result = requestService.getAll(owner.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.iterator().next().getId()).isEqualTo(request.getId());
    }

    @Test
    void getAllShouldReturnEmptyListTest() {
        requester = userRepository.save(requester);

        Collection<ItemRequestDto> result = requestService.getAll(requester.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> requestService.getAll(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdShouldReturnRequestWithItemsTest() {
        requester = userRepository.save(requester);
        owner = userRepository.save(owner);

        request = requestRepository.save(request);
        response = itemRepository.save(response);

        ItemRequestDto dto = requestService.getById(requester.getId(), request.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().getFirst().getId()).isEqualTo(response.getId());
    }

    @Test
    void getByIdShouldReturnRequestWithoutItemsTest() {
        requester = userRepository.save(requester);

        request = requestRepository.save(request);

        ItemRequestDto dto = requestService.getById(requester.getId(), request.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void getByIdThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> requestService.getById(999L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByIdThrowIfRequestNotFoundTest() {
        requester = userRepository.save(requester);

        assertThatThrownBy(() -> requestService.getById(requester.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }
}