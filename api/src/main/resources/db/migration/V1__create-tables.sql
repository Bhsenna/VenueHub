CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,

    cep CHAR(8) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero INTEGER NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,

    latitude DECIMAL(9, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL
);

CREATE TABLE venues (
    id SERIAL PRIMARY KEY,

    nome VARCHAR(255) NOT NULL,
    capacidade INTEGER NOT NULL,
    descricao TEXT,
    telefone VARCHAR(20),
    valor DECIMAL(10, 2) NOT NULL,

    address_id INTEGER NOT NULL REFERENCES addresses(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,

    nome VARCHAR(255) NOT NULL,
    sobrenome VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,

    role VARCHAR(25) NOT NULL,
    address_id INTEGER NOT NULL REFERENCES addresses(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,

    tipo_evento VARCHAR(50) NOT NULL,
    qt_pessoas INTEGER NOT NULL,
    dataInicio DATE NOT NULL,
    dataFim DATE NOT NULL,
    horaInicio TIME NOT NULL,
    horaFim TIME NOT NULL,

    user_id INTEGER NOT NULL REFERENCES users(id)
);

CREATE TABLE additionals (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE event_additionals (
    event_id INTEGER NOT NULL REFERENCES events(id),
    additional_id INTEGER NOT NULL REFERENCES additionals(id),
    PRIMARY KEY (event_id, additional_id)
);

CREATE TABLE venue_additionals (
    venue_id INTEGER NOT NULL REFERENCES venues(id),
    additional_id INTEGER NOT NULL REFERENCES additionals(id),
    valor DECIMAL(9, 2) NOT NULL,
    PRIMARY KEY (venue_id, additional_id)
);

CREATE TABLE proposals (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL REFERENCES events(id),
    venue_id INTEGER NOT NULL REFERENCES venues(id),
    valor DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);



