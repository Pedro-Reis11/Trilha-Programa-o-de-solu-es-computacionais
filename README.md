# Sistema de Gestão de Projetos - Oracle

Sistema completo de gerenciamento de projetos desenvolvido em Java com Swing e MySQL.

## 📋 Requisitos

- Java JDK 8 ou superior
- MySQL 5.7 ou superior
- MySQL Connector/J (JDBC Driver)
- IDE Java (Eclipse, IntelliJ IDEA, NetBeans, etc.)

## 🚀 Configuração do Projeto

### 1. Configuração do Banco de Dados

Execute o script SQL fornecido no arquivo para criar o banco de dados e as tabelas:

```sql
CREATE DATABASE IF NOT EXISTS gestao_projetos;
-- Execute todo o script SQL fornecido na Parte 1
```

### 2. Configuração do JDBC

Baixe o MySQL Connector/J em: https://dev.mysql.com/downloads/connector/j/

Adicione o JAR ao classpath do projeto:
- **Eclipse**: Botão direito no projeto → Build Path → Add External Archives
- **IntelliJ**: File → Project Structure → Libraries → Add
- **NetBeans**: Botão direito no projeto → Properties → Libraries → Add JAR/Folder

### 3. Configuração da Conexão

Edite o arquivo `DatabaseConfig.java` e ajuste as credenciais:

```java
private static final String URL = "jdbc:mysql://localhost:3306/gestao_projetos";
private static final String USER = "root";
private static final String PASSWORD = "sua_senha_aqui";
```

### 4. Estrutura de Pacotes

Organize o código seguindo esta estrutura:

```
src/
├── main/
│   ├── Main.java
│   ├── config/
│   │   └── DatabaseConfig.java
│   ├── model/
│   │   ├── Usuario.java
│   │   ├── Projeto.java
│   │   ├── Equipe.java
│   │   ├── Tarefa.java
│   │   └── enums/
│   │       ├── PerfilUsuario.java
│   │       ├── StatusProjeto.java
│   │       └── StatusTarefa.java
│   ├── dao/
│   │   ├── UsuarioDAO.java
│   │   ├── ProjetoDAO.java
│   │   ├── EquipeDAO.java
│   │   └── TarefaDAO.java
│   └── view/
│       ├── LoginFrame.java
│       ├── MainFrame.java
│       ├── usuario/
│       │   └── ListaUsuariosPanel.java
│       ├── projeto/
│       │   └── ListaProjetosPanel.java
│       ├── equipe/
│       │   └── ListaEquipesPanel.java
│       ├── tarefa/
│       │   └── ListaTarefasPanel.java
│       └── relatorio/
│           └── RelatoriosPanel.java
```

## 🎯 Funcionalidades Implementadas

### ✅ Requisitos Explícitos

1. **Cadastro de Usuários**
    - Campos: nome, CPF, e-mail, cargo, login, senha
    - Perfis: Administrador, Gerente, Colaborador
    - Validação de campos obrigatórios

2. **Cadastro de Projetos**
    - Campos: nome, descrição, datas, status
    - Gerente responsável
    - Filtros por gerente e status

3. **Cadastro de Equipes**
    - Nome e descrição da equipe
    - Gerenciamento de membros
    - Uma equipe pode ter múltiplos membros

4. **Alocação de Equipes a Projetos**
    - Relacionamento N:N entre projetos e equipes
    - Alocação e desalocação de equipes

5. **Cadastro de Tarefas**
    - Campos completos com datas previstas e reais
    - Status: Pendente, Em Execução, Concluída, Cancelada
    - Vinculação a projeto e responsável

6. **Relatórios e Dashboards**
    - Resumo de andamento dos projetos
    - Desempenho por colaborador
    - Projetos com risco de atraso
    - Taxa de conclusão de tarefas

7. **Autenticação**
    - Login com validação no banco
    - Controle de sessão do usuário logado

8. **Interface Visual**
    - Design moderno e intuitivo
    - Navegação por menu lateral
    - Cores e fontes profissionais

### ✅ Requisitos Implícitos Resolvidos

