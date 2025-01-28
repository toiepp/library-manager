import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const EntitiesMenu = () => {
  const isGuest = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.GUEST]));
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/book">
        <Translate contentKey="global.menu.entities.book" />
      </MenuItem>
      {!isGuest && (
        <MenuItem icon="asterisk" to="/library">
          <Translate contentKey="global.menu.entities.library" />
        </MenuItem>
      )}
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
