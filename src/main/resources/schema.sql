-- DROP TABLE IF EXISTS "relative";
-- DROP TABLE IF EXISTS citizen;
-- DROP TABLE IF EXISTS import;

CREATE TABLE IF NOT EXISTS import (
    id serial PRIMARY KEY,
    visible boolean
);

CREATE TABLE IF NOT EXISTS citizen (
    id serial PRIMARY KEY,
    import_id integer,
    citizen_id integer,
    town varchar(256) NOT NULL CHECK(char_length(town) >= 1),
    street varchar(256) NOT NULL CHECK(char_length(town) >= 1),
    building varchar(256) NOT NULL CHECK(char_length(town) >= 1),
    apartment integer NOT NULL CHECK(apartment >= 0),
    "name" varchar(256) NOT NULL CHECK(char_length("name") >= 0),
    birth_date date NOT NULL,
    "gender" varchar(10) NOT NULL CHECK("gender" = 'MALE' OR "gender" = 'FEMALE'),
    UNIQUE(import_id, citizen_id),
    FOREIGN KEY(import_id) REFERENCES import(id)
);

CREATE TABLE IF NOT EXISTS "relative" (
    id serial PRIMARY KEY,
    import_id integer,
    citizen_id integer,
    relative_id integer,
    UNIQUE(import_id, citizen_id, relative_id),
    FOREIGN KEY(import_id, citizen_id) REFERENCES citizen(import_id, citizen_id),
    FOREIGN KEY(import_id, relative_id) REFERENCES citizen(import_id, citizen_id)
)