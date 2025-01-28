package ru.bookslibrary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryMapperTest {

    private LibraryMapper libraryMapper;

    @BeforeEach
    public void setUp() {
        libraryMapper = new LibraryMapperImpl();
    }
}
