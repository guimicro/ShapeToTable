package shapetotable.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class ConnectionDBH2 {

    String url = "jdbc:h2:./db/BancoH2";
    String username = "shapetotable";
    String password = "shapetotable";

    private Connection conn = null;

    public Connection conectaDBLocal() {
        try {
            if (conn == null || conn.isClosed()) {

                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(url, username, password);
                System.out.println("Conectou ao H2");
            }
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Cadastro de Banco", JOptionPane.PLAIN_MESSAGE);
            System.out.println(e.toString());
        }
        return conn;
    }

    public final void desconectaDBLocal(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Encerrou conexão com H2");
            } catch (SQLException e) {
            }
        }
    }

    public void ValidaSeBancoExiste() throws SQLException {

        try {
            Class.forName("org.h2.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:h2:./db/BancoH2", "shapetotable", "shapetotable")) {
                Statement stat = conn.createStatement();

                try {
                    ResultSet rs;
                    rs = stat.executeQuery("select * from CDT_CAMINHOSHAPE");
                    System.out.println("Banco local H2 válido, iniciando execução!");

                    stat.close();
                    conn.close();

                } catch (SQLException ex) {

                    System.out.println("Criando banco local H2!");
                    JOptionPane.showMessageDialog(null, "Novo banco local H2 criado", "Shape To Table - SGIB", JOptionPane.PLAIN_MESSAGE);

                    stat.execute("create table CDT_CAMINHOSHAPE("
                            + "IDCAMINHO identity,"
                            + "DESCRICAO varchar(30),"
                            + "CAMINHO_SHAPE varchar(300))");

                    stat.execute("create table CDT_BANCO("
                            + "IDDB identity,"
                            + "NOMEDB varchar(30),"
                            + "TNS varchar(500),"
                            + "USUARIO varchar(30),"
                            + "SENHA varchar(30),"
                            + "HOST varchar(30),"
                            + "SID varchar(10),"
                            + "PORTA number)");
                    
                    stat.execute("create table CDT_SHAPEBANCO("
                            + "IDSHPBANCO identity,"
                            + "IDCAMINHO number,"
                            + "IDDB number,"
                            + "ATIVADO boolean default true)");
                    
                    stat.execute("create table CFG_PARAMETROS("
                            + "IDPARAMETRO identity,"
                            + "TABLE_NAME varchar(20),"
                            + "IDNAME varchar(20),"
                            + "SRID number,"
                            + "RECOMPILAR number,"
                            + "APPEND number)");
                    
                    stat.execute("create table CFG_AGENDADOR("
                            + "IDAGENDADOR identity,"
                            + "HORARIO varchar(10),"
                            + "ATIVADO boolean default false)");
                    
                    stat.executeUpdate("INSERT INTO CFG_PARAMETROS ("
                            + "TABLE_NAME, IDNAME, SRID, RECOMPILAR, APPEND) "
                            + "VALUES ('GEO_LAYER', 'ID', 8307, 1, 0)");
                    
                    stat.executeUpdate("INSERT INTO CFG_AGENDADOR ("
                            + "HORARIO, ATIVADO) "
                            + "VALUES ('00:00:00',FALSE)");

                    System.out.println("Novo Banco H2 foi criado");

                    stat.close();
                    conn.close();

                }
            }
        } catch (ClassNotFoundException ex) {

        }
    }

}
