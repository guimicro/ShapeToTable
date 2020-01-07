
package shapetotable.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import shapetotable.database.ConnectionDBH2;
import shapetotable.domain.CadastroBanco;

/**
 *
 * @author Guilherme
 */
public class CadastroBancoDao {
    
    private final String INSERT_COMUM = "INSERT INTO CDT_BANCO (NOMEDB,USUARIO,SENHA,HOST,SID,PORTA) VALUES (?,?,?,?,?,?)";
    private final String INSERT_TNS = "INSERT INTO CDT_BANCO (NOMEDB,USUARIO,SENHA,TNS) VALUES (?,?,?,?)";
    private final String UPDATE_COMUM = "UPDATE CDT_BANCO SET NOMEDB = ?,USUARIO = ?,SENHA = ?,HOST = ?,SID = ?,PORTA = ?,TNS = null WHERE IDDB = ?";
    private final String UPDATE_TNS = "UPDATE CDT_BANCO SET NOMEDB = ?,USUARIO = ?,SENHA = ?,TNS = ?,HOST = null,SID = null,PORTA = null WHERE IDDB = ?";
    private final String DELETE = "DELETE FROM CDT_BANCO WHERE IDDB = ?";
    private final String LISTALL = "SELECT * FROM CDT_BANCO";
    private final String SELECTBYID = "SELECT * FROM CDT_BANCO WHERE IDDB = ?";
    
    // METODO - INSERIR NA TABELA (INSERT_COMUM)
    public void inserirCadBancoComum(CadastroBanco cadBanco) {
        if (cadBanco != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(INSERT_COMUM);

                pstm.setString(1, cadBanco.getNomeConexao());
                pstm.setString(2, cadBanco.getUsuario());
                pstm.setString(3, cadBanco.getSenha());
                pstm.setString(4, cadBanco.getHost());
                pstm.setString(5, cadBanco.getSid());
                pstm.setInt(6, cadBanco.getPorta());

                pstm.execute();

                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Novo Registro", JOptionPane.PLAIN_MESSAGE);

                pstm.close();
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao inserir registro! -> " + e.getMessage());
            }
        } else {
            System.out.println("O parametro cadBanco está vazio!");
        }
    }
    
    // METODO - INSERIR NA TABELA (INSERT_TNS)
    public void inserirCadBancoTNS(CadastroBanco cadBanco) {
        if (cadBanco != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(INSERT_TNS);

                pstm.setString(1, cadBanco.getNomeConexao());
                pstm.setString(2, cadBanco.getUsuario());
                pstm.setString(3, cadBanco.getSenha());
                pstm.setString(4, cadBanco.getTNS());

                pstm.execute();

                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Novo Registro", JOptionPane.PLAIN_MESSAGE);

                pstm.close();
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao inserir registro! -> " + e.getMessage());
            }
        } else {
            System.out.println("O parametro cadBanco está vazio!");
        }
    }
    
    // METODO - LISTAR DADOS DA TABELA
    public List<CadastroBanco> getListarCadBanco() {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<CadastroBanco> listaCadBanco = new ArrayList<CadastroBanco>();
        ConnectionDBH2 con = new ConnectionDBH2();

        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(LISTALL);
            rs = pstm.executeQuery();

            while (rs.next()) {
                CadastroBanco cadBanco = new CadastroBanco();
                cadBanco.setIdBanco(rs.getInt("IDDB"));
                cadBanco.setNomeConexao(rs.getString("NOMEDB"));
                cadBanco.setUsuario(rs.getString("USUARIO"));
                cadBanco.setSenha(rs.getString("SENHA"));
                cadBanco.setTNS(rs.getString("TNS"));
                cadBanco.setHost(rs.getString("HOST"));
                cadBanco.setSid(rs.getString("SID"));
                cadBanco.setPorta(rs.getInt("PORTA"));

                listaCadBanco.add(cadBanco);
            }

            con.desconectaDBLocal(conexao);
        } catch (Exception e) {
           // JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Bancos ->" + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
        }
        return listaCadBanco;
    }
    
    // METODO - BUSCAR DADOS POR ID
    public CadastroBanco getBuscarCadBanco(int iddb) {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        ConnectionDBH2 con = new ConnectionDBH2();
        CadastroBanco cadBanco = new CadastroBanco();
        
        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(SELECTBYID);
            pstm.setInt(1, iddb);
            rs = pstm.executeQuery();
            
            while (rs.next()) {
                cadBanco.setIdBanco(rs.getInt("IDDB"));
                cadBanco.setNomeConexao(rs.getString("NOMEDB"));
                cadBanco.setUsuario(rs.getString("USUARIO"));
                cadBanco.setSenha(rs.getString("SENHA"));
                cadBanco.setTNS(rs.getString("TNS"));
                cadBanco.setHost(rs.getString("HOST"));
                cadBanco.setSid(rs.getString("SID"));
                cadBanco.setPorta(rs.getInt("PORTA"));
            }

            con.desconectaDBLocal(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Bancos ->" + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
        }
        return cadBanco;
    }
    
    // METODO - ALTERAR REGISTRO DA TABELA (COMUM)
    public void atualizarCadBancoComum(CadastroBanco cadBanco) {
        if (cadBanco != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(UPDATE_COMUM);

                pstm.setString(1, cadBanco.getNomeConexao());
                pstm.setString(2, cadBanco.getUsuario());
                pstm.setString(3, cadBanco.getSenha());
                pstm.setString(4, cadBanco.getHost());
                pstm.setString(5, cadBanco.getSid());
                pstm.setInt(6, cadBanco.getPorta());
                pstm.setInt(7, cadBanco.getIdBanco());

                pstm.execute();

                JOptionPane.showMessageDialog(null, "Registro alterado com sucesso!", "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o registro! " + e.getMessage(), "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("O parametro cadBanco está vazio!");
        }
    }
    
    // METODO - ALTERAR REGISTRO DA TABELA (TNS)
    public void atualizarCadBancoTNS(CadastroBanco cadBanco) {
        if (cadBanco != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(UPDATE_TNS);

                pstm.setString(1, cadBanco.getNomeConexao());
                pstm.setString(2, cadBanco.getUsuario());
                pstm.setString(3, cadBanco.getSenha());
                pstm.setString(4, cadBanco.getTNS());
                pstm.setInt(5, cadBanco.getIdBanco());

                pstm.execute(); System.out.println("update: " + pstm);

                JOptionPane.showMessageDialog(null, "Registro alterado com sucesso!", "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o registro! " + e.getMessage(), "Alteração de Banco", JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("O parametro cadBanco está vazio!");
        }
    }
    
    // METODO - REMOVER REGISTRO DA TABELA
    public void removerCadBanco(CadastroBanco cadBanco) {
        if (cadBanco != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(DELETE);
                
                pstm.setInt(1, cadBanco.getIdBanco());
                
                pstm.execute();
                
                pstm.close();
                
                JOptionPane.showMessageDialog(null, "Registro removido com sucesso!", "Remoção de Banco", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir o registro! " + e.getMessage(), "Remoção de Banco", JOptionPane.PLAIN_MESSAGE);
            } 
        } else {
            System.out.println("O parametro cadBanco está vazio!");
        }
    }
    
}
