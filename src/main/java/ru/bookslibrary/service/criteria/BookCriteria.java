package ru.bookslibrary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.bookslibrary.domain.Book} entity. This class is used
 * in {@link ru.bookslibrary.web.rest.BookResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /books?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter isbn;

    private StringFilter title;

    private StringFilter author;

    private IntegerFilter publishingYear;

    private LongFilter userId;

    private LongFilter libraryId;

    private Boolean distinct;

    public BookCriteria() {}

    public BookCriteria(BookCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.isbn = other.isbn == null ? null : other.isbn.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.author = other.author == null ? null : other.author.copy();
        this.publishingYear = other.publishingYear == null ? null : other.publishingYear.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.libraryId = other.libraryId == null ? null : other.libraryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BookCriteria copy() {
        return new BookCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getIsbn() {
        return isbn;
    }

    public StringFilter isbn() {
        if (isbn == null) {
            isbn = new StringFilter();
        }
        return isbn;
    }

    public void setIsbn(StringFilter isbn) {
        this.isbn = isbn;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getAuthor() {
        return author;
    }

    public StringFilter author() {
        if (author == null) {
            author = new StringFilter();
        }
        return author;
    }

    public void setAuthor(StringFilter author) {
        this.author = author;
    }

    public IntegerFilter getPublishingYear() {
        return publishingYear;
    }

    public IntegerFilter publishingYear() {
        if (publishingYear == null) {
            publishingYear = new IntegerFilter();
        }
        return publishingYear;
    }

    public void setPublishingYear(IntegerFilter publishingYear) {
        this.publishingYear = publishingYear;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getLibraryId() {
        return libraryId;
    }

    public LongFilter libraryId() {
        if (libraryId == null) {
            libraryId = new LongFilter();
        }
        return libraryId;
    }

    public void setLibraryId(LongFilter libraryId) {
        this.libraryId = libraryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookCriteria that = (BookCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isbn, that.isbn) &&
            Objects.equals(title, that.title) &&
            Objects.equals(author, that.author) &&
            Objects.equals(publishingYear, that.publishingYear) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(libraryId, that.libraryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn, title, author, publishingYear, userId, libraryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (isbn != null ? "isbn=" + isbn + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (author != null ? "author=" + author + ", " : "") +
            (publishingYear != null ? "publishingYear=" + publishingYear + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (libraryId != null ? "libraryId=" + libraryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
