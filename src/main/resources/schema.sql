-- Criação da tabela usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(45) NOT NULL,
    email VARCHAR(45) UNIQUE NOT NULL,
    senha VARCHAR(45) NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_login DATETIME DEFAULT NULL,
    perfil VARCHAR(45) NOT NULL
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
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    prioridade ENUM('ALTA', 'MEDIA', 'BAIXA') NOT NULL
);
INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, prioridade)
VALUES ('Projeto Exemplo',
        'Este é um projeto de teste',
        NOW(),
        DATE_ADD(NOW(), INTERVAL 30 DAY),
        'PLANEJADO',
        'ALTA');


-- Criação da tabela projetos_usuarios para a relação muitos-para-muitos
CREATE TABLE IF NOT EXISTS projetos_usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_projeto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);
INSERT INTO projetos_usuarios (id_projeto, id_usuario)
VALUES (1, 1);

-- Criação da tabela atividades
CREATE TABLE IF NOT EXISTS atividades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_projeto BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    status ENUM('ABERTA', 'EM_ANDAMENTO', 'CONCLUIDA', 'PAUSADA') NOT NULL,
    id_usuario_responsavel BIGINT, -- Removido NOT NULL
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


-- Criação da tabela lancamentos_horas
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