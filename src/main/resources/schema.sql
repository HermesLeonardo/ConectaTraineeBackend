
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
VALUES ('Projeto Exemplo',
        'Este é um projeto de teste',
        NOW(),
        DATE_ADD(NOW(), INTERVAL 30 DAY),
        'PLANEJADO',
        1,
        'ALTA');



-- Criar tabela atividades
CREATE TABLE IF NOT EXISTS atividades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_projeto BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    status ENUM('ABERTA', 'EM_ANDAMENTO', 'CONCLUIDA', 'PAUSADA') NOT NULL,
    id_usuario_responsavel BIGINT NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_responsavel) REFERENCES usuarios(id) ON DELETE SET NULL
);

INSERT INTO atividades (id_projeto, nome, descricao, data_inicio, data_fim, status, id_usuario_responsavel, data_criacao)
VALUES (
    1, -- ID do projeto existente
    'Atividade Exemplo',
    'Esta é uma atividade de teste dentro de um projeto.',
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    'EM_ANDAMENTO',
    1, -- ID de um usuário existente
    NOW()
);



-- Criar tabela lancamentos_horas
CREATE TABLE IF NOT EXISTS lancamentos_horas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_atividade BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    descricao TEXT NOT NULL,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    data_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_atividade) REFERENCES atividades(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO lancamentos_horas (id_atividade, id_usuario, descricao, data_inicio, data_fim, data_registro)
VALUES (
    1, -- ID de uma atividade existente
    1, -- ID de um usuário existente
    'Trabalho realizado na atividade de teste.',
    NOW(),
    DATE_ADD(NOW(), INTERVAL 3 HOUR),
    NOW()
);