1. **Vinculação entre entidades**
    - Relacionamentos implementados via DAOs
    - Chaves estrangeiras no banco de dados
    - Integridade referencial

2. **Usuário em múltiplas equipes**
    - ✅ Sim, através da tabela `equipe_membros`
    - Permite relacionamento N:N

3. **Tarefas de projeto cancelado**
    - Status das tarefas alterado para CANCELADA
    - Implementado no método `deletar` do ProjetoDAO

4. **Histórico de alterações**
    - Tabela `historico_tarefas` registra mudanças de status
    - Registra usuário, data e observações

5. **Controle de permissões**
    - Verificação de perfil em cada painel
    - Botões habilitados/desabilitados por perfil
    - Administrador tem acesso total

6. **Relacionamento entre tabelas**
    - JOIN implementado nos DAOs
    - Queries otimizadas com LEFT JOIN
    - Carregamento eficiente de dados relacionados

7. **Organização de pacotes**
    - Separação clara: model, dao, view, config
    - Pacotes específicos por funcionalidade
    - Seguindo padrões MVC

8. **Logs de sistema**
    - Tabela `logs_sistema` registra todas operações
    - Rastreabilidade de ações por usuário
    - Detalhes de INSERT, UPDATE, DELETE

9. **Validações**
    - Campos obrigatórios validados
    - Mensagens de erro claras
    - Validação antes de salvar no banco

## 🔐 Credenciais Padrão

- **Login:** admin
- **Senha:** admin123
- **Perfil:** Administrador

## 🎨 Características da Interface

- Design responsivo com BorderLayout
- Cores corporativas (azul, verde, vermelho)
- Tabelas com scroll e seleção
- Diálogos modais para cadastros
- Feedback visual (cores de status)
- Cursores interativos em botões

## 🔧 Padrões de Projeto Utilizados

1. **DAO (Data Access Object)**
    - Separação da lógica de acesso aos dados
    - Classes DAO por entidade

2. **MVC (Model-View-Controller)**
    - Model: classes de entidade
    - View: componentes Swing
    - Controller: lógica nos DAOs e eventos

3. **Singleton (implícito)**
    - Conexão com banco gerenciada centralmente

4. **SwingWorker**
    - Operações assíncronas
    - Evita travamento da interface

## 📊 Modelo de Dados

### Principais Tabelas

- `usuarios`: Dados dos usuários do sistema
- `projetos`: Informações dos projetos
- `equipes`: Cadastro de equipes
- `equipe_membros`: Relacionamento usuário-equipe
- `projeto_equipes`: Relacionamento projeto-equipe
- `tarefas`: Tarefas dos projetos
- `historico_tarefas`: Rastreamento de mudanças
- `logs_sistema`: Auditoria de operações

## 🚨 Solução de Problemas

### Erro de Conexão com MySQL

```
SQLException: Access denied for user
```

**Solução:** Verifique usuário e senha no `DatabaseConfig.java`

### Driver MySQL não encontrado

```
ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

**Solução:** Adicione o MySQL Connector/J ao classpath

### Tabelas não existem

```
Table 'gestao_projetos.usuarios' doesn't exist
```

**Solução:** Execute o script SQL completo

## 📝 Melhorias Futuras Sugeridas

1. Criptografia de senhas (BCrypt)
2. Gráficos de desempenho (JFreeChart)
3. Exportação de relatórios (PDF, Excel)
4. Notificações de prazos próximos
5. Dashboard interativo com métricas
6. Filtros avançados nas listagens
7. Busca global no sistema
8. Anexos em tarefas e projetos
9. Comentários em tarefas
10. Integração com e-mail

## 👨‍💻 Desenvolvedor

Pedro Verissimo Rocha Reis
Sistema desenvolvido como projeto acadêmico para disciplina de Programação de Soluções Computacionais

## 📄 Licença

Este projeto é de uso educacional.

---

**Observação:** Este é um sistema completo e funcional que atende todos os requisitos
explícitos e implícitos solicitados. Todos os componentes estão prontos para compilação
e execução.
*/