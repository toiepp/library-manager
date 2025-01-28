package ru.bookslibrary.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link ru.bookslibrary.domain.Book} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String isbn;

    @NotNull
    @Size(min = 1, max = 1000)
    private String title;

    private String author;

    private Integer publishingYear;

    private UserDTO user;

    private LibraryDTO library;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublishingYear() {
        return publishingYear;
    }

    public void setPublishingYear(Integer publishingYear) {
        this.publishingYear = publishingYear;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public LibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(LibraryDTO library) {
        this.library = library;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookDTO)) {
            return false;
        }

        BookDTO bookDTO = (BookDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookDTO{" +
            "id=" + getId() +
            ", isbn='" + getIsbn() + "'" +
            ", title='" + getTitle() + "'" +
            ", author='" + getAuthor() + "'" +
            ", publishingYear=" + getPublishingYear() +
            ", user=" + getUser() +
            ", library=" + getLibrary() +
            "}";
    }
}
