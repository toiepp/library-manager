import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Library from './library';
import LibraryDetail from './library-detail';
import LibraryUpdate from './library-update';
import LibraryDeleteDialog from './library-delete-dialog';

const LibraryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Library />} />
    <Route path="new" element={<LibraryUpdate />} />
    <Route path=":id">
      <Route index element={<LibraryDetail />} />
      <Route path="edit" element={<LibraryUpdate />} />
      <Route path="delete" element={<LibraryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LibraryRoutes;
