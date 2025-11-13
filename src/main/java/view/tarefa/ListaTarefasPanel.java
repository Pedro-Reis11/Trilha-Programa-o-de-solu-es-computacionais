package view.tarefa;
import dao.TarefaDAO;
import dao.ProjetoDAO;
import dao.UsuarioDAO;
import model.Tarefa;
import model.Projeto;
import model.Usuario;
import model.enums.StatusTarefa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ListaTarefasPanel extends JPanel {
    private Usuario usuarioLogado;
    private JTable table;
    private DefaultTableModel tableModel;
    private TarefaDAO tarefaDAO;

    public ListaTarefasPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.tarefaDAO = new TarefaDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Minhas Tarefas");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnNova = new JButton("+ Nova Tarefa");
        btnNova.setBackground(new Color(46, 204, 113));
        btnNova.setForeground(Color.WHITE);
        btnNova.setFocusPainted(false);
        btnNova.addActionListener(e -> abrirCadastro(null));

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnNova, BorderLayout.EAST);

        String[] colunas = {"ID", "Título", "Projeto", "Responsável", "Status", "Prazo"};
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

        JButton btnIniciar = new JButton("Iniciar");
        btnIniciar.addActionListener(e -> alterarStatus(StatusTarefa.EM_EXECUCAO));

        JButton btnConcluir = new JButton("Concluir");
        btnConcluir.addActionListener(e -> alterarStatus(StatusTarefa.CONCLUIDA));

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarDados());

        buttonPanel.add(btnEditar);
        buttonPanel.add(btnIniciar);
        buttonPanel.add(btnConcluir);
        buttonPanel.add(btnAtualizar);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        tableModel.setRowCount(0);

        new SwingWorker<List<Tarefa>, Void>() {
            @Override
            protected List<Tarefa> doInBackground() throws Exception {
                return tarefaDAO.listarPorResponsavel(usuarioLogado.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Tarefa> tarefas = get();
                    for (Tarefa t : tarefas) {
                        tableModel.addRow(new Object[]{
                                t.getId(),
                                t.getTitulo(),
                                t.getProjetoNome(),
                                t.getResponsavelNome(),
                                t.getStatus().getDescricao(),
                                t.getDataFimPrevista()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaTarefasPanel.this,
                            "Erro ao carregar tarefas: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void editarSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);

        new SwingWorker<Tarefa, Void>() {
            @Override
            protected Tarefa doInBackground() throws Exception {
                return tarefaDAO.buscarPorId(id);
            }

            @Override
            protected void done() {
                try {
                    abrirCadastro(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaTarefasPanel.this,
                            "Erro: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void alterarStatus(StatusTarefa novoStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Tarefa tarefa = tarefaDAO.buscarPorId(id);
                tarefa.setStatus(novoStatus);

                if (novoStatus == StatusTarefa.EM_EXECUCAO && tarefa.getDataInicioReal() == null) {
                    tarefa.setDataInicioReal(java.time.LocalDate.now());
                } else if (novoStatus == StatusTarefa.CONCLUIDA) {
                    tarefa.setDataFimReal(java.time.LocalDate.now());
                }

                tarefaDAO.atualizar(tarefa, usuarioLogado.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(ListaTarefasPanel.this,
                            "Status atualizado com sucesso!");
                    carregarDados();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ListaTarefasPanel.this,
                            "Erro ao atualizar: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void abrirCadastro(Tarefa tarefa) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                tarefa == null ? "Nova Tarefa" : "Editar Tarefa",
                true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtTitulo = new JTextField(30);
        JTextArea txtDescricao = new JTextArea(5, 30);
        txtDescricao.setLineWrap(true);
        JComboBox<Projeto> cbProjeto = new JComboBox<>();
        JComboBox<Usuario> cbResponsavel = new JComboBox<>();
        JComboBox<StatusTarefa> cbStatus = new JComboBox<>(StatusTarefa.values());
        JSpinner spnDataInicio = new JSpinner(new SpinnerDateModel());
        JSpinner spnDataFim = new JSpinner(new SpinnerDateModel());

        carregarProjetos(cbProjeto);
        carregarColaboradores(cbResponsavel);

        if (tarefa != null) {
            txtTitulo.setText(tarefa.getTitulo());
            txtDescricao.setText(tarefa.getDescricao());
            cbStatus.setSelectedItem(tarefa.getStatus());
            spnDataInicio.setValue(Date.from(tarefa.getDataInicioPrevista()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            spnDataFim.setValue(Date.from(tarefa.getDataFimPrevista()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        int row = 0;
        adicionarCampo(panel, gbc, row++, "Título:", txtTitulo);
        adicionarCampo(panel, gbc, row++, "Descrição:", new JScrollPane(txtDescricao));
        adicionarCampo(panel, gbc, row++, "Projeto:", cbProjeto);
        adicionarCampo(panel, gbc, row++, "Responsável:", cbResponsavel);
        adicionarCampo(panel, gbc, row++, "Status:", cbStatus);
        adicionarCampo(panel, gbc, row++, "Data Início:", spnDataInicio);
        adicionarCampo(panel, gbc, row++, "Data Fim:", spnDataFim);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> {
            if (txtTitulo.getText().trim().isEmpty() || cbProjeto.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Preencha os campos obrigatórios!");
                return;
            }

            Tarefa t = tarefa != null ? tarefa : new Tarefa();
            t.setTitulo(txtTitulo.getText().trim());
            t.setDescricao(txtDescricao.getText().trim());
            t.setProjetoId(((Projeto) cbProjeto.getSelectedItem()).getId());
            t.setResponsavelId(((Usuario) cbResponsavel.getSelectedItem()).getId());
            t.setStatus((StatusTarefa) cbStatus.getSelectedItem());

            Date dataInicio = (Date) spnDataInicio.getValue();
            t.setDataInicioPrevista(dataInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            Date dataFim = (Date) spnDataFim.getValue();
            t.setDataFimPrevista(dataFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            salvarTarefa(t, dialog);
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

    private void carregarProjetos(JComboBox<Projeto> combo) {
        new SwingWorker<List<Projeto>, Void>() {
            @Override
            protected List<Projeto> doInBackground() throws Exception {
                return new ProjetoDAO().listarTodos();
            }

            @Override
            protected void done() {
                try {
                    for (Projeto p : get()) {
                        combo.addItem(p);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void carregarColaboradores(JComboBox<Usuario> combo) {
        new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                return new UsuarioDAO().listarTodos();
            }

            @Override
            protected void done() {
                try {
                    for (Usuario u : get()) {
                        combo.addItem(u);
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

    private void salvarTarefa(Tarefa tarefa, JDialog dialog) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (tarefa.getId() == null) {
                    tarefaDAO.inserir(tarefa, usuarioLogado.getId());
                } else {
                    tarefaDAO.atualizar(tarefa, usuarioLogado.getId());
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(dialog, "Tarefa salva com sucesso!");
                    dialog.dispose();
                    carregarDados();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog, "Erro: " + e.getMessage());
                }
            }
        }.execute();
    }
}
