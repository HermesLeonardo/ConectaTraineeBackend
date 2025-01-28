INSERT INTO usuarios (nome, email, senha, data_criacao, perfil) 
VALUES ('Administrador', 'admin@empresa.com', 'senhabembolada', NOW(), 'ADMIN');


INSERT INTO projetos (nome, descricao, data_inicio, data_fim, status, id_usuario_responsavel, prioridade)
VALUES ('Projeto Exemplo', 'Este é um projeto de teste', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'PLANEJADO', 1, 'ALTA');
