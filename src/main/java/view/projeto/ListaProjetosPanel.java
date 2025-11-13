package view.projeto;
import dao.ProjetoDAO;
import dao.UsuarioDAO;
import model.Projeto;
import model.Usuario;
import model.enums.PerfilUsuario;
import model.enums.StatusProjeto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ListaProjetosPanel extends JPanel {
    private Usuario usuarioLogado;
    private JTable table;
    private DefaultTableModel tableModel;
    private ProjetoDAO projetoDAO;

    public ListaProjetosPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.projetoDAO = new ProjetoDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Gerenciamento de Projetos");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnNovo = new JButton("+ Novo Projeto");
        btnNovo.setBackground(new Color(46, 204, 113));
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.addActionListener(e -> abrirCadastro(null));

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR &&
                usuarioLogado.getPerfil() != PerfilUsuario.GERENTE) {
            btnNovo.setEnabled(false);
        }

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnNovo, BorderLayout.EAST);

        String[] colunas = {"ID", "Nome", "Gerente", "Início", "Término Previsto", "Status"};
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

        new SwingWorker<List<Projeto>, Void>() {
            @Override
            protected List<Projeto> doInBackground() throws Exception {
                if (usuarioLogado.getPerfil() == PerfilUsuario.GERENTE) {
                    return projetoDAO.listarPorGerente(usuarioLogado.getId());
                }
                return projetoDAO.listarTodos();
            }

            @Override
            protected void done() {
                try {
                    List<Projeto> projetos = get();
                    for (Projeto p : projetos) {
                        tableModel.addRow(new Object[]{
                                p.getId(),
                                p.getNome(),
                                p.getGerenteNome(),
                                p.getDataInicio(),
                                p.getDataTerminoPrevista(),
                                p.getStatus().getDescricao()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaProjetosPanel.this,
                            "Erro ao carregar projetos: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void editarSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto!");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);

        new SwingWorker<Projeto, Void>() {
            @Override
            protected Projeto doInBackground() throws Exception {
                return projetoDAO.buscarPorId(id);
            }

            @Override
            protected void done() {
                try {
                    abrirCadastro(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaProjetosPanel.this,
                            "Erro ao buscar projeto: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void abrirCadastro(Projeto projeto) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                projeto == null ? "Novo Projeto" : "Editar Projeto",
                true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtNome = new JTextField(30);
        JTextArea txtDescricao = new JTextArea(5, 30);
        txtDescricao.setLineWrap(true);
        JSpinner spnDataInicio = new JSpinner(new SpinnerDateModel());
        JSpinner spnDataTermino = new JSpinner(new SpinnerDateModel());
        JComboBox<StatusProjeto> cbStatus = new JComboBox<>(StatusProjeto.values());
        JComboBox<Usuario> cbGerente = new JComboBox<>();

        // Carregar gerentes
        carregarGerentes(cbGerente);

        if (projeto != null) {
            txtNome.setText(projeto.getNome());
            txtDescricao.setText(projeto.getDescricao());
            spnDataInicio.setValue(Date.from(projeto.getDataInicio()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            spnDataTermino.setValue(Date.from(projeto.getDataTerminoPrevista()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            cbStatus.setSelectedItem(projeto.getStatus());
        }

        int row = 0;
        adicionarCampo(panel, gbc, row++, "Nome:", txtNome);
        adicionarCampo(panel, gbc, row++, "Descrição:", new JScrollPane(txtDescricao));
        adicionarCampo(panel, gbc, row++, "Data Início:", spnDataInicio);
        adicionarCampo(panel, gbc, row++, "Data Término:", spnDataTermino);
        adicionarCampo(panel, gbc, row++, "Status:", cbStatus);
        adicionarCampo(panel, gbc, row++, "Gerente:", cbGerente);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> {
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome do projeto!");
                return;
            }

            Projeto p = projeto != null ? projeto : new Projeto();
            p.setNome(txtNome.getText().trim());
            p.setDescricao(txtDescricao.getText().trim());

            Date dataInicio = (Date) spnDataInicio.getValue();
            p.setDataInicio(dataInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            Date dataTermino = (Date) spnDataTermino.getValue();
            p.setDataTerminoPrevista(dataTermino.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            p.setStatus((StatusProjeto) cbStatus.getSelectedItem());
            p.setGerenteId(((Usuario) cbGerente.getSelectedItem()).getId());

            salvarProjeto(p, dialog);
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

    private void carregarGerentes(JComboBox<Usuario> combo) {
        new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                return new UsuarioDAO().listarPorPerfil(PerfilUsuario.GERENTE);
            }

            @Override
            protected void done() {
                try {
                    List<Usuario> gerentes = get();
                    for (Usuario g : gerentes) {
                        combo.addItem(g);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
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

    private void salvarProjeto(Projeto projeto, JDialog dialog) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (projeto.getId() == null) {
                    projetoDAO.inserir(projeto, usuarioLogado.getId());
                } else {
                    projetoDAO.atualizar(projeto, usuarioLogado.getId());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(dialog, "Projeto salvo com sucesso!");
                    dialog.dispose();
                    carregarDados();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erro ao salvar: " + e.getMessage());
                }
            }
        }.execute();
    }
}
