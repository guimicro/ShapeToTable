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
public class Parametros {
    
    private int idParametro;
    private String tableName;
    private String idName;
    private int srid;
    private int recompilar;
    private int append;

    public int getIdParametro() {
        return idParametro;
    }

    public void setIdParametro(int idParametro) {
        this.idParametro = idParametro;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public int getRecompilar() {
        return recompilar;
    }

    public void setRecompilar(int recompilar) {
        this.recompilar = recompilar;
    }

    public int getAppend() {
        return append;
    }

    public void setAppend(int append) {
        this.append = append;
    }
    
}
