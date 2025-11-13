package dao;
import config.DatabaseConfig;
import model.Usuario;
import model.enums.PerfilUsuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ? AND ativo = TRUE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairUsuario(rs);
            }
            return null;
        }
    }

    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome_completo, cpf, email, cargo, login, senha, perfil) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getCargo());
            stmt.setString(5, usuario.getLogin());
            stmt.setString(6, usuario.getSenha());
            stmt.setString(7, usuario.getPerfil().name());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getInt(1));
            }

            registrarLog(conn, null, "INSERT", "usuarios", usuario.getId(),
                    "Usuário cadastrado: " + usuario.getNomeCompleto());
        }
    }

    public void atualizar(Usuario usuario, Integer usuarioLogadoId) throws SQLException {
        String sql = "UPDATE usuarios SET nome_completo = ?, cpf = ?, email = ?, " +
                "cargo = ?, login = ?, senha = ?, perfil = ?, ativo = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getCargo());
            stmt.setString(5, usuario.getLogin());
            stmt.setString(6, usuario.getSenha());
            stmt.setString(7, usuario.getPerfil().name());
            stmt.setBoolean(8, usuario.isAtivo());
            stmt.setInt(9, usuario.getId());

            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "UPDATE", "usuarios", usuario.getId(),
                    "Usuário atualizado: " + usuario.getNomeCompleto());
        }
    }

    public void deletar(Integer id, Integer usuarioLogadoId) throws SQLException {
        String sql = "UPDATE usuarios SET ativo = FALSE WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            registrarLog(conn, usuarioLogadoId, "DELETE", "usuarios", id,
                    "Usuário desativado");
        }
    }

    public Usuario buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairUsuario(rs);
            }
            return null;
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE ativo = TRUE ORDER BY nome_completo";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(extrairUsuario(rs));
            }
        }
        return usuarios;
    }

    public List<Usuario> listarPorPerfil(PerfilUsuario perfil) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE perfil = ? AND ativo = TRUE ORDER BY nome_completo";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, perfil.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(extrairUsuario(rs));
            }
        }
        return usuarios;
    }

    private Usuario extrairUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNomeCompleto(rs.getString("nome_completo"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setEmail(rs.getString("email"));
        usuario.setCargo(rs.getString("cargo"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setPerfil(PerfilUsuario.valueOf(rs.getString("perfil")));
        usuario.setAtivo(rs.getBoolean("ativo"));

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) {
            usuario.setDataCadastro(ts.toLocalDateTime());
        }
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
            e.printStackTrace(); // Log de erro sem interromper operação principal
        }
    }
}
