package dao;
import config.DatabaseConfig;
import model.Equipe;
import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    public void inserir(Equipe equipe, Integer usuarioLogadoId) throws SQLException {
        String sql = "INSERT INTO equipes (nome, descricao) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                equipe.setId(rs.getInt(1));
            }

            registrarLog(conn, usuarioLogadoId, "INSERT", "equipes", equipe.getId(),
                    "Equipe criada: " + equipe.getNome());
        }
    }

    public void atualizar(Equipe equipe, Integer usuarioLogadoId) throws SQLException {
        String sql = "UPDATE equipes SET nome = ?, descricao = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.setInt(3, equipe.getId());
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "UPDATE", "equipes", equipe.getId(),
                    "Equipe atualizada: " + equipe.getNome());
        }
    }

    public void deletar(Integer id, Integer usuarioLogadoId) throws SQLException {
        String sql = "DELETE FROM equipes WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "DELETE", "equipes", id, "Equipe removida");
        }
    }

    public Equipe buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM equipes WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Equipe equipe = extrairEquipe(rs);
                equipe.setMembros(listarMembrosPorEquipe(conn, id));
                return equipe;
            }
            return null;
        }
    }

    public List<Equipe> listarTodas() throws SQLException {
        String sql = "SELECT * FROM equipes ORDER BY nome";
        List<Equipe> equipes = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Equipe equipe = extrairEquipe(rs);
                equipe.setMembros(listarMembrosPorEquipe(conn, equipe.getId()));
                equipes.add(equipe);
            }
        }
        return equipes;
    }

    public void adicionarMembro(Integer equipeId, Integer usuarioId, Integer usuarioLogadoId)
            throws SQLException {
        String sql = "INSERT INTO equipe_membros (equipe_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "INSERT", "equipe_membros", equipeId,
                    "Membro adicionado à equipe");
        }
    }

    public void removerMembro(Integer equipeId, Integer usuarioId, Integer usuarioLogadoId)
            throws SQLException {
        String sql = "DELETE FROM equipe_membros WHERE equipe_id = ? AND usuario_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "DELETE", "equipe_membros", equipeId,
                    "Membro removido da equipe");
        }
    }

    private List<Usuario> listarMembrosPorEquipe(Connection conn, Integer equipeId)
            throws SQLException {
        String sql = "SELECT u.* FROM usuarios u " +
                "INNER JOIN equipe_membros em ON u.id = em.usuario_id " +
                "WHERE em.equipe_id = ? AND u.ativo = TRUE " +
                "ORDER BY u.nome_completo";
        List<Usuario> membros = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, equipeId);
            ResultSet rs = stmt.executeQuery();

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            while (rs.next()) {
                membros.add(extrairUsuarioSimples(rs));
            }
        }
        return membros;
    }

    private Equipe extrairEquipe(ResultSet rs) throws SQLException {
        Equipe equipe = new Equipe();
        equipe.setId(rs.getInt("id"));
        equipe.setNome(rs.getString("nome"));
        equipe.setDescricao(rs.getString("descricao"));

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) {
            equipe.setDataCadastro(ts.toLocalDateTime());
        }
        return equipe;
    }

    private Usuario extrairUsuarioSimples(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNomeCompleto(rs.getString("nome_completo"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setEmail(rs.getString("email"));
        return usuario;
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
