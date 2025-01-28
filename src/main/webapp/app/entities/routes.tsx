import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Book from './book';
import Library from './library';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  const isGuest = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.GUEST]));
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="book/*" element={<Book />} />
        {!isGuest && <Route path="library/*" element={<Library />} />}
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
