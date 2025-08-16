+----------------+          +-------------------+          +-------------------+          +-------------+
| exercise_types |          |     exercises     |          | difficulty_levels |          |    tags     |
+----------------+          +-------------------+          +-------------------+          +-------------+
| id (PK)        |<-------->| id (PK)           |<-------->| id (PK)           |          | id (PK)     |
| name           |          | fen               |          | level_name        |<-------> | name        |
| description    |          | pgn               |          | description       |          | description |
+----------------+          | type_id (FK)      |          +-------------------+          +-------------+
                            | difficulty_id (FK)|
                            +-------------------+

-- tags
CREATE TABLE public.tags (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Insertar valores iniciales
INSERT INTO public.tags (name, description) VALUES
    ('easy', 'Ejercicios de nivel fácil'),
    ('medium', 'Ejercicios de nivel medio'),
    ('hard', 'Ejercicios de nivel difícil');

CREATE TABLE public.users (
   id INT8 NOT NULL DEFAULT unique_rowid(),
   email VARCHAR(255) NOT NULL,
   firstname VARCHAR(50) NOT NULL,
   lastname VARCHAR(50) NOT NULL,
   CONSTRAINT users_pkey PRIMARY KEY (id ASC),
   UNIQUE INDEX users_email_key (email ASC)
 )

 CREATE TABLE public.exercise_types (
     id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     name VARCHAR(50) NOT NULL UNIQUE,
     description TEXT NULL,
     created_at TIMESTAMP DEFAULT NOW()
 );

 -- Insertar tipos de ejercicios por defecto
 INSERT INTO public.exercise_types (name, description) VALUES
     ('memory_game', 'Ejercicio de memoria con posiciones FEN ocultas'),
     ('tactic', 'Ejercicio táctico con movimientos ganadores'),
     ('endgame', 'Ejercicios de finales de partida');

 -- Insertar tipos de ejercicios por defecto
 INSERT INTO public.exercise_types (name, description) VALUES
     ('defend_memory_game', 'Ejercicio de memoria con posiciones FEN ocultas y piezas atacadas');


CREATE TABLE public.difficulty_levels (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NULL
);

-- Insertar niveles de dificultad por defecto
INSERT INTO public.difficulty_levels (level_name, description) VALUES
    ('easy', 'Ejercicios de nivel fácil'),
    ('medium', 'Ejercicios de nivel medio'),
    ('hard', 'Ejercicios de nivel difícil');

CREATE TABLE public.exercises (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    fen VARCHAR(100) NOT NULL, -- Posición inicial en formato FEN
    pgn TEXT, -- Movimientos en PGN (opcional)
    type_id UUID NOT NULL REFERENCES exercise_types(id) ON DELETE CASCADE,
    difficulty_id UUID NOT NULL REFERENCES difficulty_levels(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);


CREATE TABLE public.tags (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Insertar etiquetas por defecto
INSERT INTO public.tags (name, description) VALUES
    ('opening', 'Ejercicios relacionados con la apertura'),
    ('endgame', 'Ejercicios relacionados con los finales de partida'),
    ('puzzle', 'Rompecabezas de ajedrez para resolver');

CREATE TABLE public.exercise_tags (
    exercise_id UUID NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (exercise_id, tag_id)
);

INSERT INTO public.exercise_tags (exercise_id, tag_id)
VALUES
    (
        'EXERCISE_ID',
        (SELECT id FROM tags WHERE name = 'endgame')
    ),
    (
        'EXERCISE_ID',
        (SELECT id FROM tags WHERE name = 'puzzle')
    );

-- Buscar ejercicios por tipo y dificultad
SELECT e.id, e.fen, e.pgn, et.name AS type, dl.level_name AS difficulty
FROM exercises e
JOIN exercise_types et ON e.type_id = et.id
JOIN difficulty_levels dl ON e.difficulty_id = dl.id
WHERE et.name = 'memory_game' AND dl.level_name = 'medium';

-- Buscar ejercicios con una etiqueta específica
SELECT e.id, e.fen, e.pgn, t.name AS tag
FROM exercises e
JOIN exercise_tags et ON e.id = et.exercise_id
JOIN tags t ON et.tag_id = t.id
WHERE t.name = 'endgame';

INSERT INTO public.exercises (fen, type_id, difficulty_id, created_at, updated_at)
VALUES (
    '7k/5np1/8/8/8/8/3Q3P/1K6 w - - 0 1',
    (SELECT id FROM public.exercise_types WHERE name = 'memory_game'),
    (SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),
    NOW(),
    NOW()
);

INSERT INTO public.exercises (fen, type_id, difficulty_id, created_at, updated_at)
VALUES (
    '1k2q3/1pp5/8/8/8/8/PPP5/1KR1R3 w - - 0 1',
    (SELECT id FROM public.exercise_types WHERE name = 'memory_game'),
    (SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),
    NOW(),
    NOW()
);

INSERT INTO public.exercises (fen, type_id, difficulty_id, created_at, updated_at)
VALUES (
    '3r4/6k1/5p2/8/8/2B5/8/4K3 w - - 0 1',
    (SELECT id FROM public.exercise_types WHERE name = 'memory_game'),
    (SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),
    NOW(),
    NOW()
);

INSERT INTO public.exercises (fen, type_id, difficulty_id, created_at, updated_at)
VALUES (
    '3k4/8/2r5/8/8/3KR3/8/8 w - - 0 1',
    (SELECT id FROM public.exercise_types WHERE name = 'memory_game'),
    (SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),
    NOW(),
    NOW()
);

INSERT INTO public.exercises (fen, type_id, difficulty_id, created_at, updated_at)
VALUES (
    '8/8/1k6/p7/4P3/3K4/8/8 w - - 0 1',
    (SELECT id FROM public.exercise_types WHERE name = 'memory_game'),
    (SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),
    NOW(),
    NOW()
);


INSERT INTO public.exercises (fen, type_id, difficulty_id, created_at, updated_at)
VALUES (
    '3r2k1/pp3pp1/2p4p/8/P2r4/1P3PP1/4R1BP/4R1K1 b - - 1 27',
    (SELECT id FROM public.exercise_types WHERE name = 'memory_game'),
    (SELECT id FROM public.difficulty_levels WHERE level_name = 'medium'),
    NOW(),
    NOW()
);


INSERT INTO public.exercises (fen, type_id, difficulty_id, created_at, updated_at)
VALUES (
    'r2q1rk1/1b1nppb1/p2p2p1/1pp3Nn/3PP2P/2N1BP2/PPPQ4/2KR1B1R w - - 0 13',
    (SELECT id FROM public.exercise_types WHERE name = 'memory_game'),
    (SELECT id FROM public.difficulty_levels WHERE level_name = 'medium'),
    NOW(),
    NOW()
);

insert into public.exercises (fen,pgn,type_id,difficulty_id,created_at,updated_at)
values('7b/1k1r3P/8/8/8/1B6/K7/2B5 w - - 1 1','Bc2',
(SELECT id FROM public.exercise_types WHERE name = 'defend_memory_game'),
(SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),now(),now());

insert into public.exercises (fen,pgn,type_id,difficulty_id,created_at,updated_at)
values('4rk2/2p2ppp/B2p4/8/6b1/P3P3/1PP5/1KR5 w - - 0 1','Re1',
(SELECT id FROM public.exercise_types WHERE name = 'defend_memory_game'),
(SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),now(),now());

insert into public.exercises (fen,pgn,type_id,difficulty_id,created_at,updated_at)
values('r1bqkb1r/ppnp1ppp/4pn2/1B6/2NP4/1PP1PN2/3B1PPP/R2QK2R w KQkq - 0 1','Ba4',
(SELECT id FROM public.exercise_types WHERE name = 'defend_memory_game'),
(SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),now(),now());

-- tactic_game
insert into public.exercises (fen,pgn,type_id,difficulty_id,created_at,updated_at)
values ('8/8/4k3/3r4/8/3N4/4K3/8 w - - 0 1','1. Nf4+ Ke5 2. Nxd5',
(SELECT id FROM public.exercise_types WHERE name = 'tactic_game'),
(SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),now(),now());

insert into public.exercises (fen,pgn,type_id,difficulty_id,created_at,updated_at)
values ('8/R5K1/8/3k4/8/8/3r4/8 w - - 0 2','1. Rd7+ Ke6 2. Rxd2',
(SELECT id FROM public.exercise_types WHERE name = 'tactic_game'),
(SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),now(),now());

insert into public.exercises (fen,pgn,type_id,difficulty_id,created_at,updated_at)
values ('R7/8/8/4k2p/8/8/8/3K4 w - - 0 1','1. Ra5+ Kf4 2. Rxh5',
(SELECT id FROM public.exercise_types WHERE name = 'tactic_game'),
(SELECT id FROM public.difficulty_levels WHERE level_name = 'easy'),now(),now());


+---------------+          +-------------------+          +-------------------+
|    users      |          |    exercises      |          | difficulty_levels |
+---------------+          +-------------------+          +-------------------+
| id (PK)       |<-------->| id (PK)           |<-------->| id (PK)           |
| email         |          | fen               |          | level_name        |
+---------------+          | pgn               |          | description       |
                           +-------------------+          +-------------------+

                            ^
                            |
+----------------------+
| user_exercise_stats  |
+----------------------+
| id (PK)              |
| user_id (FK)         |
| exercise_id (FK)     |
| attempt_date         |
| successful           |
| time_taken_seconds   |
| attempts             |
| difficulty_id (FK)   |
+----------------------+

+------------+
| user_elo   |
+------------+
| id (PK)    |
| user_id    |
| current_elo|
| last_updated|
+------------+


CREATE TABLE user_exercise_stats (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id INT8 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_id UUID NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    attempt_date TIMESTAMP DEFAULT NOW(),  -- Fecha y hora del intento
    successful BOOLEAN NOT NULL,           -- Indica si el intento fue exitoso
    time_taken_seconds INT NOT NULL,      -- Tiempo en segundos que tardó en resolverlo
    attempts INT DEFAULT 1,               -- Número de intentos hasta resolverlo
    difficulty_id UUID NOT NULL REFERENCES difficulty_levels(id) ON DELETE CASCADE  -- Dificultad del ejercicio
);

CREATE TABLE elo_types (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE,  -- Ejemplos: 'standard', 'rapid', 'blitz', 'exercises'
    description TEXT
);

-- Insertar los tipos de ELO por defecto
INSERT INTO elo_types (type_name, description) VALUES
    ('standard', 'ELO para partidas clásicas'),
    ('rapid', 'ELO para partidas rápidas'),
    ('blitz', 'ELO para partidas blitz'),
    ('exercises', 'ELO específico para ejercicios de entrenamiento');


CREATE TABLE user_elo (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id INT8 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    elo_type_id UUID NOT NULL REFERENCES elo_types(id) ON DELETE CASCADE,  -- Relación con tipo de ELO
    current_elo INT DEFAULT 1200,  -- Valor inicial estándar de ELO
    last_updated TIMESTAMP DEFAULT NOW(),
    UNIQUE (user_id, elo_type_id)  -- Un ELO por tipo para cada usuario
);

CREATE TABLE user_exercise_stats (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id INT8 NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_id UUID NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    attempt_date TIMESTAMP DEFAULT NOW(),  -- Fecha y hora del intento
    successful BOOLEAN NOT NULL,           -- Indica si el intento fue exitoso
    time_taken_seconds INT NOT NULL,      -- Tiempo en segundos que tardó en resolverlo
    attempts INT DEFAULT 1,               -- Número de intentos hasta resolverlo
    difficulty_id UUID NOT NULL REFERENCES difficulty_levels(id) ON DELETE CASCADE  -- Dificultad del ejercicio
);

CREATE TABLE IF NOT EXISTS quotes (
    id SERIAL PRIMARY KEY,
    text VARCHAR(500) NOT NULL,
    author VARCHAR(100) NOT NULL
);

-- Insertar algunas quotes iniciales
INSERT INTO quotes (text, author) VALUES
('Some people think that if their opponent plays a beautiful game, it''s okay to lose. I don''t. You have to be merciless.', 'Magnus Carlsen'),
('Chess is life in miniature. Chess is struggle, chess is battles.', 'Garry Kasparov'),
('Every chess master was once a beginner.', 'Irving Chernev'),
('Chess is the gymnasium of the mind.', 'Blaise Pascal'),
('When you see a good move, look for a better one.', 'Emanuel Lasker');


