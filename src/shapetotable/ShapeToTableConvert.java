package shapetotable;

import java.util.StringTokenizer;
import oracle.spatial.util.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JOptionPane;
import oracle.sql.STRUCT;
import oracle.jdbc.driver.*;
import oracle.jdbc.OracleConnection;
import oracle.spatial.geometry.JGeometry;
import shapetotable.view.ShapeToTableView;
import shapetotable.view.StatusConversaoView;

public class ShapeToTableConvert {

    private static String m_tableName = null;
    private static String m_shapefileName = null;
    private static String m_idName = null;
    private static int m_srid = 0;
    private static String geomMetaDataTable = "user_sdo_geom_metadata";
    private static String m_geom = "geometry";
    private static String min_x = "-180";
    private static String min_y = "-90";
    private static String max_x = "180";
    private static String max_y = "90";
    private static String m_tolerance = "0.05";
    private static String mg_tolerance = "0.000000005";
    private static int m_start_id = 1;
    private static int m_commit_interval = -1;
    private static String dimArray = null;
    private static String dimArrayMig = null;
    private static boolean defaultX = true;
    private static boolean defaultY = true;
    private static int skip_create_table = 0;
    private static int recompilaFazenda = 0;

    Connection conn = null;
    ArrayList<String> logs = new ArrayList<>(); //criado para retornar os logs para a tela principal
    int error_cnt;
    int numRecords;

    public static void main(String args[])
            throws Exception {
    }//end Main[]   

