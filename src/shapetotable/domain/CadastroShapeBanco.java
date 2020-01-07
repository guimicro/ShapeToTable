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
public class CadastroShapeBanco {

    private int idShpBanco;
    private int idcaminho;
    private int iddb;
    private String descricao;
    private String nomeDB;
    private boolean ativado;
    private String caminhoShape;
    private String tns;
    private String host;
    private String sid;
    private int porta;
    private String usuario;
    private String senha;

    public CadastroShapeBanco() {
    }

    public int getIdShpBanco() {
        return idShpBanco;
    }

    public void setIdShpBanco(int idShpBanco) {
        this.idShpBanco = idShpBanco;
    }

    public int getIdcaminho() {
        return idcaminho;
    }

    public void setIdcaminho(int idcaminho) {
        this.idcaminho = idcaminho;
    }

    public int getIddb() {
        return iddb;
    }

    public void setIddb(int iddb) {
        this.iddb = iddb;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String shape) {
        this.descricao = shape;
    }

    public String getNomeDB() {
        return nomeDB;
    }

    public void setNomeDB(String nomeDB) {
        this.nomeDB = nomeDB;
    }

    public boolean isAtivado() {
        return ativado;
    }

    public void setAtivado(boolean ativado) {
        this.ativado = ativado;
    }
    
    public String getCaminhoShape() {
        return caminhoShape;
    }

    public void setCaminhoShape(String caminhoShape) {
        this.caminhoShape = caminhoShape;
    }

    public String getTns() {
        return tns;
    }

    public void setTns(String tns) {
        this.tns = tns;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
