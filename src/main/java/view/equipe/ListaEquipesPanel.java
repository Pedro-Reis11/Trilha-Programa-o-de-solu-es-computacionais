package view.equipe;
import dao.EquipeDAO;
import model.Equipe;
import model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListaEquipesPanel extends JPanel {
    private Usuario usuarioLogado;
    private JTable table;
    private DefaultTableModel tableModel;
    private EquipeDAO equipeDAO;

    public ListaEquipesPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.equipeDAO = new EquipeDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Gerenciamento de Equipes");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnNova = new JButton("+ Nova Equipe");
        btnNova.setBackground(new Color(46, 204, 113));
        btnNova.setForeground(Color.WHITE);
        btnNova.setFocusPainted(false);
        btnNova.addActionListener(e -> abrirCadastro(null));

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnNova, BorderLayout.EAST);

        String[] colunas = {"ID", "Nome", "Descrição", "Membros"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarDados());

        buttonPanel.add(btnEditar);
        buttonPanel.add(btnAtualizar);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        tableModel.setRowCount(0);

        new SwingWorker<List<Equipe>, Void>() {
            @Override
            protected List<Equipe> doInBackground() throws Exception {
                return equipeDAO.listarTodas();
            }

            @Override
            protected void done() {
                try {
                    List<Equipe> equipes = get();
                    for (Equipe e : equipes) {
                        tableModel.addRow(new Object[]{
                                e.getId(),
                                e.getNome(),
                                e.getDescricao(),
                                e.getMembros().size() + " membros"
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaEquipesPanel.this,
                            "Erro ao carregar equipes: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void editarSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe!");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);

        new SwingWorker<Equipe, Void>() {
            @Override
            protected Equipe doInBackground() throws Exception {
                return equipeDAO.buscarPorId(id);
            }

            @Override
            protected void done() {
                try {
                    abrirCadastro(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaEquipesPanel.this,
                            "Erro: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void abrirCadastro(Equipe equipe) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                equipe == null ? "Nova Equipe" : "Editar Equipe",
                true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtNome = new JTextField(30);
        JTextArea txtDescricao = new JTextArea(5, 30);
        txtDescricao.setLineWrap(true);

        if (equipe != null) {
            txtNome.setText(equipe.getNome());
            txtDescricao.setText(equipe.getDescricao());
        }

        int row = 0;
        adicionarCampo(panel, gbc, row++, "Nome:", txtNome);
        adicionarCampo(panel, gbc, row++, "Descrição:", new JScrollPane(txtDescricao));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> {
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome da equipe!");
                return;
            }

            Equipe eq = equipe != null ? equipe : new Equipe();
            eq.setNome(txtNome.getText().trim());
            eq.setDescricao(txtDescricao.getText().trim());

            salvarEquipe(eq, dialog);
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

    private void salvarEquipe(Equipe equipe, JDialog dialog) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (equipe.getId() == null) {
                    equipeDAO.inserir(equipe, usuarioLogado.getId());
                } else {
                    equipeDAO.atualizar(equipe, usuarioLogado.getId());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(dialog, "Equipe salva com sucesso!");
                    dialog.dispose();
                    carregarDados();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog, "Erro: " + e.getMessage());
                }
            }
        }.execute();
    }
}
