package ru.bookslibrary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.bookslibrary.IntegrationTest;
import ru.bookslibrary.domain.Library;
import ru.bookslibrary.repository.LibraryRepository;
import ru.bookslibrary.repository.search.LibrarySearchRepository;
import ru.bookslibrary.service.criteria.LibraryCriteria;
import ru.bookslibrary.service.dto.LibraryDTO;
import ru.bookslibrary.service.mapper.LibraryMapper;

/**
 * Integration tests for the {@link LibraryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LibraryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/libraries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/libraries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private LibraryMapper libraryMapper;

    @Autowired
    private LibrarySearchRepository librarySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLibraryMockMvc;

    private Library library;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Library createEntity(EntityManager em) {
        Library library = new Library().name(DEFAULT_NAME).postalAddress(DEFAULT_POSTAL_ADDRESS);
        return library;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Library createUpdatedEntity(EntityManager em) {
        Library library = new Library().name(UPDATED_NAME).postalAddress(UPDATED_POSTAL_ADDRESS);
        return library;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        librarySearchRepository.deleteAll();
        assertThat(librarySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        library = createEntity(em);
    }

    @Test
    @Transactional
    void createLibrary() throws Exception {
        int databaseSizeBeforeCreate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);
        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryDTO)))
            .andExpect(status().isCreated());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLibrary.getPostalAddress()).isEqualTo(DEFAULT_POSTAL_ADDRESS);
    }

    @Test
    @Transactional
    void createLibraryWithExistingId() throws Exception {
        // Create the Library with an existing ID
        library.setId(1L);
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        int databaseSizeBeforeCreate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        // set the field null
        library.setName(null);

        // Create the Library, which fails.
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryDTO)))
            .andExpect(status().isBadRequest());

        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPostalAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        // set the field null
        library.setPostalAddress(null);

        // Create the Library, which fails.
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryDTO)))
            .andExpect(status().isBadRequest());

        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllLibraries() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(library.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].postalAddress").value(hasItem(DEFAULT_POSTAL_ADDRESS)));
    }

    @Test
    @Transactional
    void getLibrary() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get the library
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL_ID, library.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(library.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.postalAddress").value(DEFAULT_POSTAL_ADDRESS));
    }

    @Test
    @Transactional
    void getLibrariesByIdFiltering() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        Long id = library.getId();

        defaultLibraryShouldBeFound("id.equals=" + id);
        defaultLibraryShouldNotBeFound("id.notEquals=" + id);

        defaultLibraryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLibraryShouldNotBeFound("id.greaterThan=" + id);

        defaultLibraryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLibraryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLibrariesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where name equals to DEFAULT_NAME
        defaultLibraryShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the libraryList where name equals to UPDATED_NAME
        defaultLibraryShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLibrariesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultLibraryShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the libraryList where name equals to UPDATED_NAME
        defaultLibraryShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLibrariesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where name is not null
        defaultLibraryShouldBeFound("name.specified=true");

        // Get all the libraryList where name is null
        defaultLibraryShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByNameContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where name contains DEFAULT_NAME
        defaultLibraryShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the libraryList where name contains UPDATED_NAME
        defaultLibraryShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLibrariesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where name does not contain DEFAULT_NAME
        defaultLibraryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the libraryList where name does not contain UPDATED_NAME
        defaultLibraryShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLibrariesByPostalAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where postalAddress equals to DEFAULT_POSTAL_ADDRESS
        defaultLibraryShouldBeFound("postalAddress.equals=" + DEFAULT_POSTAL_ADDRESS);

        // Get all the libraryList where postalAddress equals to UPDATED_POSTAL_ADDRESS
        defaultLibraryShouldNotBeFound("postalAddress.equals=" + UPDATED_POSTAL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLibrariesByPostalAddressIsInShouldWork() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where postalAddress in DEFAULT_POSTAL_ADDRESS or UPDATED_POSTAL_ADDRESS
        defaultLibraryShouldBeFound("postalAddress.in=" + DEFAULT_POSTAL_ADDRESS + "," + UPDATED_POSTAL_ADDRESS);

        // Get all the libraryList where postalAddress equals to UPDATED_POSTAL_ADDRESS
        defaultLibraryShouldNotBeFound("postalAddress.in=" + UPDATED_POSTAL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLibrariesByPostalAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where postalAddress is not null
        defaultLibraryShouldBeFound("postalAddress.specified=true");

        // Get all the libraryList where postalAddress is null
        defaultLibraryShouldNotBeFound("postalAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllLibrariesByPostalAddressContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where postalAddress contains DEFAULT_POSTAL_ADDRESS
        defaultLibraryShouldBeFound("postalAddress.contains=" + DEFAULT_POSTAL_ADDRESS);

        // Get all the libraryList where postalAddress contains UPDATED_POSTAL_ADDRESS
        defaultLibraryShouldNotBeFound("postalAddress.contains=" + UPDATED_POSTAL_ADDRESS);
    }

    @Test
    @Transactional
    void getAllLibrariesByPostalAddressNotContainsSomething() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        // Get all the libraryList where postalAddress does not contain DEFAULT_POSTAL_ADDRESS
        defaultLibraryShouldNotBeFound("postalAddress.doesNotContain=" + DEFAULT_POSTAL_ADDRESS);

        // Get all the libraryList where postalAddress does not contain UPDATED_POSTAL_ADDRESS
        defaultLibraryShouldBeFound("postalAddress.doesNotContain=" + UPDATED_POSTAL_ADDRESS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLibraryShouldBeFound(String filter) throws Exception {
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(library.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].postalAddress").value(hasItem(DEFAULT_POSTAL_ADDRESS)));

        // Check, that the count call also returns 1
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLibraryShouldNotBeFound(String filter) throws Exception {
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLibrary() throws Exception {
        // Get the library
        restLibraryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLibrary() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        librarySearchRepository.save(library);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());

        // Update the library
        Library updatedLibrary = libraryRepository.findById(library.getId()).get();
        // Disconnect from session so that the updates on updatedLibrary are not directly saved in db
        em.detach(updatedLibrary);
        updatedLibrary.name(UPDATED_NAME).postalAddress(UPDATED_POSTAL_ADDRESS);
        LibraryDTO libraryDTO = libraryMapper.toDto(updatedLibrary);

        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(libraryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLibrary.getPostalAddress()).isEqualTo(UPDATED_POSTAL_ADDRESS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Library> librarySearchList = IterableUtils.toList(librarySearchRepository.findAll());
                Library testLibrarySearch = librarySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testLibrarySearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testLibrarySearch.getPostalAddress()).isEqualTo(UPDATED_POSTAL_ADDRESS);
            });
    }

    @Test
    @Transactional
    void putNonExistingLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        library.setId(count.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        library.setId(count.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        library.setId(count.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(libraryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateLibraryWithPatch() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();

        // Update the library using partial update
        Library partialUpdatedLibrary = new Library();
        partialUpdatedLibrary.setId(library.getId());

        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibrary))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLibrary.getPostalAddress()).isEqualTo(DEFAULT_POSTAL_ADDRESS);
    }

    @Test
    @Transactional
    void fullUpdateLibraryWithPatch() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);

        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();

        // Update the library using partial update
        Library partialUpdatedLibrary = new Library();
        partialUpdatedLibrary.setId(library.getId());

        partialUpdatedLibrary.name(UPDATED_NAME).postalAddress(UPDATED_POSTAL_ADDRESS);

        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLibrary))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        Library testLibrary = libraryList.get(libraryList.size() - 1);
        assertThat(testLibrary.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLibrary.getPostalAddress()).isEqualTo(UPDATED_POSTAL_ADDRESS);
    }

    @Test
    @Transactional
    void patchNonExistingLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        library.setId(count.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, libraryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        library.setId(count.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLibrary() throws Exception {
        int databaseSizeBeforeUpdate = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        library.setId(count.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(libraryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Library in the database
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteLibrary() throws Exception {
        // Initialize the database
        libraryRepository.saveAndFlush(library);
        libraryRepository.save(library);
        librarySearchRepository.save(library);

        int databaseSizeBeforeDelete = libraryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the library
        restLibraryMockMvc
            .perform(delete(ENTITY_API_URL_ID, library.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Library> libraryList = libraryRepository.findAll();
        assertThat(libraryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(librarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchLibrary() throws Exception {
        // Initialize the database
        library = libraryRepository.saveAndFlush(library);
        librarySearchRepository.save(library);

        // Search the library
        restLibraryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + library.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(library.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].postalAddress").value(hasItem(DEFAULT_POSTAL_ADDRESS)));
    }
}
