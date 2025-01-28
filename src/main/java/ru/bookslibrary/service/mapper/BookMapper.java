package ru.bookslibrary.service.mapper;

import org.mapstruct.*;
import ru.bookslibrary.domain.Book;
import ru.bookslibrary.domain.Library;
import ru.bookslibrary.domain.User;
import ru.bookslibrary.service.dto.BookDTO;
import ru.bookslibrary.service.dto.LibraryDTO;
import ru.bookslibrary.service.dto.UserDTO;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "library", source = "library", qualifiedByName = "libraryName")
    BookDTO toDto(Book s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("libraryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LibraryDTO toDtoLibraryName(Library library);
}
