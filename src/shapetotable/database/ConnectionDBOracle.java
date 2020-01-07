package shapetotable.database;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectionDBOracle {

    String url;
    String username;
    String password;


    private Connection conn = null;

    public void setParametrosConexaoComum(String host, String sid, int porta, String username, String password){
        this.username = username.toUpperCase();
        this.password = password;
        this.url = "jdbc:oracle:thin:@" + host.toUpperCase() + ":" + String.valueOf(porta) + ":" + sid.toUpperCase();
    }
    
    public void setParametrosConexaoTNS(String tns, String username, String password){
        this.username = username.toUpperCase();
        this.password = password;
        this.url = "jdbc:oracle:thin:@" + tns.toUpperCase();
    }
    
    public Connection getConnection() {
        try {

            if(conn==null || conn.isClosed()){
                Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
//                System.out.println("url: " + url);
//                System.out.println("user :" + username);
//                System.out.println("senha :" + password);
                conn = DriverManager.getConnection(url, username, password);   
                System.out.println("------------------------------\nClasse ConnectionDBOrale -> Abriu conexão:\n" + this.url.trim() + "\nUser: " + 
                        this.username + " ,Senha: " + this.password + "\n------------------------------");
            }      

        } catch (Exception e) {
            System.out.println(e.toString());
//            JOptionPane.showMessageDialog(null, "Problema ao conectar com"
//                    + " DB_Oracle configurado!\n" + e.getMessage() + "\nRevise os parâmetros de Conexão", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        } 
        return conn;
    }

    /**
     * Fecha a resultset utilizado, o statement que o criou e a connection que a criou.
    * @param resultSet ResultSet que será fechado
     * @throws java.sql.SQLException
    */
    public final void closeAll(ResultSet resultSet) throws SQLException{

        if(resultSet != null){
            try{
                closeAll((ResultSet) resultSet.getStatement());
            } catch(SQLException e){}

            try{
                resultSet.close();
            } catch(SQLException e){}
        }
    }

    /**
     * Fecha o statement utilizado e a connection que a criou.
     * @param statement Statement que será fechado
     * @throws java.sql.SQLException
     * @throws DAOException
     */
    public final void closeAll(Statement statement) throws SQLException{
        if(statement != null){
            try{
                closeAll(getConnection());
            } catch(SQLException e){}
        }
    }

    public final void closeAll(Connection connection) throws SQLException{
        if(connection != null){
            try{
                connection.close();
                System.out.println("Fechou conexão");
            } catch(SQLException e){}
        }
    }
    
}


