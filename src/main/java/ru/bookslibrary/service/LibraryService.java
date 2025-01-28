package ru.bookslibrary.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookslibrary.domain.Book;
import ru.bookslibrary.domain.Library;
import ru.bookslibrary.repository.LibraryRepository;
import ru.bookslibrary.repository.search.LibrarySearchRepository;
import ru.bookslibrary.service.dto.LibraryDTO;
import ru.bookslibrary.service.mapper.LibraryMapper;

/**
 * Service Implementation for managing {@link Library}.
 */
@Service
@Transactional
public class LibraryService {

    private final Logger log = LoggerFactory.getLogger(LibraryService.class);

    private final LibraryRepository libraryRepository;

    private final LibraryMapper libraryMapper;

    private final LibrarySearchRepository librarySearchRepository;

    public LibraryService(
        LibraryRepository libraryRepository,
        LibraryMapper libraryMapper,
        LibrarySearchRepository librarySearchRepository
    ) {
        this.libraryRepository = libraryRepository;
        this.libraryMapper = libraryMapper;
        this.librarySearchRepository = librarySearchRepository;
    }

    /**
     * Save a library.
     *
     * @param libraryDTO the entity to save.
     * @return the persisted entity.
     */
    public LibraryDTO save(LibraryDTO libraryDTO) {
        log.debug("Request to save Library : {}", libraryDTO);
        Library library = libraryMapper.toEntity(libraryDTO);
        library = libraryRepository.save(library);
        LibraryDTO result = libraryMapper.toDto(library);
        librarySearchRepository.index(library);
        return result;
    }

    /**
     * Update a library.
     *
     * @param libraryDTO the entity to save.
     * @return the persisted entity.
     */
    public LibraryDTO update(LibraryDTO libraryDTO) {
        log.debug("Request to update Library : {}", libraryDTO);
        Library library = libraryMapper.toEntity(libraryDTO);
        library = libraryRepository.save(library);
        LibraryDTO result = libraryMapper.toDto(library);
        librarySearchRepository.index(library);
        return result;
    }

    /**
     * Partially update a library.
     *
     * @param libraryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LibraryDTO> partialUpdate(LibraryDTO libraryDTO) {
        log.debug("Request to partially update Library : {}", libraryDTO);

        return libraryRepository
            .findById(libraryDTO.getId())
            .map(existingLibrary -> {
                libraryMapper.partialUpdate(existingLibrary, libraryDTO);

                return existingLibrary;
            })
            .map(libraryRepository::save)
            .map(savedLibrary -> {
                librarySearchRepository.save(savedLibrary);

                return savedLibrary;
            })
            .map(libraryMapper::toDto);
    }

    /**
     * Get all the libraries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Libraries");
        return libraryRepository.findAll(pageable).map(libraryMapper::toDto);
    }

    /**
     * Get one library by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LibraryDTO> findOne(Long id) {
        log.debug("Request to get Library : {}", id);
        return libraryRepository.findById(id).map(libraryMapper::toDto);
    }

    /**
     * Delete the library by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Library : {}", id);
        libraryRepository.deleteById(id);
        librarySearchRepository.deleteById(id);
    }

    /**
     * Search for the library corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Libraries for query {}", query);
        return librarySearchRepository.search(query, pageable).map(libraryMapper::toDto);
    }

    public void reindexAll() {
        librarySearchRepository.deleteAll();
        List<Library> libraries = libraryRepository.findAll();
        for (Library library : libraries) {
            librarySearchRepository.save(library);
        }
    }
}
