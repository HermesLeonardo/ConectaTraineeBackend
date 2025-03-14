-- Inserir Projeto: Treinamento dos Trainee 2025
INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, prioridade, id_usuario_responsavel)
VALUES
    ('Treinamento dos Trainee 2025',
     'Projeto destinado ao treinamento dos novos trainees de 2025, abordando áreas de desenvolvimento de software e integração com a equipe.',
     '2025-01-01', '2025-12-31', 'EM_ANDAMENTO', 'ALTA',
     (SELECT id FROM usuarios WHERE email = 'admin@wise.com' LIMIT 1));

-- Inserir Projeto: Desenvolvimento de Software - Empresa XYZ
INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, prioridade, id_usuario_responsavel)
VALUES
    ('Desenvolvimento de Software - Empresa XYZ',
     'Projeto relacionado ao desenvolvimento de soluções de software para a empresa XYZ, com foco na melhoria de sistemas internos.',
     '2025-02-01', '2025-12-31', 'PLANEJADO', 'MÉDIA',
     (SELECT id FROM usuarios WHERE email = 'admin@wise.com' LIMIT 1));


-- Inserir Atividade 1: Tutoria (Projeto: Treinamento dos Trainee 2025)
INSERT INTO atividades (id_projeto, nome, descricao, data_inicio, data_fim, status, id_usuario_responsavel)
VALUES
    ((SELECT id FROM projetos WHERE nome = 'Treinamento dos Trainee 2025' LIMIT 1),
    'Tutoria',
    'Sessões de tutoria para os trainees de 2025, abordando temas técnicos e soft skills.',
    '2025-01-05', '2025-01-10', 'ABERTA',
    (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1));

-- Inserir Atividade 2: Entrevista (Projeto: Treinamento dos Trainee 2025)
INSERT INTO atividades (id_projeto, nome, descricao, data_inicio, data_fim, status, id_usuario_responsavel)
VALUES
    ((SELECT id FROM projetos WHERE nome = 'Treinamento dos Trainee 2025' LIMIT 1),
    'Entrevista',
    'Realização de entrevistas para seleção de novos trainees e profissionais para o time de desenvolvimento.',
    '2025-01-12', '2025-01-15', 'ABERTA',
    (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1));


-- Inserir Atividade 1: Planejamento de Software (Projeto: Desenvolvimento de Software - Empresa XYZ)
INSERT INTO atividades (id_projeto, nome, descricao, data_inicio, data_fim, status, id_usuario_responsavel)
VALUES
    ((SELECT id FROM projetos WHERE nome = 'Desenvolvimento de Software - Empresa XYZ' LIMIT 1),
    'Planejamento de Software',
    'Planejamento das fases do desenvolvimento do novo software para a empresa XYZ.',
    '2025-02-05', '2025-02-10', 'ABERTA',
    (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1));

-- Inserir Atividade 2: Desenvolvimento de Funcionalidades (Projeto: Desenvolvimento de Software - Empresa XYZ)
INSERT INTO atividades (id_projeto, nome, descricao, data_inicio, data_fim, status, id_usuario_responsavel)
VALUES
    ((SELECT id FROM projetos WHERE nome = 'Desenvolvimento de Software - Empresa XYZ' LIMIT 1),
    'Desenvolvimento de Funcionalidades',
    'Desenvolvimento de funcionalidades essenciais para o sistema de gestão da empresa XYZ.',
    '2025-02-15', '2025-02-20', 'ABERTA',
    (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1));


-- Inserir Lançamento 1: Tutoria (Projeto: Treinamento dos Trainee 2025)
INSERT INTO lancamentos_horas (id_atividade, id_usuario, descricao, data_inicio, data_fim)
VALUES
    ((SELECT id FROM atividades WHERE nome = 'Tutoria' AND id_projeto = (SELECT id FROM projetos WHERE nome = 'Treinamento dos Trainee 2025' LIMIT 1) LIMIT 1),
 (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1),
    'Sessão de tutoria 1', '2025-01-05 09:00', '2025-01-05 12:00');

-- Inserir Lançamento 2: Tutoria (Projeto: Treinamento dos Trainee 2025)
INSERT INTO lancamentos_horas (id_atividade, id_usuario, descricao, data_inicio, data_fim)
VALUES
    ((SELECT id FROM atividades WHERE nome = 'Tutoria' AND id_projeto = (SELECT id FROM projetos WHERE nome = 'Treinamento dos Trainee 2025' LIMIT 1) LIMIT 1),
 (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1),
    'Sessão de tutoria 2', '2025-01-06 09:00', '2025-01-06 12:00');


-- Inserir Lançamento 1: Entrevista (Projeto: Treinamento dos Trainee 2025)
INSERT INTO lancamentos_horas (id_atividade, id_usuario, descricao, data_inicio, data_fim)
VALUES
    ((SELECT id FROM atividades WHERE nome = 'Entrevista' AND id_projeto = (SELECT id FROM projetos WHERE nome = 'Treinamento dos Trainee 2025' LIMIT 1) LIMIT 1),
 (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1),
    'Entrevista 1', '2025-01-12 14:00', '2025-01-12 16:00');

-- Inserir Lançamento 2: Entrevista (Projeto: Treinamento dos Trainee 2025)
INSERT INTO lancamentos_horas (id_atividade, id_usuario, descricao, data_inicio, data_fim)
VALUES
    ((SELECT id FROM atividades WHERE nome = 'Entrevista' AND id_projeto = (SELECT id FROM projetos WHERE nome = 'Treinamento dos Trainee 2025' LIMIT 1) LIMIT 1),
 (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1),
    'Entrevista 2', '2025-01-13 14:00', '2025-01-13 16:00');


-- Inserir Lançamento 1: Planejamento de Software (Projeto: Desenvolvimento de Software - Empresa XYZ)
INSERT INTO lancamentos_horas (id_atividade, id_usuario, descricao, data_inicio, data_fim)
VALUES
    ((SELECT id FROM atividades WHERE nome = 'Planejamento de Software' AND id_projeto = (SELECT id FROM projetos WHERE nome = 'Desenvolvimento de Software - Empresa XYZ' LIMIT 1) LIMIT 1),
 (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1),
    'Planejamento inicial das funcionalidades', '2025-02-05 10:00', '2025-02-05 13:00');

-- Inserir Lançamento 2: Planejamento de Software (Projeto: Desenvolvimento de Software - Empresa XYZ)
INSERT INTO lancamentos_horas (id_atividade, id_usuario, descricao, data_inicio, data_fim)
VALUES
    ((SELECT id FROM atividades WHERE nome = 'Planejamento de Software' AND id_projeto = (SELECT id FROM projetos WHERE nome = 'Desenvolvimento de Software - Empresa XYZ' LIMIT 1) LIMIT 1),
 (SELECT id FROM usuarios WHERE email = 'rodrigo.quisen@wise.com' LIMIT 1),
    'Planejamento de integração com sistemas externos', '2025-02-06 10:00', '2025-02-06 13:00');
