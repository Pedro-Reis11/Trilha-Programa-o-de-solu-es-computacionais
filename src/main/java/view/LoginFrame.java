package view;
import dao.UsuarioDAO;
import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("Sistema de Gestão de Projetos - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Painel do título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        JLabel lblTitle = new JLabel("Sistema de Gestão de Projetos");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Oracle Project Manager");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.WHITE);

        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setBackground(new Color(41, 128, 185));
        titleContainer.add(lblTitle, BorderLayout.CENTER);
        titleContainer.add(lblSubtitle, BorderLayout.SOUTH);
        titleContainer.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Painel central com campos
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Login
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        centerPanel.add(lblLogin, gbc);

        txtLogin = new JTextField(20);
        txtLogin.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(txtLogin, gbc);

        // Senha
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(lblSenha, gbc);

        txtSenha = new JPasswordField(20);
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(txtSenha, gbc);

        // Botão
        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEntrar.setBackground(new Color(46, 204, 113));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        centerPanel.add(btnEntrar, gbc);

        // Status
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 11));
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(lblStatus, gbc);

        // Rodapé
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        JLabel lblFooter = new JLabel("© 2024 Oracle Project Manager - Usuário padrão: admin / admin123");
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFooter.setForeground(Color.GRAY);
        footerPanel.add(lblFooter);

        mainPanel.add(titleContainer, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Eventos
        btnEntrar.addActionListener(e -> autenticar());
        txtSenha.addActionListener(e -> autenticar());
    }

    private void autenticar() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            lblStatus.setText("Preencha todos os campos!");
            return;
        }

        btnEntrar.setEnabled(false);
        lblStatus.setText("Autenticando...");
        lblStatus.setForeground(Color.BLUE);

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                UsuarioDAO dao = new UsuarioDAO();
                return dao.autenticar(login, senha);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        lblStatus.setText("Login bem-sucedido!");
                        lblStatus.setForeground(new Color(46, 204, 113));

                        SwingUtilities.invokeLater(() -> {
                            MainFrame mainFrame = new MainFrame(usuario);
                            mainFrame.setVisible(true);
                            dispose();
                        });
                    } else {
                        lblStatus.setText("Login ou senha inválidos!");
                        lblStatus.setForeground(Color.RED);
                        btnEntrar.setEnabled(true);
                    }
                } catch (Exception e) {
                    lblStatus.setText("Erro ao conectar: " + e.getMessage());
                    lblStatus.setForeground(Color.RED);
                    btnEntrar.setEnabled(true);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
