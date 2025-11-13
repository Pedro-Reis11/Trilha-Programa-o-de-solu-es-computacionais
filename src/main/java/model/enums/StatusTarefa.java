package model.enums;
public enum StatusTarefa {
    PENDENTE("Pendente"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
