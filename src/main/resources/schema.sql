CREATE TABLE IF NOT EXISTS `user` (
    user_id BIGSERIAL PRIMARY KEY NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
    film_id BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpaa CHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL REFERENCES film (film_id),
    genre_id BIGINT NOT NULL REFERENCES genre (genre_id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    inviter_id BIGINT NOT NULL REFERENCES `user` (user_id),
    acceptor_id BIGINT NOT NULL REFERENCES `user` (user_id),
    is_confirmed BOOLEAN NOT NULL,
    min_id BIGINT AS LEAST(inviter_id, acceptor_id),
    max_id BIGINT AS GREATEST(inviter_id, acceptor_id),
    CONSTRAINT uq1 UNIQUE (min_id, max_id),
    PRIMARY KEY (inviter_id, acceptor_id)
);

CREATE TABLE IF NOT EXISTS `like` (
    user_id BIGINT NOT NULL REFERENCES `user` (user_id),
    film_id BIGINT NOT NULL REFERENCES film (film_id),
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, film_id)
);