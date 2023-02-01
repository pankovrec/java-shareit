package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * ItemRequestDto.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotNull
    @NotEmpty
    private String description;
    private LocalDateTime created;
    private Set<ItemDto> items;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private long id;
        private String name;
        private String description;
        private Boolean available;
        private long requestId;
    }
}
