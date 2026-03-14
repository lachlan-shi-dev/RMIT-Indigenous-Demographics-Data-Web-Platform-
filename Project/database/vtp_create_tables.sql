-- Create tables for Semester 1 2023 CTG example ER Model
PRAGMA foreign_keys = OFF;
drop table if exists LGA;
drop table if exists Population;
PRAGMA foreign_keys = ON;

CREATE TABLE LGA (
    code              INTEGER NOT NULL,
    year              INTEGER NOT NULL,
    name                 TEXT NOT NULL,
    state_abbr               CHAR (10),
    area                        DOUBLE,
    latitude                    DOUBLE,
    longitude                   DOUBLE,
    PRIMARY KEY (code, year)
);

CREATE TABLE Population (
    lga_code           INTEGER NOT NULL,
    lga_year           INTEGER NOT NULL,
    indigenous_status     TEXT NOT NULL,
    sex               CHAR (1) NOT NULL,
    age_category          TEXT NOT NULL,
    count              INTEGER NOT NULL,
    age_min                     INTEGER,
    age_max                     INTEGER,
    PRIMARY KEY (lga_code, lga_year, indigenous_status, sex, age_category)
    FOREIGN KEY (lga_code) REFERENCES LGA(lga_code)
    FOREIGN KEY (lga_year) REFERENCES LGA(lga_year)
);

CREATE TABLE HighestSchoolYear (
    lga_code          INTEGER NOT NULL,
    lga_year          INTEGER NOT NULL,
    indigenous_status    TEXT NOT NULL,
    sex              CHAR (1) NOT NULL,
    category             TEXT NOT NULL,
    count             INTEGER NOT NULL,
    PRIMARY KEY (lga_code, lga_year, indigenous_status, sex, category_year)
    FOREIGN KEY (lga_code) REFERENCES LGA(lga_code)
    FOREIGN KEY (lga_year) REFERENCES LGA(lga_year)
);

CREATE TABLE NonSchoolCompletion (
    lga_code          INTEGER  NOT NULL,
    lga_year          INTEGER  NOT NULL,
    indigenous_status TEXT     NOT NULL,
    sex               CHAR (1) NOT NULL,
    category          TEXT     NOT NULL,
    count             INTEGER  NOT NULL,
    PRIMARY KEY (lga_code, lga_year, indigenous_status, sex, category)
    FOREIGN KEY (lga_code) REFERENCES LGA(lga_code)
    FOREIGN KEY (lga_year) REFERENCES LGA(lga_year)
);

CREATE TABLE Persona (
    persona_id      INTEGER NOT NULL,
    name               TEXT NOT NULL,
    image_file_path    TEXT NOT NULL,
    PRIMARY KEY (persona_id),
    FOREIGN KEY (persona_id) REFERENCES Persona_Attributes(persona_id)
);

CREATE TABLE Persona_Attributes (
    persona_id INTEGER NOT NULL,
    description   TEXT NOT NULL,
    needs         TEXT NOT NULL,
    goals         TEXT NOT NULL,
    skills        TEXT NOT NULL,
    PRIMARY KEY (persona_id)
);

CREATE TABLE Student (
    student_id    TEXT NOT NULL,
    student_name  TEXT NOT NULL,
    student_email TEXT NOT NULL,
    PRIMARY KEY (student_id)
);

CREATE TABLE LTHC (
    lga_code       INTEGER NOT NULL,
    lga_year       INTEGER NOT NULL,
    indigenous_status TEXT NOT NULL,
    sex           CHAR (1) NOT NULL,
    condition         TEXT NOT NULL,
    count          INTEGER NOT NULL,
    PRIMARY KEY (lga_code, lga_year, indigenous_status, sex, condition),
    FOREIGN KEY (lga_code) REFERENCES LGA(lga_code),
    FOREIGN KEY (lga_year) REFERENCES LGA(lga_year)
);

CREATE TABLE IndigenousDemographics (
    lga_code       INTEGER NOT NULL,
    lga_year       INTEGER NOT NULL,
    indigenous_status TEXT NOT NULL,
    sex CHAR(1)            NOT NULL,
    age_category      TEXT NOT NULL,
    count          INTEGER NOT NULL,
    PRIMARY KEY (lga_code, lga_year, indigenous_status, sex, age_category),
    FOREIGN KEY (lga_code) REFERENCES LGA(lga_code),
    FOREIGN KEY (lga_year) REFERENCES LGA(lga_year)
);