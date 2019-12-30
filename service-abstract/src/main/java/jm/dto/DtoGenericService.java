package jm.dto;

import java.util.List;
import java.util.stream.Collectors;

public interface DtoGenericService<ENTITY, DTO> {

    DTO toDto(ENTITY entity);

    default List<DTO> toDto(List<ENTITY> entities) {
        return entities == null ? null : entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    ENTITY toEntity(DTO dto);

}
