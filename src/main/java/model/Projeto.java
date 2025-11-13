package model;
import model.enums.StatusProjeto;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Projeto {
    private Integer id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTerminoPrevista;
    private LocalDate dataTerminoReal;
    private StatusProjeto status;
    private Integer gerenteId;
    private String gerenteNome;
    private LocalDateTime dataCadastro;

    // Construtores
    public Projeto() {}

    public Projeto(String nome, String descricao, LocalDate dataInicio,
                   LocalDate dataTerminoPrevista, StatusProjeto status, Integer gerenteId) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.status = status;
        this.gerenteId = gerenteId;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataTerminoPrevista() { return dataTerminoPrevista; }
    public void setDataTerminoPrevista(LocalDate dataTerminoPrevista) {
        this.dataTerminoPrevista = dataTerminoPrevista;
    }

    public LocalDate getDataTerminoReal() { return dataTerminoReal; }
    public void setDataTerminoReal(LocalDate dataTerminoReal) {
        this.dataTerminoReal = dataTerminoReal;
    }

    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) { this.status = status; }

    public Integer getGerenteId() { return gerenteId; }
    public void setGerenteId(Integer gerenteId) { this.gerenteId = gerenteId; }

    public String getGerenteNome() { return gerenteNome; }
    public void setGerenteNome(String gerenteNome) { this.gerenteNome = gerenteNome; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() {
        return nome;
    }
}