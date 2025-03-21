package ru.bookslibrary.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.bookslibrary.domain.Library;
import ru.bookslibrary.repository.LibraryRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Library} entity.
 */
public interface LibrarySearchRepository extends ElasticsearchRepository<Library, Long>, LibrarySearchRepositoryInternal {}

interface LibrarySearchRepositoryInternal {
    Page<Library> search(String query, Pageable pageable);

    Page<Library> search(Query query);

    void index(Library entity);
}

class LibrarySearchRepositoryInternalImpl implements LibrarySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final LibraryRepository repository;

    LibrarySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, LibraryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Library> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Library> search(Query query) {
        SearchHits<Library> searchHits = elasticsearchTemplate.search(query, Library.class);
        List<Library> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Library entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
