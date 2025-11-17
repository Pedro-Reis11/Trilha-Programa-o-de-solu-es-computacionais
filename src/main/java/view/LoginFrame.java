package view;

import dao.UsuarioDAO;
import model.Usuario;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("Sistema de Gestão de Projetos - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 480)); // Responsivo sem limitar muito
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 242, 245),
                        0, h, Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        /* ----- HEADER RESPONSIVO ----- */
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185),
                        w, 0, new Color(52, 152, 219));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Sistema de Gestão de Projetos");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Oracle Project Manager");
        lblSubtitle.setFont(lblSubtitle.getFont().deriveFont(Font.PLAIN, 14f));
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalGlue());
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createVerticalGlue());

        /* ----- CENTRO RESPONSIVO ----- */
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(lblLogin.getFont().deriveFont(Font.BOLD, 14f));
        lblLogin.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(lblLogin, gbc);

        txtLogin = new JTextField();
        txtLogin.setBorder(compoundField());
        txtLogin.setFont(txtLogin.getFont().deriveFont(14f));
        gbc.gridy = 1;
        centerPanel.add(txtLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(lblSenha.getFont().deriveFont(Font.BOLD, 14f));
        lblSenha.setForeground(new Color(52, 73, 94));
        gbc.gridy = 2;
        centerPanel.add(lblSenha, gbc);

        txtSenha = new JPasswordField();
        txtSenha.setBorder(compoundField());
        txtSenha.setFont(txtSenha.getFont().deriveFont(14f));
        gbc.gridy = 3;
        centerPanel.add(txtSenha, gbc);

        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(btnEntrar.getFont().deriveFont(Font.BOLD, 15f));
        btnEntrar.setBackground(new Color(46, 204, 113));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.gridy = 4;
        gbc.ipady = 15;
        centerPanel.add(btnEntrar, gbc);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.ITALIC, 12f));
        lblStatus.setForeground(new Color(231, 76, 60));

        gbc.gridy = 5;
        gbc.ipady = 0;
        centerPanel.add(lblStatus, gbc);

        /* ----- FOOTER ----- */
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);

        JLabel lblFooter = new JLabel("© 2024 Oracle Project Manager");
        lblFooter.setForeground(new Color(120, 120, 120));

        JLabel lblCred = new JLabel(" | Usuário padrão: admin / admin123");
        lblCred.setForeground(new Color(140, 140, 140));

        footerPanel.add(lblFooter);
        footerPanel.add(lblCred);

        /* ----- MONTAGEM FINAL ----- */
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        btnEntrar.addActionListener(e -> autenticar());
        txtSenha.addActionListener(e -> autenticar());
        txtLogin.addActionListener(e -> txtSenha.requestFocus());
    }

    private Border compoundField() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
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
                Thread.sleep(600);
                return new UsuarioDAO().autenticar(login, senha);
            }

            @Override
            protected void done() {
                try {
                    Usuario u = get();
                    if (u != null) {
                        lblStatus.setText("✓ Login bem-sucedido!");
                        lblStatus.setForeground(new Color(46, 204, 113));

                        Timer t = new Timer(700, e -> {
                            new MainFrame(u).setVisible(true);
                            dispose();
                        });
                        t.setRepeats(false);
                        t.start();
                    } else {
                        lblStatus.setText("✗ Login ou senha inválidos!");
                        lblStatus.setForeground(new Color(231, 76, 60));
                        btnEntrar.setEnabled(true);
                        btnEntrar.setText("Entrar");
                        txtSenha.setText("");
                    }
                } catch (Exception ex) {
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("Erro: " + ex.getMessage());
                    btnEntrar.setEnabled(true);
                    btnEntrar.setText("Entrar");
                }
            }
        }.execute();
    }
}
