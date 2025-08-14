-- Вставка данных в таблицу mpa (рейтинги)
INSERT INTO mpa (name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

-- Вставка данных в таблицу genres (жанры)
INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');

-- Вставка данных в таблицу user (пользователи)
INSERT INTO "user" (email, login, name, birthday) VALUES
('user1@example.com', 'user1', 'Иван Иванов', '1990-05-15'),
('user2@example.com', 'user2', 'Петр Петров', '1985-08-21'),
('user3@example.com', 'user3', 'Мария Сидорова', '1995-03-10'),
('user4@example.com', 'user4', 'Анна Кузнецова', '2000-11-30'),
('user5@example.com', 'user5', 'Сергей Смирнов', '1980-07-04');

-- Вставка данных в таблицу film (фильмы)
INSERT INTO film (name, description, releaseDate, duration, mpa_id) VALUES
('Фильм 1', 'Описание фильма 1', '2000-01-01', 120, 1),
('Фильм 2', 'Описание фильма 2', '2005-05-15', 90, 2),
('Фильм 3', 'Описание фильма 3', '2010-10-20', 150, 3),
('Фильм 4', 'Описание фильма 4', '2015-12-25', 110, 4),
('Фильм 5', 'Описание фильма 5', '2020-07-07', 180, 5);

-- Вставка данных в таблицу film_genres (жанры фильмов)
INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1), (1, 2),
(2, 3), (2, 4),
(3, 5), (3, 6),
(4, 1), (4, 3),
(5, 2), (5, 4);

-- Вставка данных в таблицу user_friendship (дружба пользователей)
INSERT INTO user_friendship (user_id, friend_id, confirmed) VALUES
(1, 2, true),
(1, 3, false),
(2, 3, true),
(3, 4, true),
(4, 5, false),
(5, 1, true);

-- Вставка данных в таблицу film_likes (лайки фильмов)
INSERT INTO film_likes (film_id, user_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 2), (2, 4),
(3, 1), (3, 5),
(4, 3), (4, 4),
(5, 2), (5, 3), (5, 5);