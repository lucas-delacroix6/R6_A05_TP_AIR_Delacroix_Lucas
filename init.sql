CREATE TABLE category (
  id SERIAL PRIMARY KEY,
  label VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE users (
   id SERIAL PRIMARY KEY,
   username VARCHAR(255) UNIQUE NOT NULL,
   email VARCHAR(255) UNIQUE NOT NULL,
   password VARCHAR(255) NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE annonce (
     id SERIAL PRIMARY KEY,
     title VARCHAR(64) NOT NULL,
     description VARCHAR(256),
     address VARCHAR(64),
     mail VARCHAR(64),
     status VARCHAR(20) DEFAULT 'DRAFT',
     date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

     user_id INTEGER REFERENCES users(id),
     category_id INTEGER REFERENCES category(id)
);

INSERT INTO category (label) VALUES ('Immobilier'), ('Véhicules'), ('Multimédia');
INSERT INTO users (username, email, password) VALUES ('jdoe', 'john@test.com', 'pass123');

INSERT INTO annonce (title, description, address, mail, status, user_id, category_id)
VALUES ('Vends MacBook Pro', 'Très bon état, peu servi.', 'Paris', 'john@test.com', 'PUBLISHED', 1, 3);