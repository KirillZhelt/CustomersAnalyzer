CREATE TABLE IF NOT EXISTS imports (
    id serial PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS citizens (
    import_id integer,
    citizen_id integer,
    town varchar(256) NOT NULL CHECK(char_length(town) >= 1),
    street varchar(256) NOT NULL CHECK(char_length(town) >= 1),
    building varchar(256) NOT NULL CHECK(char_length(town) >= 1),
    apartment integer NOT NULL CHECK(apartment >= 0),
    "name" varchar(256) NOT NULL CHECK(char_length("name") >= 0),
    birth_date date NOT NULL,
    "gender" gender NOT NULL,
    PRIMARY KEY(import_id, citizen_id)
);

CREATE TABLE IF NOT EXISTS relatives (
    import_id integer,
    citizen_id integer,
    relative_id integer,
    PRIMARY KEY(import_id, citizen_id, relative_id),
    FOREIGN KEY(import_id, citizen_id) REFERENCES citizens(import_id, citizen_id),
    FOREIGN KEY(import_id, relative_id) REFERENCES citizens(import_id, citizen_id)
)