    public ArrayList<String> converter() throws IOException, SQLException, Exception {

        // Open dbf and input files
        DBFReaderJGeom dbfr = new DBFReaderJGeom(m_shapefileName);
        //System.out.println("ShapeFile name: " + m_shapefileName);
        logs.clear();
        logs.add("\r\n\n-> CAMINHO SHAPE: " + m_shapefileName);

        ShapefileReaderJGeom sfh = new ShapefileReaderJGeom(m_shapefileName);
        ShapefileFeatureJGeom sf = new ShapefileFeatureJGeom();
        
        // Get type, measure and Z info from shapefile
        int shpFileType = sfh.getShpFileType();
        double minMeasure = sfh.getMinMeasure();
        double maxMeasure = sfh.getMaxMeasure();
        if (maxMeasure <= -10E38) {
            maxMeasure = Double.NaN;
        }
        double minZ = sfh.getMinZ();
        double maxZ = sfh.getMaxZ();
        //Get X,Y extents if srid is not geodetic
        if (defaultX && m_srid != 0) {
            PreparedStatement psSrid = conn.prepareStatement("SELECT COUNT(*) cnt FROM MDSYS.GEODETIC_SRIDS WHERE srid = ?");
            psSrid.setInt(1, m_srid);
            ResultSet rs = psSrid.executeQuery();
            if (rs.next()) {
                if (rs.getInt("cnt") == 0) {
                    min_x = String.valueOf(sfh.getMinX());
                    max_x = String.valueOf(sfh.getMaxX());
                    //System.out.println("X: " + min_x +", "+ max_x);
                }
            }
            psSrid.close();
        }
        if (defaultY && m_srid != 0) {
            PreparedStatement psSrid = conn.prepareStatement("SELECT COUNT(*) cnt FROM MDSYS.GEODETIC_SRIDS WHERE srid = ?");
            psSrid.setInt(1, m_srid);
            ResultSet rs = psSrid.executeQuery();
            if (rs.next()) {
                if (rs.getInt("cnt") == 0) {
                    min_y = String.valueOf(sfh.getMinY());
                    max_y = String.valueOf(sfh.getMaxY());
                    //System.out.println("Y: " + min_y +", "+ max_y);
                }
            }
            psSrid.close();
        }   
        
        //Get dimension of shapefile   
        int shpDims = sfh.getShpDims(shpFileType, maxMeasure);
        
        // Construct dimArrarys
        if (shpDims == 2 || shpDims == 0) {
            dimArray = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + m_tolerance + "))";
            dimArrayMig = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + mg_tolerance + "))";
        } else if (shpDims == 3 && Double.isNaN(maxMeasure)) {
            dimArray = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Z', " + minZ + ", " + maxZ + ", " + m_tolerance + "))";
            dimArrayMig = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Z', " + minZ + ", " + maxZ + ", " + mg_tolerance + "))";
        } else if (shpDims == 3) {
            dimArray = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('M', " + minMeasure + ", " + maxMeasure + ", " + m_tolerance + "))";
            dimArrayMig = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('M', " + minMeasure + ", " + maxMeasure + ", " + mg_tolerance + "))";
        } else if (shpDims == 4) {
            dimArray = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Z', " + minZ + ", " + maxZ + ", " + m_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('M', " + minMeasure + ", " + maxMeasure + ", " + m_tolerance + "))";
            dimArrayMig = "MDSYS.SDO_DIM_ARRAY(MDSYS.SDO_DIM_ELEMENT('X', " + min_x + ", " + max_x + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Y', " + min_y + ", " + max_y + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('Z', " + minZ + ", " + maxZ + ", " + mg_tolerance + "), "
                    + "MDSYS.SDO_DIM_ELEMENT('M', " + minMeasure + ", " + maxMeasure + ", " + mg_tolerance + "))";
        }
        
        // Call create table   
        if (skip_create_table == 0) {
            prepareTableForData(conn, dbfr, sf, sfh);
        } else {
            System.out.println("Appending to existing table\n");
            logs.add("\r\n\n-> INCREMENTANDO DADOS NA TABELA ATUAL");
        }

        ////////////////////////////////////////////////////////////////////////////
        // Conversion from Feature to DB
        ////////////////////////////////////////////////////////////////////////////
        error_cnt = 0;
        int numFields = dbfr.numFields();
        numRecords = dbfr.numRecords();
        logs.add("\r\n\n-> QUANTIDADE DE REGISTROS À CONVERTER DO ARQUIVO SHAPE: " + String.valueOf(numRecords));
        byte[] fieldTypes = new byte[numFields];
        for (int field = 0; field < numFields; field++) {
            fieldTypes[field] = dbfr.getFieldType(field);
            System.out.println("Campos: " + dbfr.getFieldName(field));
            logs.add("\r\n [COLUNA" + (field + 1) + ": " + dbfr.getFieldName(field) + "]");
        } //usar isso para mostrar as colunas do shape para o usuário
        Hashtable ht = null;
        // Get first feature record to determine num of columns
        ht = sf.fromRecordToFeature(dbfr, sfh, fieldTypes, numFields, 0, m_srid);
        // Num of columns
        int val = ht.size();
        String params = null;
        String paramsM = null;
        if (m_idName == null) {
            params = "(";
        } else {
            params = "(?,";
        }
        for (int i = 0; i < val; i++) {
            if (i == 0) {
                params = params + " ?";
            } else {
                params = params + ", ?";
            }
        }
        params = params + ")";
        paramsM = params.substring(0, (params.length() - 2)) + "MDSYS.SDO_MIGRATE.TO_CURRENT(?, " + dimArrayMig + "))";
        String[] colNames = sf.getOraFieldNames(dbfr, fieldTypes, numFields);
        // Create prepared statements
        String insertRec = "INSERT INTO " + m_tableName + " VALUES" + params;
        PreparedStatement ps = conn.prepareStatement(insertRec);
        PreparedStatement psCom = conn.prepareStatement("COMMIT");
        String insertMig = "INSERT INTO " + m_tableName + " VALUES" + paramsM;
        PreparedStatement psMig = conn.prepareStatement(insertMig);
        //ResultSet resMig = null;
        STRUCT str = null;

        for (int i = 0; i < numRecords; i++) {

            //Edit to adjust, or comment to remove screen output; default 10
            if ((i + 1) % 100 == 0) {
                //System.out.println("Converting record #" + (i + 1));
            }
            //////////////////////////////////////////////////////////////////////////////

            ht = sf.fromRecordToFeature(dbfr, sfh, fieldTypes, numFields, i, m_srid);

            if (m_idName == null) {
                try {
                    // Migrate geometry if polygon, polygonz, or polygonm
                    if (shpFileType == 5 || shpFileType == 15 || shpFileType == 25) {
                        for (int j = 0; j < colNames.length; j++) {
                            if ((ht.get(colNames[j]) instanceof String)) {
                                psMig.setString((j + 1), (String) ht.get(colNames[j]));
                            } else if ((ht.get(colNames[j]) instanceof Integer)) {
                                psMig.setInt((j + 1), ((Integer) ht.get(colNames[j])).intValue());
                            } else if ((ht.get(colNames[j]) instanceof Double)) {
                                psMig.setDouble((j + 1), ((Double) ht.get(colNames[j])).doubleValue());
                            } else {
                                throw new RuntimeException("Unsupported Column Type");
                            }
                        }//end for_colNames
                        str = JGeometry.store(conn, (JGeometry) ht.get("geometry"));
                        psMig.setObject((colNames.length + 1), str);
                        psMig.executeUpdate();
                    } else {
                        for (int j = 0; j < colNames.length; j++) {
                            if ((ht.get(colNames[j]) instanceof String)) {
                                ps.setString((j + 1), (String) ht.get(colNames[j]));
                            } else if ((ht.get(colNames[j]) instanceof Integer)) {
                                ps.setInt((j + 1), ((Integer) ht.get(colNames[j])).intValue());
                            } else if ((ht.get(colNames[j]) instanceof Double)) {
                                ps.setDouble((j + 1), ((Double) ht.get(colNames[j])).doubleValue());
                            } else {
                                throw new RuntimeException("Unsupported Column Type");
                            }
                        }//end for_colNames
                        str = JGeometry.store(conn, (JGeometry) ht.get("geometry"));
                        ps.setObject((colNames.length + 1), str);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    error_cnt = error_cnt + 1;
                    System.out.println(e + "\nRecord #" + (i + 1) + " not converted 1.");
                    logs.add("\r\nREGISTRO #" + (i + 1) + "NÃO CONVERTIDO");
                }
            }//if_m_idName
            else {
                int id = i + m_start_id;
                try {
                    // Migrate geometry if polygon, polygonz, or polygonm
                    if (shpFileType == 5 || shpFileType == 15 || shpFileType == 25) {
                        psMig.setInt(1, id);
                        for (int j = 0; j < colNames.length; j++) {
                            if ((ht.get(colNames[j]) instanceof String)) {
                                psMig.setString((j + 2), (String) ht.get(colNames[j]));
                            } else if ((ht.get(colNames[j]) instanceof Integer)) {
                                psMig.setInt((j + 2), ((Integer) ht.get(colNames[j])).intValue());
                            } else if ((ht.get(colNames[j]) instanceof Double)) {
                                psMig.setDouble((j + 2), ((Double) ht.get(colNames[j])).doubleValue());
                            } else {
                                throw new RuntimeException("Unsupported Column Type");
                            }
                        }//end for_colNames
                        str = JGeometry.store(conn, (JGeometry) ht.get("geometry"));
                        psMig.setObject((colNames.length + 2), str);
                        psMig.executeUpdate();
                    } else {
                        ps.setInt(1, id);
                        for (int j = 0; j < colNames.length; j++) {
                            if ((ht.get(colNames[j]) instanceof String)) {
                                ps.setString((j + 2), (String) ht.get(colNames[j]));
                            } else if ((ht.get(colNames[j]) instanceof Integer)) {
                                ps.setInt((j + 2), ((Integer) ht.get(colNames[j])).intValue());
                            } else if ((ht.get(colNames[j]) instanceof Double)) {
                                ps.setDouble((j + 2), ((Double) ht.get(colNames[j])).doubleValue());
                            } else {
                                throw new RuntimeException("Unsupported Column Type");
                            }
                        }//end for_colNames
                        str = JGeometry.store(conn, (JGeometry) ht.get("geometry"));
                        ps.setObject((colNames.length + 2), str);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    error_cnt = error_cnt + 1;
                    System.out.println(e + "\nRecord #" + (i + 1) + " not converted 2.");
                    logs.add("\r\nREGISTRO #" + (i + 1) + "NÃO CONVERTIDO");
                }
            }

            //Edit to adjust, or comment to remove COMMIT interval; default 1000
            if (m_commit_interval == -1) {
                if ((i + 1) % 2000 == 0) {
                    conn.commit();
                }
            } else {
                if ((i + 1) % m_commit_interval == 0) {
                    conn.commit();
                }
            }
            /////////////////////////////////////////////////////////////////////////////

        }//end_for_each_record   

        if (skip_create_table == 0) {

            try {
                String updateIndex;
                Statement stmt = conn.createStatement();
                updateIndex = "CREATE INDEX " + m_tableName + "_MB_IDX ON " + m_tableName + " (GEOMETRY) INDEXTYPE IS MDSYS.SPATIAL_INDEX";
                stmt.executeUpdate(updateIndex);
                stmt.close();
            } catch (SQLException exF) {
                System.out.println(exF);
            }
        }

        if (recompilaFazenda == 1) {
            try {
                String updateFunction;
                String selectFunction;
                Statement stmt = conn.createStatement();

                updateFunction = "ALTER FUNCTION fnc_localiza_faz COMPILE";
                stmt.executeUpdate(updateFunction);
                selectFunction = "SELECT fnc_localiza_faz(0.0,0.0) as FAZ FROM DUAL";
                stmt.executeUpdate(selectFunction);
                stmt.close();

                logs.add("\r\n\n-> FUNÇÃO 'FNC_LOCALIZA_FAZ' FOI RECOMPILADA COM SUCESSO!");
            } catch (SQLException exF) {
                System.out.println(exF);
                logs.add("\r\n\n-> FUNÇÃO 'FNC_LOCALIZA_FAZ' NÃO RECOMPILADA!\n [" + exF.getMessage().trim() + "]");
            }
        }

        conn.commit();
        dbfr.closeDBF();
        sfh.closeShapefile();
        ps.close();
        psMig.close();
        psCom.close();
        //janela.dispose();
        //conn.close();

        if (error_cnt > 0) {
            System.out.println(error_cnt + " record(s) not converted.");
            logs.add("\r\n\nTOTAL DE REGISTROS NÃO IMPORTADOS: " + error_cnt);
        }

        System.out.println((numRecords - error_cnt) + " record(s) converted.");
        logs.add("\r\n\nTOTAL DE REGISTROS IMPORTADOS: " + numRecords + " LINHAS");
        System.out.println("Done.\n");

        //} //fim do IF que criei validando conexão
        return logs;
    }

    public void setDadosParaConversao(String tableName, String fileName, int sRID, String columnID, int append, int functionFaz, Connection conexao) throws SQLException, Exception {

        m_tableName = tableName;
        m_shapefileName = fileName;
        m_srid = sRID;
        m_idName = columnID;
        //m_idStruct = idStruct;
        conn = conexao;
        skip_create_table = append;
        recompilaFazenda = functionFaz;
    }

    protected void prepareTableForData(Connection conn, DBFReaderJGeom dbfr, ShapefileFeatureJGeom sf, //static foi removido
            ShapefileReaderJGeom sfh)
            throws IOException, SQLException {
        ////////////////////////////////////////////////////////////////////////////   
        // Preparation of the database   
        ////////////////////////////////////////////////////////////////////////////   

        // Drop table   
        System.out.println("Dropping old table...");
        logs.add("\r\n\n-> TABELA " + m_tableName + " ANTERIOR FOI APAGADA!");
        Statement stmt = null;
        String update;

        try {
            stmt = conn.createStatement();
            update = "DROP TABLE " + m_tableName;
            stmt.executeUpdate(update);
            stmt.close();
        } catch (SQLException de) {
            System.out.println(de);
        }

        // Delete reference to it from metadata table   
        try {
            stmt = conn.createStatement();
            // For Oracle Spatial 8.1.6+ databases   
            update
                    = "DELETE FROM " + geomMetaDataTable + " WHERE table_name = '" + m_tableName.toUpperCase() + "'";
            stmt.executeUpdate(update);
            stmt.close();
        } catch (SQLException de) {
            System.out.println(de);
        }

        try {
            //Try to find and replace instances of "geometry" with m_geom   
            String relSchema = sf.getRelSchema(dbfr, m_idName);
            String updatedRelSchema = replaceAllWords1(relSchema, "geometry", m_geom);
            //System.out.println(updatedRelSchema);   

            // Create feature table   
            System.out.println("Creating new table...");
            logs.add("\r\n\n-> CRIANDO NOVA TABELA " + m_tableName + "...");
            //System.out.println("RelSchema: " + sf.getRelSchema(dbfr, m_idName));   
            stmt = conn.createStatement();
            update = "CREATE TABLE " + m_tableName + " ("
                    + /*sf.getRelSchema(dbfr, m_idName)*/ updatedRelSchema + ")";
            stmt.executeUpdate(update);
            stmt.close();

        } catch (SQLException de) {
            System.out.println(de);
        }

        if (m_srid != 0) {
            try {
                // Add reference to geometry metadata table.   
                stmt = conn.createStatement();
                update = "INSERT INTO " + geomMetaDataTable + " VALUES ('" + m_tableName
                        + "', '" + m_geom.toUpperCase() + "', " + dimArray + ", " + m_srid + ")";
                stmt.executeUpdate(update);
                stmt.close();
            } catch (SQLException de) {
                System.out.println(de);
            }
        } else {
            try {
                // Add reference to geometry metadata table.   
                stmt = conn.createStatement();
                update = "INSERT INTO " + geomMetaDataTable + " VALUES ('" + m_tableName
                        + "', '" + m_geom.toUpperCase() + "', " + dimArray + ", NULL)";
                stmt.executeUpdate(update);
                stmt.close();
            } catch (SQLException de) {
                System.out.println(de);
            }
        }
    }

    static String replaceAllWords1(String original, String find, String replacement) {
        String result = "";
        String delimiters = "+-*/(),. ";
        StringTokenizer st = new StringTokenizer(original, delimiters, true);
        while (st.hasMoreTokens()) {
            String w = st.nextToken();
            if (w.equals(find)) {
                result = result + replacement;
            } else {
                result = result + w;
            }
        }
        return result;
    }

}
