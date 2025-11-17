package view;
import dao.UsuarioDAO;
import model.Usuario;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("Sistema de Gestão de Projetos - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        // Painel principal com gradiente
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color cor1 = new Color(240, 242, 245);
                Color cor2 = new Color(255, 255, 255);
                GradientPaint gp = new GradientPaint(0, 0, cor1, 0, h, cor2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));

        // Painel do cabeçalho
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color cor1 = new Color(41, 128, 185);
                Color cor2 = new Color(52, 152, 219);
                GradientPaint gp = new GradientPaint(0, 0, cor1, w, 0, cor2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setPreferredSize(new Dimension(500, 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Sistema de Gestão de Projetos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Oracle Project Manager");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(236, 240, 241));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalGlue());
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createVerticalGlue());

        // Painel central com campos
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Label Login
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLogin.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(lblLogin, gbc);

        // Campo Login
        txtLogin = new JTextField(20);
        txtLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLogin.setPreferredSize(new Dimension(300, 40));
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 1;
        centerPanel.add(txtLogin, gbc);

        // Label Senha
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSenha.setForeground(new Color(52, 73, 94));
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        centerPanel.add(lblSenha, gbc);

        // Campo Senha
        txtSenha = new JPasswordField(20);
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSenha.setPreferredSize(new Dimension(300, 40));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 3;
        gbc.insets = new Insets(8, 8, 8, 8);
        centerPanel.add(txtSenha, gbc);

        // Botão Entrar
        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnEntrar.setPreferredSize(new Dimension(300, 45));
        btnEntrar.setBackground(new Color(46, 204, 113));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover no botão
        btnEntrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEntrar.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnEntrar.setBackground(new Color(46, 204, 113));
            }
        });

        gbc.gridy = 4;
        gbc.insets = new Insets(25, 8, 8, 8);
        centerPanel.add(btnEntrar, gbc);

        // Label de status
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(231, 76, 60));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 8, 8, 8);
        centerPanel.add(lblStatus, gbc);

        // Rodapé
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblFooter = new JLabel("© 2024 Oracle Project Manager");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(127, 140, 141));
        footerPanel.add(lblFooter);

        JLabel lblCredentials = new JLabel("  |  Usuário padrão: admin / admin123");
        lblCredentials.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCredentials.setForeground(new Color(149, 165, 166));
        footerPanel.add(lblCredentials);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Eventos
        btnEntrar.addActionListener(e -> autenticar());
        txtSenha.addActionListener(e -> autenticar());
        txtLogin.addActionListener(e -> txtSenha.requestFocus());
    }

    private void autenticar() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            lblStatus.setText("⚠ Preencha todos os campos!");
            lblStatus.setForeground(new Color(230, 126, 34));
            return;
        }

        btnEntrar.setEnabled(false);
        btnEntrar.setText("Autenticando...");
        lblStatus.setText("Verificando credenciais...");
        lblStatus.setForeground(new Color(52, 152, 219));

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                Thread.sleep(500); // Simula delay de rede
                UsuarioDAO dao = new UsuarioDAO();
                return dao.autenticar(login, senha);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        lblStatus.setText("✓ Login bem-sucedido!");
                        lblStatus.setForeground(new Color(46, 204, 113));

                        Timer timer = new Timer(800, e -> {
                            MainFrame mainFrame = new MainFrame(usuario);
                            mainFrame.setVisible(true);
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        lblStatus.setText("✗ Login ou senha inválidos!");
                        lblStatus.setForeground(new Color(231, 76, 60));
                        btnEntrar.setEnabled(true);
                        btnEntrar.setText("Entrar");
                        txtSenha.setText("");
                        txtSenha.requestFocus();
                    }
                } catch (Exception e) {
                    lblStatus.setText("✗ Erro ao conectar: " + e.getMessage());
                    lblStatus.setForeground(new Color(231, 76, 60));
                    btnEntrar.setEnabled(true);
                    btnEntrar.setText("Entrar");
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
