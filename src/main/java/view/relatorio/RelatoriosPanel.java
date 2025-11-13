package view.relatorio;
import dao.ProjetoDAO;
import dao.TarefaDAO;
import model.Projeto;
import model.Usuario;
import model.enums.StatusTarefa;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RelatoriosPanel extends JPanel {
    private Usuario usuarioLogado;
    private JTextArea txtRelatorio;

    public RelatoriosPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initComponents();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Relatórios e Dashboards");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnDesempenho = new JButton("Desempenho");
        btnDesempenho.addActionListener(e -> gerarRelatorioDesempenho());

        JButton btnAtrasos = new JButton("Projetos com Risco");
        btnAtrasos.addActionListener(e -> gerarRelatorioAtrasos());

        JButton btnResumo = new JButton("Resumo Geral");
        btnResumo.addActionListener(e -> gerarResumoGeral());

        buttonPanel.add(btnDesempenho);
        buttonPanel.add(btnAtrasos);
        buttonPanel.add(btnResumo);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        txtRelatorio = new JTextArea();
        txtRelatorio.setEditable(false);
        txtRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtRelatorio);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        gerarResumoGeral();
    }

    private void gerarRelatorioDesempenho() {
        txtRelatorio.setText("Carregando relatório de desempenho...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                TarefaDAO tarefaDAO = new TarefaDAO();
                StringBuilder sb = new StringBuilder();

                sb.append("═══════════════════════════════════════════════════\n");
                sb.append("  RELATÓRIO DE DESEMPENHO - COLABORADOR\n");
                sb.append("═══════════════════════════════════════════════════\n\n");

                sb.append("Colaborador: ").append(usuarioLogado.getNomeCompleto()).append("\n");
                sb.append("Perfil: ").append(usuarioLogado.getPerfil().getDescricao()).append("\n\n");

                int pendentes = tarefaDAO.contarTarefasPorStatus(usuarioLogado.getId(), StatusTarefa.PENDENTE);
                int emExecucao = tarefaDAO.contarTarefasPorStatus(usuarioLogado.getId(), StatusTarefa.EM_EXECUCAO);
                int concluidas = tarefaDAO.contarTarefasPorStatus(usuarioLogado.getId(), StatusTarefa.CONCLUIDA);
                int total = pendentes + emExecucao + concluidas;

                sb.append("┌─ TAREFAS ────────────────────────────────────────┐\n");
                sb.append(String.format("│ Total de Tarefas: %28d │\n", total));
                sb.append(String.format("│ Pendentes: %35d │\n", pendentes));
                sb.append(String.format("│ Em Execução: %33d │\n", emExecucao));
                sb.append(String.format("│ Concluídas: %34d │\n", concluidas));

                if (total > 0) {
                    double taxaConclusao = (concluidas * 100.0) / total;
                    sb.append(String.format("│ Taxa de Conclusão: %27.1f%% │\n", taxaConclusao));
                }

                sb.append("└──────────────────────────────────────────────────┘\n");

                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    txtRelatorio.setText(get());
                } catch (Exception e) {
                    txtRelatorio.setText("Erro ao gerar relatório: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void gerarRelatorioAtrasos() {
        txtRelatorio.setText("Carregando projetos com risco de atraso...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                ProjetoDAO projetoDAO = new ProjetoDAO();
                List<Projeto> projetosRisco = projetoDAO.listarComRiscoAtraso();

                StringBuilder sb = new StringBuilder();
                sb.append("═══════════════════════════════════════════════════\n");
                sb.append("  PROJETOS COM RISCO DE ATRASO\n");
                sb.append("═══════════════════════════════════════════════════\n\n");

                if (projetosRisco.isEmpty()) {
                    sb.append("✓ Nenhum projeto com risco de atraso no momento!\n");
                } else {
                    sb.append(String.format("⚠ Total de projetos em risco: %d\n\n", projetosRisco.size()));

                    for (Projeto p : projetosRisco) {
                        sb.append("┌─────────────────────────────────────────────────\n");
                        sb.append("│ Projeto: ").append(p.getNome()).append("\n");
                        sb.append("│ Gerente: ").append(p.getGerenteNome()).append("\n");
                        sb.append("│ Prazo: ").append(p.getDataTerminoPrevista()).append("\n");
                        sb.append("│ Status: ").append(p.getStatus().getDescricao()).append("\n");
                        sb.append("└─────────────────────────────────────────────────\n\n");
                    }
                }

                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    txtRelatorio.setText(get());
                } catch (Exception e) {
                    txtRelatorio.setText("Erro ao gerar relatório: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void gerarResumoGeral() {
        txtRelatorio.setText("Carregando resumo geral...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                ProjetoDAO projetoDAO = new ProjetoDAO();
                TarefaDAO tarefaDAO = new TarefaDAO();

                List<Projeto> projetos = projetoDAO.listarTodos();

                StringBuilder sb = new StringBuilder();
                sb.append("═══════════════════════════════════════════════════\n");
                sb.append("  RESUMO GERAL DO SISTEMA\n");
                sb.append("═══════════════════════════════════════════════════\n\n");

                sb.append("┌─ PROJETOS ───────────────────────────────────────┐\n");
                sb.append(String.format("│ Total de Projetos: %27d │\n", projetos.size()));

                long planejados = projetos.stream().filter(p ->
                        p.getStatus().name().equals("PLANEJADO")).count();
                long emAndamento = projetos.stream().filter(p ->
                        p.getStatus().name().equals("EM_ANDAMENTO")).count();
                long concluidos = projetos.stream().filter(p ->
                        p.getStatus().name().equals("CONCLUIDO")).count();

                sb.append(String.format("│ Planejados: %34d │\n", planejados));
                sb.append(String.format("│ Em Andamento: %32d │\n", emAndamento));
                sb.append(String.format("│ Concluídos: %34d │\n", concluidos));
                sb.append("└──────────────────────────────────────────────────┘\n\n");

                sb.append("┌─ MINHAS TAREFAS ─────────────────────────────────┐\n");
                int minhasPendentes = tarefaDAO.contarTarefasPorStatus(
                        usuarioLogado.getId(), StatusTarefa.PENDENTE);
                int minhasExecucao = tarefaDAO.contarTarefasPorStatus(
                        usuarioLogado.getId(), StatusTarefa.EM_EXECUCAO);
                int minhasConcluidas = tarefaDAO.contarTarefasPorStatus(
                        usuarioLogado.getId(), StatusTarefa.CONCLUIDA);

                sb.append(String.format("│ Pendentes: %35d │\n", minhasPendentes));
                sb.append(String.format("│ Em Execução: %33d │\n", minhasExecucao));
                sb.append(String.format("│ Concluídas: %34d │\n", minhasConcluidas));
                sb.append("└──────────────────────────────────────────────────┘\n\n");

                List<Projeto> projetosRisco = projetoDAO.listarComRiscoAtraso();
                if (!projetosRisco.isEmpty()) {
                    sb.append("┌─ ALERTAS ────────────────────────────────────────┐\n");
                    sb.append(String.format("│ ⚠ %d projeto(s) com risco de atraso               │\n",
                            projetosRisco.size()));
                    sb.append("└──────────────────────────────────────────────────┘\n");
                }

                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    txtRelatorio.setText(get());
                } catch (Exception e) {
                    txtRelatorio.setText("Erro ao gerar resumo: " + e.getMessage());
                }
            }
        }.execute();
    }
}
