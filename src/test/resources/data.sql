INSERT INTO users (email, login, name, birthday)
VALUES
    ('john@example.com', 'john_doe', 'John Doe', '1988-07-22'),
    ('mike@example.com', 'mike', 'Mike Smith', '1990-02-01'),
    ('marie@example.com', 'marie', 'Marie Smith', '1990-05-02');

INSERT INTO friendship (inviter_id, acceptor_id, is_confirmed)
VALUES
    (1, 2, FALSE),
    (1, 3, TRUE),
    (2, 3, TRUE);

INSERT INTO films (name, description, release_date, duration, mpa)
VALUES
    ('Matrix', 'Best film ever', '1999-10-14', 136, 'R'),
    ('The Batman', 'Is it a bird? Is it a plane? No, this is Batman!', '2022-03-04', 130, 'R'),
    ('Rain Man', 'Must see', '1988-12-16', 134, 'R');

INSERT INTO likes (user_id, film_id, created_at)
VALUES
    (1, 2, '2021-03-08T01:00:01.000000800'),
    (2, 2, '2022-05-11T01:00:01.000000800');

INSERT INTO film_genre (film_id, genre_id)
VALUES
    (2, 1),
    (1, 2);

INSERT INTO events (user_id, entity_id, event_type, operation, occurred_on)
VALUES
    (1, 2, 'LIKE', 'ADD', '2021-03-08T01:00:01.000000800'),
    (2, 2, 'FRIEND', 'ADD', '2022-05-11T01:00:01.000000800');