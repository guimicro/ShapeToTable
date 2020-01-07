package shapetotable.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import shapetotable.database.ConnectionDBH2;
import shapetotable.domain.CadastroDiretorio;

/**
 *
 * @author Guilherme
 */
public class CadastroDiretorioDao {

    private final String INSERT = "INSERT INTO CDT_CAMINHOSHAPE (DESCRICAO,CAMINHO_SHAPE) VALUES (?,?)";
    private final String UPDATE = "UPDATE CDT_CAMINHOSHAPE SET DESCRICAO = ?,CAMINHO_SHAPE = ? WHERE IDCAMINHO = ?";
    private final String DELETE = "DELETE FROM CDT_CAMINHOSHAPE WHERE IDCAMINHO = ?";
    private final String LIST = "SELECT * FROM CDT_CAMINHOSHAPE";
    private final String SELECTBYID = "SELECT * FROM CDT_CAMINHOSHAPE WHERE IDCAMINHAO = ?";

    // METODO - INSERIR NA TABELA
    public void inserirCadDir(CadastroDiretorio cadDir) {
        if (cadDir != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(INSERT);

                pstm.setString(1, cadDir.getDescricao());
                pstm.setString(2, cadDir.getCaminhoShape());

                pstm.execute();

                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!", "Novo Registro", JOptionPane.PLAIN_MESSAGE);

                pstm.close();
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao inserir registro! -> " + e.getMessage());
            }
        } else {
            System.out.println("O parametro cadDir está vazio!");
        }
    }

    // METODO - LISTAR DADOS DA TABELA
    public List<CadastroDiretorio> getListarCadDir() {

        PreparedStatement pstm;
        ResultSet rs;
        ArrayList<CadastroDiretorio> listaCadDir = new ArrayList<>();
        ConnectionDBH2 con = new ConnectionDBH2();

        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(LIST);
            rs = pstm.executeQuery();

            while (rs.next()) {
                CadastroDiretorio cadDir = new CadastroDiretorio();
                cadDir.setIdCaminho(rs.getInt("IDCAMINHO"));
                cadDir.setDescricao(rs.getString("DESCRICAO"));
                cadDir.setCaminhoShape(rs.getString("CAMINHO_SHAPE"));

                listaCadDir.add(cadDir);
            }

            con.desconectaDBLocal(conexao);
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Shapes ->" + e.getMessage(), "Cadastro de Caminho", JOptionPane.PLAIN_MESSAGE);
        }
        return listaCadDir;
    }

    // METODO - BUSCAR DADOS POR ID
    public CadastroDiretorio getBuscarCadDir(int cadDir) {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        ConnectionDBH2 con = new ConnectionDBH2();
        CadastroDiretorio cadDiretorio = new CadastroDiretorio();

        try {

            Connection conexao = con.conectaDBLocal();
            pstm = conexao.prepareStatement(SELECTBYID);
            pstm.setInt(1, cadDir);
            rs = pstm.executeQuery();

            while (rs.next()) {
                cadDiretorio.setIdCaminho(rs.getInt("IDCAMINHO"));
                cadDiretorio.setDescricao(rs.getString("DESCRICAO"));
                cadDiretorio.setCaminhoShape(rs.getString("CAMINHO_SHAPE"));
            }

            con.desconectaDBLocal(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar Cadastro de Shapes ->" + e.getMessage(), "Cadastro de Diretório", JOptionPane.PLAIN_MESSAGE);
        }
        return cadDiretorio;
    }

    // METODO - ALTERAR REGISTRO DA TABELA
    public void atualizarCadDir(CadastroDiretorio cadDir) {
        if (cadDir != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(UPDATE);

                pstm.setString(1, cadDir.getDescricao());
                pstm.setString(2, cadDir.getCaminhoShape());
                pstm.setInt(3, cadDir.getIdCaminho());

                pstm.execute();

                JOptionPane.showMessageDialog(null, "Registro alterado com sucesso!", "Alteração de Caminho", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o registro! " + e.getMessage(), "Alteração de Caminho", JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("O parametro cadDir está vazio!");
        }
    }

    // METODO - REMOVER REGISTRO DA TABELA
    public void removerCadDir(CadastroDiretorio cadDir) {
        if (cadDir != null) {
            ConnectionDBH2 con = new ConnectionDBH2();

            try {

                Connection conexao = con.conectaDBLocal();
                PreparedStatement pstm;
                pstm = conexao.prepareStatement(DELETE);

                pstm.setInt(1, cadDir.getIdCaminho());

                pstm.execute();

                pstm.close();

                JOptionPane.showMessageDialog(null, "Registro removido com sucesso!", "Remoção de Caminho", JOptionPane.PLAIN_MESSAGE);
                con.desconectaDBLocal(conexao);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao excluir o registro! " + e.getMessage(), "Remoção de Caminho", JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            System.out.println("O parametro cadDir está vazio!");
        }
    }

}
