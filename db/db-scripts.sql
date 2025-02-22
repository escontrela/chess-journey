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



