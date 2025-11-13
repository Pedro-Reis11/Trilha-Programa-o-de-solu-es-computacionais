package view.usuario;
import dao.UsuarioDAO;
import model.Usuario;
import model.enums.PerfilUsuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListaUsuariosPanel extends JPanel {
    private Usuario usuarioLogado;
    private JTable table;
    private DefaultTableModel tableModel;
    private UsuarioDAO usuarioDAO;

    public ListaUsuariosPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.usuarioDAO = new UsuarioDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Gerenciamento de Usuários");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnNovo = new JButton("+ Novo Usuário");
        btnNovo.setBackground(new Color(46, 204, 113));
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.addActionListener(e -> abrirCadastro(null));

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnNovo, BorderLayout.EAST);

        // Tabela
        String[] colunas = {"ID", "Nome", "CPF", "E-mail", "Cargo", "Perfil", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarDados());

        buttonPanel.add(btnEditar);
        buttonPanel.add(btnAtualizar);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        tableModel.setRowCount(0);

        new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                return usuarioDAO.listarTodos();
            }

            @Override
            protected void done() {
                try {
                    List<Usuario> usuarios = get();
                    for (Usuario u : usuarios) {
                        tableModel.addRow(new Object[]{
                                u.getId(),
                                u.getNomeCompleto(),
                                u.getCpf(),
                                u.getEmail(),
                                u.getCargo(),
                                u.getPerfil().getDescricao(),
                                u.isAtivo() ? "Ativo" : "Inativo"
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaUsuariosPanel.this,
                            "Erro ao carregar usuários: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void editarSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário!");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return usuarioDAO.buscarPorId(id);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    abrirCadastro(usuario);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaUsuariosPanel.this,
                            "Erro ao buscar usuário: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void abrirCadastro(Usuario usuario) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                usuario == null ? "Novo Usuário" : "Editar Usuário",
                true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos
        JTextField txtNome = new JTextField(20);
        JTextField txtCpf = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtCargo = new JTextField(20);
        JTextField txtLogin = new JTextField(20);
        JPasswordField txtSenha = new JPasswordField(20);
        JComboBox<PerfilUsuario> cbPerfil = new JComboBox<>(PerfilUsuario.values());
        JCheckBox chkAtivo = new JCheckBox("Ativo");
        chkAtivo.setSelected(true);

        if (usuario != null) {
            txtNome.setText(usuario.getNomeCompleto());
            txtCpf.setText(usuario.getCpf());
            txtEmail.setText(usuario.getEmail());
            txtCargo.setText(usuario.getCargo());
            txtLogin.setText(usuario.getLogin());
            txtSenha.setText(usuario.getSenha());
            cbPerfil.setSelectedItem(usuario.getPerfil());
            chkAtivo.setSelected(usuario.isAtivo());
        }

        int row = 0;
        adicionarCampo(panel, gbc, row++, "Nome Completo:", txtNome);
        adicionarCampo(panel, gbc, row++, "CPF:", txtCpf);
        adicionarCampo(panel, gbc, row++, "E-mail:", txtEmail);
        adicionarCampo(panel, gbc, row++, "Cargo:", txtCargo);
        adicionarCampo(panel, gbc, row++, "Login:", txtLogin);
        adicionarCampo(panel, gbc, row++, "Senha:", txtSenha);
        adicionarCampo(panel, gbc, row++, "Perfil:", cbPerfil);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(chkAtivo, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> {
            if (validarCampos(txtNome, txtCpf, txtEmail, txtCargo, txtLogin, txtSenha)) {
                Usuario u = usuario != null ? usuario : new Usuario();
                u.setNomeCompleto(txtNome.getText().trim());
                u.setCpf(txtCpf.getText().trim());
                u.setEmail(txtEmail.getText().trim());
                u.setCargo(txtCargo.getText().trim());
                u.setLogin(txtLogin.getText().trim());
                u.setSenha(new String(txtSenha.getPassword()));
                u.setPerfil((PerfilUsuario) cbPerfil.getSelectedItem());
                u.setAtivo(chkAtivo.isSelected());

                salvarUsuario(u, dialog);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void adicionarCampo(JPanel panel, GridBagConstraints gbc, int row,
                                String label, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private boolean validarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios!");
                return false;
            }
        }
        return true;
    }

    private void salvarUsuario(Usuario usuario, JDialog dialog) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (usuario.getId() == null) {
                    usuarioDAO.inserir(usuario);
                } else {
                    usuarioDAO.atualizar(usuario, usuarioLogado.getId());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(dialog, "Usuário salvo com sucesso!");
                    dialog.dispose();
                    carregarDados();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erro ao salvar: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
