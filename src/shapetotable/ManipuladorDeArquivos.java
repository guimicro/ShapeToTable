/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapetotable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.DataFormat;

/**
 *
 * @author Guilherme
 */
public class ManipuladorDeArquivos {

    public String salvarLog(String texto) {

        Date dataAtual = new Date();
        String data = new SimpleDateFormat("yyyy-MM-dd").format(dataAtual);
        String hora = new SimpleDateFormat("HH-mm-ss").format(dataAtual);
        //String pathLog = "./Logs/";
        String arquivoLog = "Importacao " + data + "_" + hora + ".txt";
        
        File path = new File("./Logs/");
        File caminhoAbsoluto = new File(path,arquivoLog);
        try {

            if (!path.exists()) {
                path.mkdir(); 
            } else {
                caminhoAbsoluto.createNewFile();
            }
            
            
            FileWriter fw = new FileWriter(caminhoAbsoluto.getCanonicalFile());
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(texto);
            }
            

        } catch (IOException ex) {
            Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return String.valueOf(path);
    }

}
