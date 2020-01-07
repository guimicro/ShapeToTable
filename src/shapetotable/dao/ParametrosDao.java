/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapetotable.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import shapetotable.database.ConnectionDBH2;
import shapetotable.domain.Parametros;

/**
 *
 * @author Guilherme
 */
public class ParametrosDao {
    
    private final String UPDATE = "UPDATE CFG_PARAMETROS SET  TABLE_NAME = ?, IDNAME = ?, SRID = ?, RECOMPILAR = ?, APPEND = ?";
    private final String LIST = "SELECT * FROM CFG_PARAMETROS";

    
    // METODO - LISTAR DADOS DA TABELA
    public List<Parametros> getListarParametros() {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<Parametros> listaCadParametros = new ArrayList<>();
        ConnectionDBH2 con = new ConnectionDBH2();

        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(LIST);
            rs = pstm.executeQuery();

            while (rs.next()) {
                Parametros cadParametros = new Parametros();
                cadParametros.setTableName(rs.getString("TABLE_NAME"));
                cadParametros.setIdName(rs.getString("IDNAME"));
                cadParametros.setSrid(rs.getInt("SRID"));
                cadParametros.setRecompilar(rs.getInt("RECOMPILAR"));
                cadParametros.setAppend(rs.getInt("APPEND"));

                listaCadParametros.add(cadParametros);
            }
            con.desconectaDBLocal(conexao);
        } catch (Exception e) {
           // JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Bancos ->" + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
        }
        return listaCadParametros;
    }
    
  
    // METODO - ALTERAR REGISTRO DA TABELA
    public void atualizarCadParametros(Parametros cadParametros) {
        if (cadParametros != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(UPDATE);

                pstm.setString(1, cadParametros.getTableName());
                pstm.setString(2, cadParametros.getIdName());
                pstm.setInt(3, cadParametros.getSrid());
                pstm.setInt(4, cadParametros.getRecompilar());
                pstm.setInt(5, cadParametros.getAppend());

                pstm.execute();

//                JOptionPane.showMessageDialog(null, "Registro alterado com sucesso!", "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o registro! " + e.getMessage(), "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("O parametro cadParametros está vazio!");
        }
    }
    
}
