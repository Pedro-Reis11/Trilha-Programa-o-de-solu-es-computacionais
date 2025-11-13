package model;
import model.enums.StatusTarefa;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Tarefa {
    private Integer id;
    private String titulo;
    private String descricao;
    private Integer projetoId;
    private String projetoNome;
    private Integer responsavelId;
    private String responsavelNome;
    private StatusTarefa status;
    private LocalDate dataInicioPrevista;
    private LocalDate dataFimPrevista;
    private LocalDate dataInicioReal;
    private LocalDate dataFimReal;
    private LocalDateTime dataCadastro;

    // Construtores
    public Tarefa() {}

    public Tarefa(String titulo, String descricao, Integer projetoId, Integer responsavelId,
                  StatusTarefa status, LocalDate dataInicioPrevista, LocalDate dataFimPrevista) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.projetoId = projetoId;
        this.responsavelId = responsavelId;
        this.status = status;
        this.dataInicioPrevista = dataInicioPrevista;
        this.dataFimPrevista = dataFimPrevista;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getProjetoId() { return projetoId; }
    public void setProjetoId(Integer projetoId) { this.projetoId = projetoId; }

    public String getProjetoNome() { return projetoNome; }
    public void setProjetoNome(String projetoNome) { this.projetoNome = projetoNome; }

    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }

    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }

    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }

    public LocalDate getDataInicioPrevista() { return dataInicioPrevista; }
    public void setDataInicioPrevista(LocalDate dataInicioPrevista) {
        this.dataInicioPrevista = dataInicioPrevista;
    }

    public LocalDate getDataFimPrevista() { return dataFimPrevista; }
    public void setDataFimPrevista(LocalDate dataFimPrevista) {
        this.dataFimPrevista = dataFimPrevista;
    }

    public LocalDate getDataInicioReal() { return dataInicioReal; }
    public void setDataInicioReal(LocalDate dataInicioReal) {
        this.dataInicioReal = dataInicioReal;
    }

    public LocalDate getDataFimReal() { return dataFimReal; }
    public void setDataFimReal(LocalDate dataFimReal) {
        this.dataFimReal = dataFimReal;
    }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}

