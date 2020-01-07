/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapetotable;

import java.sql.SQLException;
import shapetotable.database.ConnectionDBH2;
import shapetotable.view.ShapeToTableView;

/**
 *
 * @author Guilherme
 */
public class ShapeToTable {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        // TODO code application logic here
        
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        ConnectionDBH2 conexao = new ConnectionDBH2();
        conexao.ValidaSeBancoExiste();
        
        ShapeToTableView scene = new ShapeToTableView();
        scene.setLocationRelativeTo(null);
        scene.setVisible(true);
    }
    
}
