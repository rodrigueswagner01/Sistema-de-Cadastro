-- ============================================================
-- Sistema de Cadastro - Etapa 6 (UC15 / Projeto Integrador)
-- Script do banco de dados (MySQL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS SistemadeCadastro;
USE SistemadeCadastro;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    perfil VARCHAR(50) NOT NULL,      -- Administrador | Técnico | Operador
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    status VARCHAR(50) NOT NULL       -- Disponível | Vendido
);

-- Dados iniciais (valores já padronizados com os enums PerfilUsuario e StatusProduto)
INSERT INTO usuarios (nome, email, perfil, login, senha) VALUES
('Administrador Geral', 'admin@email.com',    'Administrador', 'admin',    '123'),
('Carlos Técnico',      'tecnico@email.com',  'Técnico',       'tecnico',  '123'),
('Ana Operadora',       'operador@email.com', 'Operador',      'operador', '123');

INSERT INTO Produtos (nome, descricao, quantidade, status) VALUES
('Celular',          'nokia',   5, 'Disponível'),
('Cafeteira',        'philco',  3, 'Disponível'),
('Máquina de serra', 'makita',  2, 'Disponível');

-- ------------------------------------------------------------
-- OPCIONAL: se você já tem o banco da Etapa 5 populado, use estes
-- comandos para padronizar os dados antigos (o sistema também aceita
-- as variações antigas, mas é melhor manter o banco consistente):
-- ------------------------------------------------------------
-- UPDATE usuarios SET perfil = 'Técnico' WHERE perfil IN ('Tecnico', 'Técnico de Laboratório');
-- UPDATE Produtos SET status = 'Disponível' WHERE status = 'Disponivel';

SELECT * FROM usuarios;
SELECT * FROM Produtos;
