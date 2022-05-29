INSERT INTO `user` (email, login, name, birthday)
VALUES
    ('john@example.com', 'john_doe', 'John Doe', '1988-07-22'),
    ('mike@example.com', 'mike', 'Mike Smith', '1990-02-01'),
    ('marie@example.com', 'marie', 'Marie Smith', '1990-05-02');

INSERT INTO friendship (inviter_id, acceptor_id, is_confirmed)
VALUES
    (1, 2, FALSE),
    (1, 3, TRUE),
    (2, 3, TRUE);

INSERT INTO film (name, description, release_date, duration, mpaa)
VALUES
    ('Matrix', 'Best film ever', '1999-10-14', 136, 'R'),
    ('The Batman', 'Is it a bird? Is it a plane? No, this is Batman!', '2022-03-04', 130, 'R');

INSERT INTO `like` (user_id, film_id, created_at)
VALUES
    (1, 2, '2021-03-08T01:00:01.000000800'),
    (2, 2, '2022-05-11T01:00:01.000000800');