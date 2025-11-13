package model.enums;

public enum PerfilUsuario {
    ADMINISTRADOR("Administrador"),
    GERENTE("Gerente de Projeto"),
    COLABORADOR("Colaborador");

    private String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}