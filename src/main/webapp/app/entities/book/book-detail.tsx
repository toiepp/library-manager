import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const BookDetail = () => {
  const dispatch = useAppDispatch();
  const isGuest = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.GUEST]));

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookEntity = useAppSelector(state => state.book.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookDetailsHeading">
          <Translate contentKey="booksLibraryApp.book.detail.title">Book</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{bookEntity.id}</dd>
          <dt>
            <span id="isbn">
              <Translate contentKey="booksLibraryApp.book.isbn">Isbn</Translate>
            </span>
          </dt>
          <dd>{bookEntity.isbn}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="booksLibraryApp.book.title">Title</Translate>
            </span>
          </dt>
          <dd>{bookEntity.title}</dd>
          <dt>
            <span id="author">
              <Translate contentKey="booksLibraryApp.book.author">Author</Translate>
            </span>
          </dt>
          <dd>{bookEntity.author}</dd>
          <dt>
            <span id="publishingYear">
              <Translate contentKey="booksLibraryApp.book.publishingYear">Publishing Year</Translate>
            </span>
          </dt>
          <dd>{bookEntity.publishingYear}</dd>
          <dt>
            <Translate contentKey="booksLibraryApp.book.user">User</Translate>
          </dt>
          <dd>{bookEntity.user ? bookEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="booksLibraryApp.book.library">Library</Translate>
          </dt>
          <dd>{bookEntity.library ? bookEntity.library.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/book" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        {!isGuest && (
          <Button tag={Link} to={`/book/${bookEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        )}
      </Col>
    </Row>
  );
};

export default BookDetail;
