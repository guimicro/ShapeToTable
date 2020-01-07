
package shapetotable.dao;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import shapetotable.database.ConnectionDBH2;
import shapetotable.domain.CadastroShapeBanco;

/**
 *
 * @author Guilherme
 */
public class CadastroShapeBancoDao {
    
    private final String INSERT = "INSERT INTO CDT_SHAPEBANCO (IDCAMINHO,IDDB) VALUES (?,?)";
    
    private final String UPDATE = "UPDATE CDT_SHAPEBANCO SET ATIVADO = ? WHERE IDSHPBANCO = ?";
    
    private final String DELETE = "DELETE FROM CDT_SHAPEBANCO WHERE IDSHPBANCO = ?";
    
    private final String LISTALL = "SELECT sb.IDSHPBANCO,sb.IDCAMINHO,sb.idDB, cam.DESCRICAO as DESCRICAO, ban.NOMEDB as NOMEDB, sb.ATIVADO  FROM CDT_SHAPEBANCO sb " +
                                   "INNER JOIN CDT_CAMINHOSHAPE cam ON cam.IDCAMINHO = sb.IDCAMINHO " +
                                   "INNER JOIN CDT_BANCO ban ON ban.IDDB = sb.IDDB ORDER BY sb.IDSHPBANCO asc"; 
    
    private final String LISTATIVADO = "SELECT sb.IDSHPBANCO,sb.IDCAMINHO,sb.idDB, cam.DESCRICAO as DESCRICAO, ban.NOMEDB as NOMEDB, sb.ATIVADO, " +
                                       "cam.caminho_shape as CAMINHO_SHAPE, ban.tns as TNS, ban.host as HOST, ban.sid as SID, ban.porta as PORTA, " +
                                       "ban.usuario as USUARIO, ban.senha as SENHA " +
                                       "FROM CDT_SHAPEBANCO sb " +
                                       "INNER JOIN CDT_CAMINHOSHAPE cam ON cam.IDCAMINHO = sb.IDCAMINHO " +
                                       "INNER JOIN CDT_BANCO ban ON ban.IDDB = sb.IDDB WHERE sb.ATIVADO IS TRUE ORDER BY sb.IDSHPBANCO asc";
    //private final String SELECTBYID = "SELECT * FROM CDT_SHAPEBANCO WHERE IDSHPBANCO = ?";
    
    // METODO - INSERIR NA TABELA
    public void inserirShapeBanco(CadastroShapeBanco cadShpBan) {
        if (cadShpBan != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(INSERT);

                pstm.setInt(1, cadShpBan.getIdcaminho());
                pstm.setInt(2, cadShpBan.getIddb());

                pstm.execute();

                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Novo Registro", JOptionPane.PLAIN_MESSAGE);

                pstm.close();
                con.desconectaDBLocal(conexao);

            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao inserir registro! -> " + e.getMessage());
            }
        } else {
            System.out.println("O parametro cadShpBan está vazio!");
        }
    }
    
    
    // METODO - LISTAR DADOS DA TABELA (LISTALL)
    public List<CadastroShapeBanco> getListarShpBan() {

        PreparedStatement pstm;
        ResultSet rs;
        ArrayList<CadastroShapeBanco> listaShpBan = new ArrayList<>();
        ConnectionDBH2 con = new ConnectionDBH2();

        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(LISTALL);
            rs = pstm.executeQuery();

            while (rs.next()) {
                CadastroShapeBanco cadShpBan = new CadastroShapeBanco();
                cadShpBan.setIdShpBanco(rs.getInt("IDSHPBANCO"));
                cadShpBan.setIdcaminho(rs.getInt("IDCAMINHO"));
                cadShpBan.setIddb(rs.getInt("IDDB"));
                cadShpBan.setDescricao(rs.getString("DESCRICAO"));
                cadShpBan.setNomeDB(rs.getString("NOMEDB"));
                cadShpBan.setAtivado(rs.getBoolean("ATIVADO"));

                listaShpBan.add(cadShpBan);
            }

            con.desconectaDBLocal(conexao);
        } catch (SQLException e) {
           // JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Bancos ->" + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
        }
        return listaShpBan;
    }
    
    // METODO - LISTAR DADOS DA TABELA (LISTATIVADO)
    public List<CadastroShapeBanco> getListarShpBanAtivado() {

        PreparedStatement pstm;
        ResultSet rs;
        ArrayList<CadastroShapeBanco> listaShpBan = new ArrayList<>();
        ConnectionDBH2 con = new ConnectionDBH2();

        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(LISTATIVADO);
            rs = pstm.executeQuery();

            while (rs.next()) {
                CadastroShapeBanco cadShpBan = new CadastroShapeBanco();
                cadShpBan.setIdShpBanco(rs.getInt("IDSHPBANCO"));
                cadShpBan.setIdcaminho(rs.getInt("IDCAMINHO"));
                cadShpBan.setIddb(rs.getInt("IDDB"));
                cadShpBan.setDescricao(rs.getString("DESCRICAO"));
                cadShpBan.setNomeDB(rs.getString("NOMEDB"));
                cadShpBan.setAtivado(rs.getBoolean("ATIVADO"));
                cadShpBan.setCaminhoShape(rs.getString("CAMINHO_SHAPE"));
                cadShpBan.setTns(rs.getString("TNS"));
                cadShpBan.setHost(rs.getString("HOST"));
                cadShpBan.setSid(rs.getString("SID"));
                cadShpBan.setPorta(rs.getInt("PORTA"));
                cadShpBan.setUsuario(rs.getString("USUARIO"));
                cadShpBan.setSenha(rs.getString("SENHA"));

                listaShpBan.add(cadShpBan); 
//                System.out.println("Select: \n" + pstm);
            }

            con.desconectaDBLocal(conexao);
        } catch (SQLException e) {
           // JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Bancos ->" + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
        }
        return listaShpBan;
    }
