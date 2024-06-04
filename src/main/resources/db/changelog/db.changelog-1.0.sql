--liquibase formatted sql
--changeset adagridjan:1.create-table-d_user_roles stripComments:false splitStatements:false runAlways:false runOnChange:true failOnError:true
CREATE TABLE IF NOT EXISTS d_user_roles
(
    id                smallint PRIMARY KEY NOT NULL,
    name              text                 NOT NULL UNIQUE,
    description       text,
    short_description text
);

DO
$$
    BEGIN
        -- ALTER TABLE d_user_roles ADD IF NOT EXISTS short_description text;
    END;
$$;

COMMENT ON TABLE d_user_roles IS 'Роли пользователя';
COMMENT ON COLUMN d_user_roles.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN d_user_roles.name IS 'Уникальное название роли';
COMMENT ON COLUMN d_user_roles.description IS 'Описание роли';
COMMENT ON COLUMN d_user_roles.short_description IS 'Сокращенное наименование';

DO
$$
    BEGIN
        INSERT INTO d_user_roles (id, name, description, short_description)
        VALUES (0, 'platform_admin', 'Администратор Платформы', 'Администратор Платформы'),
               (1, 'workplace_admin', 'Администратор рабочего места', 'Админ РМ'),
               (2, 'operator', 'Оператор', 'Оператор')
        ON CONFLICT (id) DO UPDATE SET name              = EXCLUDED.name,
                                       description       = EXCLUDED.description,
                                       short_description = EXCLUDED.short_description;
    END;
$$;


--changeset adagridjan:1.create-table-users stripComments:false splitStatements:false runAlways:false runOnChange:true failOnError:true
CREATE TABLE IF NOT EXISTS users
(
    id           integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    created_at   timestamp with time zone DEFAULT now()               NOT NULL,
    updated_at   timestamp with time zone,
    user_role_id smallint REFERENCES d_user_roles                     NOT NULL,
    login        text UNIQUE                                          NOT NULL,
    password     text                                                 NOT NULL,
    name         text
);

COMMENT ON TABLE users IS 'Таблица для хранения пользователей';
COMMENT ON COLUMN users.id IS 'Первичный ключ. Уникальный идентификатор записи';
COMMENT ON COLUMN users.created_at IS 'Дата создания пользователя. Не изменяется после создания';
COMMENT ON COLUMN users.updated_at IS 'Дата последнего изменения. Если не было изменений то null';
COMMENT ON COLUMN users.user_role_id IS 'Роль пользователя. Ссылка на справочник ролей пользователей';
COMMENT ON COLUMN users.login IS 'Уникальное значение идентификатора пользователя';
COMMENT ON COLUMN users.password IS 'Зашифрованный пароль пользователя';
COMMENT ON COLUMN users.name IS 'ФИО пользователя';
