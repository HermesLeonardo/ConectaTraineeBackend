
-- Criação das tabelas Usuario
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_login DATETIME DEFAULT NULL,
    perfil VARCHAR(50) NOT NULL
);

INSERT INTO usuarios (nome, email, senha, data_criacao, perfil) 
VALUES ('Administrador',
        CONCAT(SUBSTRING(MD5(RAND()), 1, 10), '@teste.com'),
        'senhabembolada',
         NOW(),
        'ADMIN');