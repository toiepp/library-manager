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
import ru.bookslibrary.domain.Book;
import ru.bookslibrary.repository.BookRepository;
import ru.bookslibrary.repository.search.BookSearchRepository;
import ru.bookslibrary.service.criteria.BookCriteria;
import ru.bookslibrary.service.dto.BookDTO;
import ru.bookslibrary.service.mapper.BookMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Book} entities in the database.
 * The main input is a {@link BookCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BookDTO} or a {@link Page} of {@link BookDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookQueryService extends QueryService<Book> {

    private final Logger log = LoggerFactory.getLogger(BookQueryService.class);

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final BookSearchRepository bookSearchRepository;

    public BookQueryService(BookRepository bookRepository, BookMapper bookMapper, BookSearchRepository bookSearchRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookSearchRepository = bookSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BookDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BookDTO> findByCriteria(BookCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Book> specification = createSpecification(criteria);
        return bookMapper.toDto(bookRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BookDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> findByCriteria(BookCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, page).map(bookMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Book> specification = createSpecification(criteria);
        return bookRepository.count(specification);
    }

    /**
     * Function to convert {@link BookCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Book> createSpecification(BookCriteria criteria) {
        Specification<Book> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Book_.id));
            }
            if (criteria.getIsbn() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIsbn(), Book_.isbn));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Book_.title));
            }
            if (criteria.getAuthor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAuthor(), Book_.author));
            }
            if (criteria.getPublishingYear() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPublishingYear(), Book_.publishingYear));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getUserId(), root -> root.join(Book_.user, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getLibraryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getLibraryId(), root -> root.join(Book_.library, JoinType.LEFT).get(Library_.id))
                    );
            }
        }
        return specification;
    }
}
