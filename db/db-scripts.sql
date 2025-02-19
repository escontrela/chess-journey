CREATE TABLE public.tags (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Insertar valores iniciales
INSERT INTO tags (name, description) VALUES
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