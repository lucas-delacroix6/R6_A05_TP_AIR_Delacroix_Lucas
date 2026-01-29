CREATE TABLE annonce (
     id SERIAL PRIMARY KEY,
     title VARCHAR(64) NOT NULL,
     description VARCHAR(256),
     address VARCHAR(64),
     mail VARCHAR(64),
     date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);