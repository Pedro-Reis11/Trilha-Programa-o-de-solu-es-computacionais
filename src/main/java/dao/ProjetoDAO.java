package dao;
import config.DatabaseConfig;
import model.Projeto;
import model.enums.StatusProjeto;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    public void inserir(Projeto projeto, Integer usuarioLogadoId) throws SQLException {
        String sql = "INSERT INTO projetos (nome, descricao, data_inicio, data_termino_prevista, " +
                "status, gerente_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataTerminoPrevista()));
            stmt.setString(5, projeto.getStatus().name());
            stmt.setInt(6, projeto.getGerenteId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                projeto.setId(rs.getInt(1));
            }

            registrarLog(conn, usuarioLogadoId, "INSERT", "projetos", projeto.getId(),
                    "Projeto criado: " + projeto.getNome());
        }
    }

    public void atualizar(Projeto projeto, Integer usuarioLogadoId) throws SQLException {
        String sql = "UPDATE projetos SET nome = ?, descricao = ?, data_inicio = ?, " +
                "data_termino_prevista = ?, data_termino_real = ?, status = ?, gerente_id = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataTerminoPrevista()));
            stmt.setDate(5, projeto.getDataTerminoReal() != null ?
                    Date.valueOf(projeto.getDataTerminoReal()) : null);
            stmt.setString(6, projeto.getStatus().name());
            stmt.setInt(7, projeto.getGerenteId());
            stmt.setInt(8, projeto.getId());

            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "UPDATE", "projetos", projeto.getId(),
                    "Projeto atualizado: " + projeto.getNome());
        }
    }

    public void deletar(Integer id, Integer usuarioLogadoId) throws SQLException {
        String sql = "UPDATE tarefas SET status = 'CANCELADA' WHERE projeto_id = ?";
        String sql2 = "UPDATE projetos SET status = 'CANCELADO' WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(sql);
                 PreparedStatement stmt2 = conn.prepareStatement(sql2)) {

                stmt1.setInt(1, id);
                stmt1.executeUpdate();

                stmt2.setInt(1, id);
                stmt2.executeUpdate();

                conn.commit();

                registrarLog(conn, usuarioLogadoId, "DELETE", "projetos", id,
                        "Projeto cancelado e tarefas atualizadas");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Projeto buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome " +
                "FROM projetos p " +
                "LEFT JOIN usuarios u ON p.gerente_id = u.id " +
                "WHERE p.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairProjeto(rs);
            }
            return null;
        }
    }

    public List<Projeto> listarTodos() throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome " +
                "FROM projetos p " +
                "LEFT JOIN usuarios u ON p.gerente_id = u.id " +
                "WHERE p.status != 'CANCELADO' " +
                "ORDER BY p.data_inicio DESC";

        return executarConsultaLista(sql);
    }

    public List<Projeto> listarPorGerente(Integer gerenteId) throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome " +
                "FROM projetos p " +
                "LEFT JOIN usuarios u ON p.gerente_id = u.id " +
                "WHERE p.gerente_id = ? AND p.status != 'CANCELADO' " +
                "ORDER BY p.data_inicio DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gerenteId);
            return executarConsultaLista(stmt);
        }
    }

    public List<Projeto> listarComRiscoAtraso() throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome " +
                "FROM projetos p " +
                "LEFT JOIN usuarios u ON p.gerente_id = u.id " +
                "WHERE p.status = 'EM_ANDAMENTO' " +
                "AND p.data_termino_prevista < CURDATE() " +
                "ORDER BY p.data_termino_prevista";

        return executarConsultaLista(sql);
    }

    public void alocarEquipe(Integer projetoId, Integer equipeId, Integer usuarioLogadoId)
            throws SQLException {
        String sql = "INSERT INTO projeto_equipes (projeto_id, equipe_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            stmt.setInt(2, equipeId);
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "INSERT", "projeto_equipes", projetoId,
                    "Equipe alocada ao projeto");
        }
    }

    public void desalocarEquipe(Integer projetoId, Integer equipeId, Integer usuarioLogadoId)
            throws SQLException {
        String sql = "DELETE FROM projeto_equipes WHERE projeto_id = ? AND equipe_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            stmt.setInt(2, equipeId);
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "DELETE", "projeto_equipes", projetoId,
                    "Equipe desalocada do projeto");
        }
    }

    private List<Projeto> executarConsultaLista(String sql) throws SQLException {
        List<Projeto> projetos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projetos.add(extrairProjeto(rs));
            }
        }
        return projetos;
    }

    private List<Projeto> executarConsultaLista(PreparedStatement stmt) throws SQLException {
        List<Projeto> projetos = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            projetos.add(extrairProjeto(rs));
        }
        return projetos;
    }

    private Projeto extrairProjeto(ResultSet rs) throws SQLException {
        Projeto projeto = new Projeto();
        projeto.setId(rs.getInt("id"));
        projeto.setNome(rs.getString("nome"));
        projeto.setDescricao(rs.getString("descricao"));

        Date dataInicio = rs.getDate("data_inicio");
        if (dataInicio != null) {
            projeto.setDataInicio(dataInicio.toLocalDate());
        }

        Date dataTerminoPrev = rs.getDate("data_termino_prevista");
        if (dataTerminoPrev != null) {
            projeto.setDataTerminoPrevista(dataTerminoPrev.toLocalDate());
        }

        Date dataTerminoReal = rs.getDate("data_termino_real");
        if (dataTerminoReal != null) {
            projeto.setDataTerminoReal(dataTerminoReal.toLocalDate());
        }

        projeto.setStatus(StatusProjeto.valueOf(rs.getString("status")));
        projeto.setGerenteId(rs.getInt("gerente_id"));
        projeto.setGerenteNome(rs.getString("gerente_nome"));

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) {
            projeto.setDataCadastro(ts.toLocalDateTime());
        }

        return projeto;
    }

    private void registrarLog(Connection conn, Integer usuarioId, String acao,
                              String tabela, Integer registroId, String detalhes) {
        try {
            String sql = "INSERT INTO logs_sistema (usuario_id, acao, tabela_afetada, registro_id, detalhes) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, usuarioId);
            stmt.setString(2, acao);
            stmt.setString(3, tabela);
            stmt.setInt(4, registroId);
            stmt.setString(5, detalhes);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
