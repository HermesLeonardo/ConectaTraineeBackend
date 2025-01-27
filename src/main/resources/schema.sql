
-- Criação das tabelas Usuario
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(45) NOT NULL, --texto, obrigatório.texto, obrigatório.
    email VARCHAR(45) UNIQUE NOT NULL, --texto, único, obrigatório.
    senha VARCHAR(45) NOT NULL, --texto (armazenada como hash), obrigatório.
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, --data e hora em que o registro foi criado.
    ultimo_login DATETIME DEFAULT NULL, --data e hora do último acesso.
    perfil VARCHAR(45) NOT NULL --texto (ex.: "ADMIN", "USUARIO").
);

INSERT INTO usuarios (nome, email, senha, data_criacao, perfil) 
VALUES ('Administrador',
        CONCAT(SUBSTRING(MD5(RAND()), 1, 10), '@teste.com'),
        'senhabembolada',
         NOW(),
        'ADMIN');

-- Criação da tabela projetos
CREATE TABLE IF NOT EXISTS projetos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    status ENUM('PLANEJADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO') NOT NULL,
    id_usuario_responsavel BIGINT NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    prioridade ENUM('ALTA', 'MEDIA', 'BAIXA') NOT NULL,
    FOREIGN KEY (id_usuario_responsavel) REFERENCES usuarios(id) ON DELETE CASCADE
    );

INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, id_usuario_responsavel, prioridade)
VALUES ('Projeto Exemplo', 'Este é um projeto de teste', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'PLANEJADO', 1, 'ALTA');
