package shapetotable.database;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionDBOracleBoneCP {

    String url;
    String username;
    String password;

    private Connection conn = null;
    private BoneCP connectionPool = null;

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

    public Connection getConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        try {

            if (conn == null || conn.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            }
            BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(url);
            config.setUser(username);
            config.setPassword(password);
            config.setMinConnectionsPerPartition(1);
            config.setMaxConnectionsPerPartition(1);
            config.setPartitionCount(1);

            connectionPool = new BoneCP(config);
            conn = connectionPool.getConnection();

//                conn.setAutoCommit(false);
            System.out.println("Abriu conexão");

        } catch (SQLException e) {
            e.getMessage();
        }
        return conn;
    }

    /**
     * Fecha a resultset utilizado, o statement que o criou e a connection que a
     * criou.
     *
     * @param resultSet ResultSet que será fechado
     * @throws java.sql.SQLException
     */
    public final void closeAll(ResultSet resultSet) throws SQLException {

        if (resultSet != null) {
            try {
                closeAll((ResultSet) resultSet.getStatement());
            } catch (SQLException e) {
            }

            try {
                resultSet.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * Fecha o statement utilizado e a connection que a criou.
     *
     * @param statement Statement que será fechado
     * @throws java.sql.SQLException
     * @throws DAOException
     */
    public final void closeAll(Statement statement) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (statement != null) {
            try {
                closeAll(getConnection());
            } catch (SQLException e) {
            }
        }
    }

    public final void closeAll(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connectionPool.shutdown();
                connection.close();
                System.out.println("Fechou conexão");
            } catch (SQLException e) {
            }
        }
    }

}
