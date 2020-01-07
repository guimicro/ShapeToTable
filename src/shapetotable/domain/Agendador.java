/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapetotable.domain;

/**
 *
 * @author Guilherme
 */
public class Agendador {
    
    private int idAgendador;
    private String horario;
    private boolean ativado;

    public int getIdAgendador() {
        return idAgendador;
    }

    public void setIdAgendador(int idAgendador) {
        this.idAgendador = idAgendador;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean isAtivado() {
        return ativado;
    }

    public void setAtivado(boolean ativado) {
        this.ativado = ativado;
    }
    
}
