-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS sistema_gerenciamento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_gerenciamento;

-- Criar tabela usuarios (com a nova coluna 'ativo')
CREATE TABLE IF NOT EXISTS usuarios (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    ultimo_login DATETIME DEFAULT NULL,
    perfil ENUM('ADMIN', 'USUARIO') NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
    );

-- Criar tabela projetos
CREATE TABLE IF NOT EXISTS projetos (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME,
    status ENUM('PLANEJADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO') NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    prioridade ENUM('ALTA', 'MEDIA', 'BAIXA') NOT NULL
    );

-- Criar tabela projetos_usuarios (relação muitos-para-muitos)
CREATE TABLE IF NOT EXISTS projetos_usuarios (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 id_projeto BIGINT NOT NULL,
                                                 id_usuario BIGINT NOT NULL,
                                                 FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
    );

-- Criar tabela atividades (atualizada com id_usuario_responsavel)
CREATE TABLE IF NOT EXISTS atividades (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          id_projeto BIGINT NOT NULL,
                                          nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    status ENUM('ABERTA', 'EM_ANDAMENTO', 'CONCLUIDA', 'PAUSADA') NOT NULL,
    id_usuario_responsavel BIGINT NULL, -- Novo campo para armazenar o responsável principal
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_responsavel) REFERENCES usuarios(id) ON DELETE SET NULL
    );

-- Criar tabela atividades_usuarios (relação muitos-para-muitos entre atividades e usuários)
CREATE TABLE IF NOT EXISTS atividades_usuarios (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   id_atividade BIGINT NOT NULL,
                                                   id_usuario BIGINT NOT NULL,
                                                   FOREIGN KEY (id_atividade) REFERENCES atividades(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
    );

-- Criar tabela lancamentos_horas (registro de horas trabalhadas em atividades, agora com a coluna 'cancelado')
CREATE TABLE IF NOT EXISTS lancamentos_horas (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 id_atividade BIGINT NOT NULL,
                                                 id_usuario BIGINT NOT NULL,
                                                 descricao TEXT NOT NULL,
                                                 data_inicio DATETIME NOT NULL,
                                                 data_fim DATETIME NOT NULL,
                                                 data_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                 cancelado BOOLEAN DEFAULT FALSE, -- Coluna adicionada para controle de cancelamento
                                                 FOREIGN KEY (id_atividade) REFERENCES atividades(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
    );






