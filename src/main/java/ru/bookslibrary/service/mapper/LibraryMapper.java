package ru.bookslibrary.service.mapper;

import org.mapstruct.*;
import ru.bookslibrary.domain.Library;
import ru.bookslibrary.service.dto.LibraryDTO;

/**
 * Mapper for the entity {@link Library} and its DTO {@link LibraryDTO}.
 */
@Mapper(componentModel = "spring")
public interface LibraryMapper extends EntityMapper<LibraryDTO, Library> {}
