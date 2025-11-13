package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Equipe {
    private Integer id;
    private String nome;
    private String descricao;
    private LocalDateTime dataCadastro;
    private List<Usuario> membros;

    // Construtores
    public Equipe() {
        this.membros = new ArrayList<>();
    }

    public Equipe(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public List<Usuario> getMembros() { return membros; }
    public void setMembros(List<Usuario> membros) { this.membros = membros; }

    @Override
    public String toString() {
        return nome;
    }
}
