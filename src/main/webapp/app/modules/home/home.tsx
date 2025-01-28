import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Row, Col, Alert } from 'reactstrap';

import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isEmployee = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.EMPLOYEE]));
  const roleNames = {
    ROLE_GUEST: 'Читатель',
    ROLE_EMPLOYEE: 'Библиотекарь',
    ROLE_ADMIN: 'Админ',
  };

  return (
    <Row>
      <Col md="3" className="pad">
        <span className="hipster rounded" />
      </Col>
      <Col md="9">
        <h2>
          <Translate contentKey="home.title">Welcome, Java Hipster!</Translate>
        </h2>
        <p className="lead">
          <Translate contentKey="home.subtitle">This is your homepage</Translate>
        </p>
        {account?.login ? (
          <div>
            <Alert color="success">
              <Translate contentKey="home.logged.message" interpolate={{ username: account.login }}>
                You are logged in as user <b>&quot;{account.login}&quot;</b>
              </Translate>
              <br />
              Ваша роль: <b>{account.authorities.map(role => roleNames[role]).join(', ')}</b>
            </Alert>
          </div>
        ) : (
          <div>
            <Alert color="warning">
              <Translate contentKey="global.messages.info.authenticated.prefix">If you want to </Translate>

              <Link to="/login" className="alert-link">
                <Translate contentKey="global.messages.info.authenticated.link"> sign in</Translate>
              </Link>
              <Translate contentKey="global.messages.info.authenticated.suffix">
                , you can try the default accounts:
                <br />- Administrator (login=&quot;admin&quot; and password=&quot;admin&quot;)
                <br />- User (login=&quot;user&quot; and password=&quot;user&quot;).
              </Translate>
            </Alert>

            <Alert color="warning">
              <Translate contentKey="global.messages.info.register.noaccount">You do not have an account yet?</Translate>&nbsp;
              <Link to="/account/register" className="alert-link">
                <Translate contentKey="global.messages.info.register.link">Register a new account</Translate>
              </Link>
            </Alert>
          </div>
        )}
        {isEmployee && (
          <p>
            <Link to="/account/register-active-guest">Зарегистрировать нового пользователя (с ролью GUEST)</Link>
          </p>
        )}
      </Col>
    </Row>
  );
};

export default Home;
