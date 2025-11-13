package view;
import model.Usuario;
import model.enums.PerfilUsuario;
import view.usuario.ListaUsuariosPanel;
import view.projeto.ListaProjetosPanel;
import view.equipe.ListaEquipesPanel;
import view.tarefa.ListaTarefasPanel;
import view.relatorio.RelatoriosPanel;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Usuario usuarioLogado;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(Usuario usuario) {
        this.usuarioLogado = usuario;

        setTitle("Sistema de Gestão de Projetos - " + usuario.getNomeCompleto());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Painel superior com informações do usuário
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(41, 128, 185));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblWelcome = new JLabel("Bem-vindo, " + usuarioLogado.getNomeCompleto());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        lblWelcome.setForeground(Color.WHITE);

        JLabel lblPerfil = new JLabel("Perfil: " + usuarioLogado.getPerfil().getDescricao());
        lblPerfil.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPerfil.setForeground(Color.WHITE);

        JButton btnSair = new JButton("Sair");
        btnSair.setBackground(new Color(231, 76, 60));
        btnSair.setForeground(Color.WHITE);
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> sair());

        JPanel userInfoPanel = new JPanel(new BorderLayout(10, 5));
        userInfoPanel.setBackground(new Color(41, 128, 185));
        userInfoPanel.add(lblWelcome, BorderLayout.NORTH);
        userInfoPanel.add(lblPerfil, BorderLayout.CENTER);

        topPanel.add(userInfoPanel, BorderLayout.WEST);
        topPanel.add(btnSair, BorderLayout.EAST);

        // Menu lateral
        JPanel menuPanel = createMenuPanel();

        // Painel de conteúdo com CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Adicionar painéis
        contentPanel.add(new JPanel(), "HOME");
        contentPanel.add(new ListaProjetosPanel(usuarioLogado), "PROJETOS");
        contentPanel.add(new ListaTarefasPanel(usuarioLogado), "TAREFAS");
        contentPanel.add(new ListaEquipesPanel(usuarioLogado), "EQUIPES");
        contentPanel.add(new RelatoriosPanel(usuarioLogado), "RELATORIOS");

        if (usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            contentPanel.add(new ListaUsuariosPanel(usuarioLogado), "USUARIOS");
        }

        add(topPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Mostrar painel inicial
        cardLayout.show(contentPanel, "PROJETOS");
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(52, 73, 94));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Botões do menu
        addMenuButton(menuPanel, "Projetos", "PROJETOS");
        addMenuButton(menuPanel, "Tarefas", "TAREFAS");
        addMenuButton(menuPanel, "Equipes", "EQUIPES");
        addMenuButton(menuPanel, "Relatórios", "RELATORIOS");

        if (usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            addMenuButton(menuPanel, "Usuários", "USUARIOS");
        }

        return menuPanel;
    }

    private void addMenuButton(JPanel panel, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));

        panel.add(Box.createVerticalStrut(5));
        panel.add(btn);
    }

    private void sair() {
        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair do sistema?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION
        );

        if (opcao == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}
