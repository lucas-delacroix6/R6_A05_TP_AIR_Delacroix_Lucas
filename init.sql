-- Users table
CREATE TABLE IF NOT EXISTS users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    role       VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    version    BIGINT       NOT NULL DEFAULT 0
);

-- Annonces table
CREATE TABLE IF NOT EXISTS annonces (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2),
    category    VARCHAR(100),
    status      VARCHAR(20)    NOT NULL DEFAULT 'DRAFT',
    author_id   BIGINT         NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP,
    version     BIGINT         NOT NULL DEFAULT 0
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_annonces_author ON annonces(author_id);
CREATE INDEX IF NOT EXISTS idx_annonces_status ON annonces(status);
CREATE INDEX IF NOT EXISTS idx_annonces_created ON annonces(created_at DESC);

INSERT INTO users (username, password, email, role)
VALUES
    ('admin', 'admin', 'admin@masterannonce.com', 'ROLE_ADMIN'),
    ('john',  'password', 'john@example.com', 'ROLE_USER'),
    ('jane',  'password', 'jane@example.com', 'ROLE_USER')
    ON CONFLICT DO NOTHING;

INSERT INTO annonces (title, description, price, category, status, author_id)
SELECT 'Vélo de course Trek', 'Vélo de course en excellent état, 21 vitesses', 450.00, 'Sport', 'PUBLISHED', u.id
FROM users u WHERE u.username = 'john'
    ON CONFLICT DO NOTHING;

INSERT INTO annonces (title, description, price, category, status, author_id)
SELECT 'iPhone 13 Pro', 'iPhone 13 Pro 256Go, avec facture', 750.00, 'Electronics', 'DRAFT', u.id
FROM users u WHERE u.username = 'john'
    ON CONFLICT DO NOTHING;

INSERT INTO annonces (title, description, price, category, status, author_id)
SELECT 'Canapé 3 places', 'Canapé en tissu gris, peu utilisé', 200.00, 'Furniture', 'ARCHIVED', u.id
FROM users u WHERE u.username = 'jane'
    ON CONFLICT DO NOTHING;