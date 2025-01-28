package ru.bookslibrary.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link ru.bookslibrary.domain.Library} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LibraryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Size(min = 10, max = 500)
    private String postalAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LibraryDTO)) {
            return false;
        }

        LibraryDTO libraryDTO = (LibraryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, libraryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", postalAddress='" + getPostalAddress() + "'" +
            "}";
    }
}
