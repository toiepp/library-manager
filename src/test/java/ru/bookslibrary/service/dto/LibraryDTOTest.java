package ru.bookslibrary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.bookslibrary.web.rest.TestUtil;

class LibraryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LibraryDTO.class);
        LibraryDTO libraryDTO1 = new LibraryDTO();
        libraryDTO1.setId(1L);
        LibraryDTO libraryDTO2 = new LibraryDTO();
        assertThat(libraryDTO1).isNotEqualTo(libraryDTO2);
        libraryDTO2.setId(libraryDTO1.getId());
        assertThat(libraryDTO1).isEqualTo(libraryDTO2);
        libraryDTO2.setId(2L);
        assertThat(libraryDTO1).isNotEqualTo(libraryDTO2);
        libraryDTO1.setId(null);
        assertThat(libraryDTO1).isNotEqualTo(libraryDTO2);
    }
}
