package model.enums;
public enum StatusProjeto {
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private String descricao;

    StatusProjeto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

