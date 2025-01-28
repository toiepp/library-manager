package ru.bookslibrary.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookslibrary.domain.*; // for static metamodels
import ru.bookslibrary.domain.Library;
import ru.bookslibrary.repository.LibraryRepository;
import ru.bookslibrary.repository.search.LibrarySearchRepository;
import ru.bookslibrary.service.criteria.LibraryCriteria;
import ru.bookslibrary.service.dto.LibraryDTO;
import ru.bookslibrary.service.mapper.LibraryMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Library} entities in the database.
 * The main input is a {@link LibraryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LibraryDTO} or a {@link Page} of {@link LibraryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LibraryQueryService extends QueryService<Library> {

    private final Logger log = LoggerFactory.getLogger(LibraryQueryService.class);

    private final LibraryRepository libraryRepository;

    private final LibraryMapper libraryMapper;

    private final LibrarySearchRepository librarySearchRepository;

    public LibraryQueryService(
        LibraryRepository libraryRepository,
        LibraryMapper libraryMapper,
        LibrarySearchRepository librarySearchRepository
    ) {
        this.libraryRepository = libraryRepository;
        this.libraryMapper = libraryMapper;
        this.librarySearchRepository = librarySearchRepository;
    }

    /**
     * Return a {@link List} of {@link LibraryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LibraryDTO> findByCriteria(LibraryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Library> specification = createSpecification(criteria);
        return libraryMapper.toDto(libraryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LibraryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryDTO> findByCriteria(LibraryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Library> specification = createSpecification(criteria);
        return libraryRepository.findAll(specification, page).map(libraryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LibraryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Library> specification = createSpecification(criteria);
        return libraryRepository.count(specification);
    }

    /**
     * Function to convert {@link LibraryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Library> createSpecification(LibraryCriteria criteria) {
        Specification<Library> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Library_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Library_.name));
            }
            if (criteria.getPostalAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPostalAddress(), Library_.postalAddress));
            }
        }
        return specification;
    }
}