//    
//    // METODO - BUSCAR DADOS POR ID
//    public CadastroBanco getBuscarCadBanco(int iddb) {
//
//        PreparedStatement pstm = null;
//        ResultSet rs = null;
//        ConnectionDBH2 con = new ConnectionDBH2();
//        CadastroBanco cadBanco = new CadastroBanco();
//        
//        try {
//
//            Connection conexao = con.conectaDBLocal();
//            pstm = conexao.prepareStatement(SELECTBYID);
//            pstm.setInt(1, iddb);
//            rs = pstm.executeQuery();
//            
//            while (rs.next()) {
//                cadBanco.setIdBanco(rs.getInt("IDDB"));
//                cadBanco.setNomeConexao(rs.getString("NOMEDB"));
//                cadBanco.setUsuario(rs.getString("USUARIO"));
//                cadBanco.setSenha(rs.getString("SENHA"));
//                cadBanco.setTNS(rs.getString("TNS"));
//                cadBanco.setHost(rs.getString("HOST"));
//                cadBanco.setSid(rs.getString("SID"));
//                cadBanco.setPorta(rs.getInt("PORTA"));
//            }
//
//            con.desconectaDBLocal(conexao);
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Bancos ->" + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
//        }
//        return cadBanco;
//    }
//    
    
    // METODO - ALTERAR REGISTRO DA TABELA
    public void atualizarCadShpBan(CadastroShapeBanco cadShpBan) {
        if (cadShpBan != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(UPDATE);

                pstm.setBoolean(1, cadShpBan.isAtivado());
                pstm.setInt(2, cadShpBan.getIdShpBanco());

                pstm.execute();// System.out.println("update: " + pstm);

                //JOptionPane.showMessageDialog(null, "Registro alterado com sucesso!", "Alteração de Importação", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o registro! " + e.getMessage(), "Alteração de Importação", JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("O parametro cadShpBan está vazio!");
        }
    }
    
    // METODO - REMOVER REGISTRO DA TABELA
    public void removerCadShpBan(CadastroShapeBanco cadShpBan) {
        if (cadShpBan != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(DELETE);
                
                pstm.setInt(1, cadShpBan.getIdShpBanco());
                
                pstm.execute();
                pstm.close();
                
                JOptionPane.showMessageDialog(null, "Registro removido com sucesso!", "Remoção da Lista de Importação", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir o registro! " + e.getMessage(), "Remoção da Lista de Importação", JOptionPane.PLAIN_MESSAGE);
            } 
        } else {
            System.out.println("O parametro cadShpBan está vazio!");
        }
    }
    
}
