package ru.bookslibrary.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.bookslibrary.domain.Library;

/**
 * Spring Data JPA repository for the Library entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LibraryRepository extends JpaRepository<Library, Long>, JpaSpecificationExecutor<Library> {}
