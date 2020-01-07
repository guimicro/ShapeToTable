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
public class CadastroDiretorio {
    
    private int idCaminho;
    private String descricao;
    private String caminhoShape;    

    public CadastroDiretorio() {
    }

    public int getIdCaminho() {
        return idCaminho;
    }

    public void setIdCaminho(int idCaminho) {
        this.idCaminho = idCaminho;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoShape() {
        return caminhoShape;
    }

    public void setCaminhoShape(String caminhoShape) {
        this.caminhoShape = caminhoShape;
    }
    
}
