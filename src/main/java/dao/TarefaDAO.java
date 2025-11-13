package dao;
import config.DatabaseConfig;
import model.Tarefa;
import model.enums.StatusTarefa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    public void inserir(Tarefa tarefa, Integer usuarioLogadoId) throws SQLException {
        String sql = "INSERT INTO tarefas (titulo, descricao, projeto_id, responsavel_id, status, " +
                "data_inicio_prevista, data_fim_prevista) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjetoId());
            stmt.setInt(4, tarefa.getResponsavelId());
            stmt.setString(5, tarefa.getStatus().name());
            stmt.setDate(6, Date.valueOf(tarefa.getDataInicioPrevista()));
            stmt.setDate(7, Date.valueOf(tarefa.getDataFimPrevista()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                tarefa.setId(rs.getInt(1));
            }

            registrarLog(conn, usuarioLogadoId, "INSERT", "tarefas", tarefa.getId(),
                    "Tarefa criada: " + tarefa.getTitulo());
        }
    }

    public void atualizar(Tarefa tarefa, Integer usuarioLogadoId) throws SQLException {
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ?, projeto_id = ?, " +
                "responsavel_id = ?, status = ?, data_inicio_prevista = ?, " +
                "data_fim_prevista = ?, data_inicio_real = ?, data_fim_real = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            StatusTarefa statusAnterior = buscarStatusAnterior(conn, tarefa.getId());

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjetoId());
            stmt.setInt(4, tarefa.getResponsavelId());
            stmt.setString(5, tarefa.getStatus().name());
            stmt.setDate(6, Date.valueOf(tarefa.getDataInicioPrevista()));
            stmt.setDate(7, Date.valueOf(tarefa.getDataFimPrevista()));
            stmt.setDate(8, tarefa.getDataInicioReal() != null ?
                    Date.valueOf(tarefa.getDataInicioReal()) : null);
            stmt.setDate(9, tarefa.getDataFimReal() != null ?
                    Date.valueOf(tarefa.getDataFimReal()) : null);
            stmt.setInt(10, tarefa.getId());

            stmt.executeUpdate();

            if (statusAnterior != null && !statusAnterior.equals(tarefa.getStatus())) {
                registrarHistoricoTarefa(conn, tarefa.getId(), statusAnterior,
                        tarefa.getStatus(), usuarioLogadoId, null);
            }

            registrarLog(conn, usuarioLogadoId, "UPDATE", "tarefas", tarefa.getId(),
                    "Tarefa atualizada: " + tarefa.getTitulo());
        }
    }

    public void deletar(Integer id, Integer usuarioLogadoId) throws SQLException {
        String sql = "UPDATE tarefas SET status = 'CANCELADA' WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "DELETE", "tarefas", id, "Tarefa cancelada");
        }
    }

    public Tarefa buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT t.*, p.nome as projeto_nome, u.nome_completo as responsavel_nome " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.projeto_id = p.id " +
                "LEFT JOIN usuarios u ON t.responsavel_id = u.id " +
                "WHERE t.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairTarefa(rs);
            }
            return null;
        }
    }

    public List<Tarefa> listarTodas() throws SQLException {
        String sql = "SELECT t.*, p.nome as projeto_nome, u.nome_completo as responsavel_nome " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.projeto_id = p.id " +
                "LEFT JOIN usuarios u ON t.responsavel_id = u.id " +
                "WHERE t.status != 'CANCELADA' " +
                "ORDER BY t.data_fim_prevista";

        return executarConsultaLista(sql);
    }

    public List<Tarefa> listarPorProjeto(Integer projetoId) throws SQLException {
        String sql = "SELECT t.*, p.nome as projeto_nome, u.nome_completo as responsavel_nome " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.projeto_id = p.id " +
                "LEFT JOIN usuarios u ON t.responsavel_id = u.id " +
                "WHERE t.projeto_id = ? AND t.status != 'CANCELADA' " +
                "ORDER BY t.data_fim_prevista";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            return executarConsultaLista(stmt);
        }
    }

    public List<Tarefa> listarPorResponsavel(Integer responsavelId) throws SQLException {
        String sql = "SELECT t.*, p.nome as projeto_nome, u.nome_completo as responsavel_nome " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.projeto_id = p.id " +
                "LEFT JOIN usuarios u ON t.responsavel_id = u.id " +
                "WHERE t.responsavel_id = ? AND t.status != 'CANCELADA' " +
                "ORDER BY t.data_fim_prevista";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, responsavelId);
            return executarConsultaLista(stmt);
        }
    }

    public int contarTarefasPorStatus(Integer usuarioId, StatusTarefa status) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM tarefas WHERE responsavel_id = ? AND status = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, status.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    private StatusTarefa buscarStatusAnterior(Connection conn, Integer tarefaId) throws SQLException {
        String sql = "SELECT status FROM tarefas WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tarefaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return StatusTarefa.valueOf(rs.getString("status"));
            }
        }
        return null;
    }

    private void registrarHistoricoTarefa(Connection conn, Integer tarefaId,
                                          StatusTarefa statusAnterior, StatusTarefa statusNovo,
                                          Integer usuarioId, String observacao) throws SQLException {

        String sql = "INSERT INTO historico_tarefas (tarefa_id, status_anterior, status_novo, " +
                "usuario_id, observacao) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tarefaId);
            stmt.setString(2, statusAnterior != null ? statusAnterior.name() : null);
            stmt.setString(3, statusNovo.name());
            stmt.setInt(4, usuarioId);
            stmt.setString(5, observacao);
            stmt.executeUpdate();
        }
    }

    private List<Tarefa> executarConsultaLista(String sql) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tarefas.add(extrairTarefa(rs));
            }
        }
        return tarefas;
    }

    private List<Tarefa> executarConsultaLista(PreparedStatement stmt) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            tarefas.add(extrairTarefa(rs));
        }
        return tarefas;
    }

    private Tarefa extrairTarefa(ResultSet rs) throws SQLException {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(rs.getInt("id"));
        tarefa.setTitulo(rs.getString("titulo"));
        tarefa.setDescricao(rs.getString("descricao"));
        tarefa.setProjetoId(rs.getInt("projeto_id"));
        tarefa.setProjetoNome(rs.getString("projeto_nome"));
        tarefa.setResponsavelId(rs.getInt("responsavel_id"));
        tarefa.setResponsavelNome(rs.getString("responsavel_nome"));
        tarefa.setStatus(StatusTarefa.valueOf(rs.getString("status")));

        Date dataInicioPrev = rs.getDate("data_inicio_prevista");
        if (dataInicioPrev != null) {
            tarefa.setDataInicioPrevista(dataInicioPrev.toLocalDate());
        }

        Date dataFimPrev = rs.getDate("data_fim_prevista");
        if (dataFimPrev != null) {
            tarefa.setDataFimPrevista(dataFimPrev.toLocalDate());
        }

        Date dataInicioReal = rs.getDate("data_inicio_real");
        if (dataInicioReal != null) {
            tarefa.setDataInicioReal(dataInicioReal.toLocalDate());
        }

        Date dataFimReal = rs.getDate("data_fim_real");
        if (dataFimReal != null) {
            tarefa.setDataFimReal(dataFimReal.toLocalDate());
        }

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) {
            tarefa.setDataCadastro(ts.toLocalDateTime());
        }

        return tarefa;
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
