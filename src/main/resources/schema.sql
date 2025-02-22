-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS sistema_gerenciamento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_gerenciamento;

drop schema sistema_gerenciamento;

-- Criar tabela usuarios
CREATE TABLE IF NOT EXISTS usuarios (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    ultimo_login DATETIME DEFAULT NULL,
    perfil ENUM('ADMIN', 'USUARIO') NOT NULL
    );

-- Criar tabela projetos
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

-- Criação da tabela projetos_usuarios para a relação muitos-para-muitos
CREATE TABLE IF NOT EXISTS projetos_usuarios (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 id_projeto BIGINT NOT NULL,
                                                 id_usuario BIGINT NOT NULL,
                                                 FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
    );

-- Criar tabela atividades
CREATE TABLE IF NOT EXISTS atividades (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          id_projeto BIGINT NOT NULL,
                                          nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NOT NULL,
    status ENUM('ABERTA', 'EM_ANDAMENTO', 'CONCLUIDA', 'PAUSADA') NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_projeto) REFERENCES projetos(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS atividades_usuarios (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   id_atividade BIGINT NOT NULL,
                                                   id_usuario BIGINT NOT NULL,
                                                   FOREIGN KEY (id_atividade) REFERENCES atividades(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
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
