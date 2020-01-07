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
import shapetotable.domain.Agendador;

/**
 *
 * @author Guilherme
 */
public class AgendadorDao {
    
    private final String UPDATE = "UPDATE CFG_AGENDADOR SET HORARIO = ?,ATIVADO = ?";
    private final String LIST = "SELECT * FROM CFG_AGENDADOR";

    
    // METODO - LISTAR DADOS DA TABELA
    public List<Agendador> getListarAgendador() {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<Agendador> listaCadAgendamento = new ArrayList<Agendador>();
        ConnectionDBH2 con = new ConnectionDBH2();

        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(LIST);
            rs = pstm.executeQuery();

            while (rs.next()) {
                Agendador cadAgendador = new Agendador();
                cadAgendador.setHorario(rs.getString("HORARIO"));
                cadAgendador.setAtivado(rs.getBoolean("ATIVADO"));

                listaCadAgendamento.add(cadAgendador);
            }

            con.desconectaDBLocal(conexao);
        } catch (Exception e) {
           // JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Bancos ->" + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
        }
        return listaCadAgendamento;
    }
    
  
    // METODO - ALTERAR REGISTRO DA TABELA
    public void atualizarCadAgendador(Agendador cadAgendador) {
        if (cadAgendador != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(UPDATE);

                pstm.setString(1, cadAgendador.getHorario());
                pstm.setBoolean(2, cadAgendador.isAtivado());

                pstm.execute();

//                JOptionPane.showMessageDialog(null, "Registro alterado com sucesso!", "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o registro! " + e.getMessage(), "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("O parametro cadAgendador está vazio!");
        }
    }
    
}
