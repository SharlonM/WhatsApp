package com.sharlon.whatsapp.modelos;

public class Mensagens {

    private String idUsuario, mensagem, horario;

    public Mensagens() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String h) {
        this.horario = h;
    }
}
