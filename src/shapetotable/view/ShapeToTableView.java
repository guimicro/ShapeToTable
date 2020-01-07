/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapetotable.view;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.DBFReaderJGeom;
import oracle.spatial.util.ShapefileFeatureJGeom;
import oracle.spatial.util.ShapefileReaderJGeom;
import oracle.sql.STRUCT;
import shapetotable.IconeNoTray;
import shapetotable.ManipuladorDeArquivos;
import shapetotable.dao.AgendadorDao;
import shapetotable.dao.CadastroBancoDao;
import shapetotable.dao.CadastroDiretorioDao;
import shapetotable.dao.CadastroShapeBancoDao;
import shapetotable.dao.ParametrosDao;
import shapetotable.database.ConnectionDBOracle;
import shapetotable.domain.Agendador;
import shapetotable.domain.CadastroBanco;
import shapetotable.domain.CadastroDiretorio;
import shapetotable.domain.CadastroShapeBanco;
import shapetotable.domain.Parametros;

/**
 *
 * @author Guilherme
 */
public class ShapeToTableView extends javax.swing.JFrame {

    private final ArrayList<CadastroDiretorio> ListaDeShapes = new ArrayList<>();
    private final ArrayList<CadastroBanco> ListaDeBancos = new ArrayList<>();
    private final ArrayList<CadastroShapeBanco> ListaDeImportacao = new ArrayList<>();
    private String path;
    private String horarioAgendamento;
    private boolean agendamentoAtivado;
    private IconeNoTray trayManager;
    private ImageIcon icone;

    //--------------------------------------------------------------------------
    //Atributos copiados da classe de conversão
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
    private String nomeDB;
    private boolean processamentoAtivo = false;

    Connection conn = null;
    ArrayList<String> logs = new ArrayList<>(); //criado para retornar os logs para a tela principal
    int error_cnt;
    int numRecords;
    //--------------------------------------------------------------------------

    /**
     * Creates new form ShapeToFileView
     */
    public ShapeToTableView() {
        initComponents();
        setIcon();
    }

    public void ListarComboBoxShape() {

        jCBxShape.removeAllItems();
        jCBxShape.addItem("Selecione");
        ListaDeShapes.clear();
        CadastroDiretorioDao listarShape = new CadastroDiretorioDao();

        listarShape.getListarCadDir().forEach((item) -> {
            jCBxShape.addItem(item.getDescricao());
            ListaDeShapes.add(item);
        });
    }

    public void ListarComboBoxBanco() {

        jCBxBanco.removeAllItems();
        jCBxBanco.addItem("Selecione");
        ListaDeBancos.clear();
        CadastroBancoDao listarBanco = new CadastroBancoDao();

        listarBanco.getListarCadBanco().forEach((item) -> {
            jCBxBanco.addItem(item.getNomeConexao());
            ListaDeBancos.add(item);
        });
    }

    public void ListarJTable() {
        try {

            CadastroShapeBancoDao buscar = new CadastroShapeBancoDao();

            jTblImportacao.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTblImportacao.getColumnModel().getColumn(1).setPreferredWidth(200);
            jTblImportacao.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTblImportacao.getColumnModel().getColumn(3).setPreferredWidth(40);
            DefaultTableModel tabela = (DefaultTableModel) jTblImportacao.getModel();

            tabela.setNumRows(0);

            for (CadastroShapeBanco listar : buscar.getListarShpBan()) {
                tabela.addRow(new Object[]{
                    listar.getIdShpBanco(),
                    listar.getDescricao(),
                    listar.getNomeDB(),
                    listar.isAtivado()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Problema ao listar Tabela: " + e.getMessage());
        }
    }

    public void listarAgendamento() {
        AgendadorDao agendamentoDao = new AgendadorDao();
        agendamentoDao.getListarAgendador().forEach((Agendador listar) -> {
            horarioAgendamento = listar.getHorario();
            agendamentoAtivado = listar.isAtivado();

            if (agendamentoAtivado) {
                jLblAgendAtivado.setText("<html>ATIVADO");
                jLblAgendAtivado.setForeground(Color.BLUE);
            } else {
                jLblAgendAtivado.setText("DESATIVADO");
                jLblAgendAtivado.setForeground(Color.red);
            }
            jLblHorario.setText("<HTML><b>HORÁRIO: " + horarioAgendamento.substring(0, 5));
//            System.out.println("horario agendado:" + horarioAgendamento);
        });
    }

    public void listarParametrosTela() {
        ParametrosDao pegarParametros = new ParametrosDao();
        pegarParametros.getListarParametros().forEach((Parametros listar) -> {
            jTxNomeTabela.setText(listar.getTableName());
            jTxColunaId.setText(listar.getIdName());
            jTxSrid.setText(String.valueOf(listar.getSrid()));
            if (listar.getRecompilar() == 0) {
                jCBoxFunction.setSelected(false);
            } else {
                jCBoxFunction.setSelected(true);
            }
            if (listar.getAppend() == 0) {
                jCBoxAppend1.setSelected(false);
                jCBoxAppend2.setSelected(false);
            } else if (listar.getAppend() == 1) {
                jCBoxAppend1.setSelected(true);
                jCBoxAppend2.setSelected(false);
            } else {
                jCBoxAppend1.setSelected(false);
                jCBoxAppend2.setSelected(true);
            }
        });
    }

    public void alterarParametrosTela() {
        ParametrosDao alterarParametrosDao = new ParametrosDao();
        Parametros alterarParametros = new Parametros();

        alterarParametros.setTableName(jTxNomeTabela.getText());
        alterarParametros.setIdName(jTxColunaId.getText());
        alterarParametros.setSrid(Integer.valueOf(jTxSrid.getText()));
        if (jCBoxFunction.isSelected()) {
            alterarParametros.setRecompilar(1);
        } else {
            alterarParametros.setRecompilar(0);
        }
        if (jCBoxAppend1.isSelected()) {
            alterarParametros.setAppend(1);
        } else if (jCBoxAppend2.isSelected()) {
            alterarParametros.setAppend(2);
        } else if (!jCBoxAppend1.isSelected() && !jCBoxAppend2.isSelected()) {
            alterarParametros.setAppend(0);
        }

        alterarParametrosDao.atualizarCadParametros(alterarParametros);
    }

    public void appendJTALog(String texto) {
        jTALog.append(texto);
        jTALog.setCaretPosition(jTALog.getText().length());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTblImportacao = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCBxShape = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jCBxBanco = new javax.swing.JComboBox<>();
        jBtnAdicionar = new javax.swing.JButton();
        jBtnRemover = new javax.swing.JButton();
        jBtnAjuda = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTxNomeTabela = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTxColunaId = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTxSrid = new javax.swing.JTextField();
        jCBoxFunction = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTALog = new javax.swing.JTextArea();
        jBtnImportar = new javax.swing.JButton();
        jPanelText = new javax.swing.JPanel();
        jCBoxAppend1 = new javax.swing.JCheckBox();
        jCBoxAppend2 = new javax.swing.JCheckBox();
        jBtnLogs = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLblAgendAtivado = new javax.swing.JLabel();
        jLblHorario = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SHAPE TO TABLE - SGIB");
        setMaximumSize(new java.awt.Dimension(1018, 715));
        setName("frmPrincipal"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1018, 715));
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LISTA DE IMPORTAÇÃO"));
        jPanel1.setName(""); // NOI18N

        jTblImportacao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "SHAPE", "BANCO ORACLE", "ATIVADO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTblImportacao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTblImportacaoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTblImportacao);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "CONFIGURAR: LISTA DE IMPORTAÇÃO DE MAPAS"));

        jLabel1.setText("MAPA (Shape)");

        jCBxShape.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
        jCBxShape.setPreferredSize(new java.awt.Dimension(82, 30));
        jCBxShape.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jCBxShapeFocusGained(evt);
            }
        });

        jLabel2.setText("BANCO ORACLE (Conexão)");

        jCBxBanco.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
        jCBxBanco.setPreferredSize(new java.awt.Dimension(82, 30));
        jCBxBanco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jCBxBancoFocusGained(evt);
            }
        });

        jBtnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/mais-24.png"))); // NOI18N
        jBtnAdicionar.setText("<html>\n<b>Adicionar");
        jBtnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAdicionarActionPerformed(evt);
            }
        });

        jBtnRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/negativo-24.png"))); // NOI18N
        jBtnRemover.setText("<html>\n<b>Remover");
        jBtnRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRemoverActionPerformed(evt);
            }
        });

        jBtnAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/ajuda-32.png"))); // NOI18N
        jBtnAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAjudaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jCBxShape, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBxBanco, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBtnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBtnAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnAjuda, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCBxShape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCBxBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBtnRemover, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "PARÂMETROS"));

        jLabel3.setText("NOME DA TABELA:");

        jTxNomeTabela.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTxNomeTabelaFocusLost(evt);
            }
        });

        jLabel4.setText("NOME COLUNA ID:");

        jTxColunaId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTxColunaIdFocusLost(evt);
            }
        });

        jLabel5.setText("SRID:");

        jTxSrid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTxSridFocusLost(evt);
            }
        });

        jCBoxFunction.setText("<HTML>RECOMPILAR  <p>FUNCTION FAZENDA");
        jCBoxFunction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBoxFunctionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTxSrid, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                        .addComponent(jTxColunaId, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTxNomeTabela, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jCBoxFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxNomeTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxColunaId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTxSrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBoxFunction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LOG DE IMPORTAÇÃO"));

        jTALog.setColumns(20);
        jTALog.setLineWrap(true);
        jTALog.setRows(5);
        jScrollPane2.setViewportView(jTALog);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        jBtnImportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/importação-de-banco-de-dados-60.png"))); // NOI18N
        jBtnImportar.setText("<html>\n<center><b>Importar\n<p> Mapa(s) </p></center>");
        jBtnImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnImportarActionPerformed(evt);
            }
        });

        jPanelText.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "APPEND"));
        jPanelText.setPreferredSize(new java.awt.Dimension(173, 110));

        jCBoxAppend1.setText("MAPA FRAGMENTADO");
        jCBoxAppend1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBoxAppend1ActionPerformed(evt);
            }
        });

        jCBoxAppend2.setText("MAPA COMPLETO");
        jCBoxAppend2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBoxAppend2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTextLayout = new javax.swing.GroupLayout(jPanelText);
        jPanelText.setLayout(jPanelTextLayout);
        jPanelTextLayout.setHorizontalGroup(
            jPanelTextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTextLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBoxAppend1)
                    .addComponent(jCBoxAppend2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelTextLayout.setVerticalGroup(
            jPanelTextLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTextLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCBoxAppend1)
                .addGap(18, 18, 18)
                .addComponent(jCBoxAppend2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jBtnLogs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/registros-24.png"))); // NOI18N
        jBtnLogs.setText("Abrir logs");
        jBtnLogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLogsActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "PROCESSO AUTOMÁTICO"));
        jPanel5.setPreferredSize(new java.awt.Dimension(173, 61));

        jLblAgendAtivado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLblAgendAtivado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLblAgendAtivado.setText("DESATIVADO");
        jLblAgendAtivado.setToolTipText("");
        jLblAgendAtivado.setMaximumSize(new java.awt.Dimension(38, 15));
        jLblAgendAtivado.setMinimumSize(new java.awt.Dimension(38, 15));

        jLblHorario.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLblHorario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLblHorario.setText("Horário");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLblHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblAgendAtivado, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLblAgendAtivado, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLblHorario)
                .addContainerGap())
        );

        jMenuBar1.setBorder(new javax.swing.border.MatteBorder(null));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/salvar-24.png"))); // NOI18N
        jMenu1.setText("Cadastros");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/icons8-pilha-24.png"))); // NOI18N
        jMenuItem2.setText("Banco Oracle");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/icons8-abrir-pasta-24.png"))); // NOI18N
        jMenuItem1.setText("Caminho Shape");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/icons-script-24.png"))); // NOI18N
        jMenuItem3.setText("Function Fazenda");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/ferramentas-24.png"))); // NOI18N
        jMenu2.setText("Ferramentas");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/tarefas-24.png"))); // NOI18N
        jMenuItem4.setText("Agendador");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jBtnLogs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBtnImportar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanelText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnImportar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBtnLogs)
                        .addGap(13, 13, 13))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:

        CadastroDiretorioView abrirCadDirView = new CadastroDiretorioView();
        abrirCadDirView.setLocationRelativeTo(null);
        abrirCadDirView.setVisible(true);

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:

        CadastroBancoView abrirCadBancoView = new CadastroBancoView();
        abrirCadBancoView.setLocationRelativeTo(null);
        abrirCadBancoView.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        ListarComboBoxShape();
        ListarComboBoxBanco();
        ListarJTable();
        listarParametrosTela();
        listarAgendamento();
        jMenuItem3.setVisible(false);

        //---------------------------------------------------------------------------------------
        long TEMPO = (1000);
        Timer timer = null;
        if (timer == null) {
            timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                public void run() {
                    try {
//                        System.out.println("CONTADOR " + new SimpleDateFormat("HH:mm:ss").format(new Date()));

                        if (horarioAgendamento.equals(new SimpleDateFormat("HH:mm:ss").format(new Date()))) {
                            //System.out.println("Hora bateu com: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                            if (agendamentoAtivado == true) {
                                acionarImportar();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.scheduleAtFixedRate(tarefa, TEMPO, TEMPO);
        }
        //---------------------------------------------------------------------------------------

    }//GEN-LAST:event_formWindowOpened

    private void jCBxShapeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCBxShapeFocusGained
        // TODO add your handling code here:
        ListarComboBoxShape();
        ListarComboBoxBanco();
        ListarJTable();
    }//GEN-LAST:event_jCBxShapeFocusGained

    private void jCBxBancoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCBxBancoFocusGained
        // TODO add your handling code here:
        ListarComboBoxBanco();
    }//GEN-LAST:event_jCBxBancoFocusGained

    private void jBtnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAdicionarActionPerformed

        if ((jCBxShape.getSelectedIndex() > 0) && (jCBxBanco.getSelectedIndex() > 0)) {

            int posicaoComboBoxShape = jCBxShape.getSelectedIndex() - 1;
            int posicaoComboBoxBanco = jCBxBanco.getSelectedIndex() - 1;

            CadastroShapeBanco adicionar = new CadastroShapeBanco();
            //System.out.println("posicao: " + posicaoComboBoxShape);

            adicionar.setIdcaminho(ListaDeShapes.get(posicaoComboBoxShape).getIdCaminho());
            adicionar.setIddb(ListaDeBancos.get(posicaoComboBoxBanco).getIdBanco());

            CadastroShapeBancoDao adicionarDao = new CadastroShapeBancoDao();
            adicionarDao.inserirShapeBanco(adicionar);

            ListarJTable();
        }
    }//GEN-LAST:event_jBtnAdicionarActionPerformed

    private void jBtnRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRemoverActionPerformed

        if (jTblImportacao.getSelectedRow() > -1) {

            CadastroShapeBanco remover = new CadastroShapeBanco();
            int linhaSelecionada = jTblImportacao.getSelectedRow();

            remover.setIdShpBanco((int) jTblImportacao.getValueAt(linhaSelecionada, 0));

            CadastroShapeBancoDao removerDao = new CadastroShapeBancoDao();
            removerDao.removerCadShpBan(remover);

            ListarJTable();
        }

    }//GEN-LAST:event_jBtnRemoverActionPerformed

    private void jBtnAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAjudaActionPerformed

        JOptionPane.showMessageDialog(null, "<html><b>[+] Adicionar:</b> Utilizado para incluir um novo item de importação na lista. Vinculo entre (Shape/Origem) e (Banco/Destino)"
                + "\n<html><u>Obs:</u> É necessário cadastrar pelo menos um Shape e um Banco de Dados no Menu <u>Cadastros</u>"
                + "\n\n<html><b>[-] Remover:</b> Utilizado para retirar um item de importação da lista. Antes de remover, selecione alguma linha da Lista"
                + "\n\n<html><b>Importar Mapa(s):</b> Usado para converter/importar algum Shape à um Banco que está selecionado na Lista. O item precisa estar 'Ativado'"
                + "\n\n<html><b>Parâmetros - Function Fazenda:</b> Se ativado, após o término da importação do mapa, será feita a recompilação da Função de Geo Localização"
                + "\n\n<html><b>Append:</b> Itens usados para realizar atualização da atual tabela (existente no Banco):"
                + "\n<html><b>Mapa Fragmentado:</b> Ideal para situação onde apenas um fragmento do mapa será adicionado, pois irá adicionar dados à tabela atual."
                + "\n<html><b>Mapa Completo:</b> Caso o shape seja completo, a tabela atual será atualizada.",
                "Shape To Table - Ajuda", JOptionPane.PLAIN_MESSAGE);

    }//GEN-LAST:event_jBtnAjudaActionPerformed

    private void jTblImportacaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTblImportacaoMouseClicked

        int linhaSelecionada = jTblImportacao.getSelectedRow();
        int colunaSelecionada = jTblImportacao.getSelectedColumn();

        System.out.println("Linha: " + linhaSelecionada + " coluna: " + colunaSelecionada + " valor bool: " + jTblImportacao.getValueAt(linhaSelecionada, colunaSelecionada) + " valor ID: " + jTblImportacao.getValueAt(linhaSelecionada, 0));
        if (jTblImportacao.getSelectedRow() > -1) {
            if (colunaSelecionada == 3) {
                CadastroShapeBanco alterar = new CadastroShapeBanco();
                alterar.setAtivado((boolean) jTblImportacao.getValueAt(linhaSelecionada, colunaSelecionada));
                alterar.setIdShpBanco((int) jTblImportacao.getValueAt(linhaSelecionada, 0));

                CadastroShapeBancoDao alterarDao = new CadastroShapeBancoDao();
                alterarDao.atualizarCadShpBan(alterar);
                //ListarJTable();
            }
        }

    }//GEN-LAST:event_jTblImportacaoMouseClicked

    private void jBtnImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnImportarActionPerformed
        if (!processamentoAtivo) {
            processamentoAtivo = true;
            jBtnImportar.setEnabled(false);
            acionarImportar();
        }
        
    }//GEN-LAST:event_jBtnImportarActionPerformed

    private void jCBoxFunctionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBoxFunctionActionPerformed
        // TODO add your handling code here:
        alterarParametrosTela();
    }//GEN-LAST:event_jCBoxFunctionActionPerformed

    private void jCBoxAppend1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBoxAppend1ActionPerformed
        if (jCBoxAppend1.isSelected()) {
            jCBoxAppend2.setSelected(false);
        }
        alterarParametrosTela();
    }//GEN-LAST:event_jCBoxAppend1ActionPerformed

    private void jCBoxAppend2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBoxAppend2ActionPerformed
        if (jCBoxAppend2.isSelected()) {
            jCBoxAppend1.setSelected(false);
        }
        alterarParametrosTela();
    }//GEN-LAST:event_jCBoxAppend2ActionPerformed

    private void jBtnLogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLogsActionPerformed
        try {
            Runtime.getRuntime().exec("explorer " + new File("./Logs/")); // abre pasta de Logs
        } catch (Exception e) {
        }

    }//GEN-LAST:event_jBtnLogsActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        AgendadorView abrirAgendador = new AgendadorView();
        abrirAgendador.setLocationRelativeTo(null);
        abrirAgendador.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        listarAgendamento();
    }//GEN-LAST:event_formWindowGainedFocus

    private void jTxNomeTabelaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTxNomeTabelaFocusLost
        // TODO add your handling code here:
        alterarParametrosTela();
    }//GEN-LAST:event_jTxNomeTabelaFocusLost

    private void jTxColunaIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTxColunaIdFocusLost
        // TODO add your handling code here:
        alterarParametrosTela();
    }//GEN-LAST:event_jTxColunaIdFocusLost

    private void jTxSridFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTxSridFocusLost
        // TODO add your handling code here:
        alterarParametrosTela();
    }//GEN-LAST:event_jTxSridFocusLost

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (JOptionPane.showConfirmDialog(null, "<html>Realmente deseja sair do Sistema?", "Shape To Table", JOptionPane.YES_NO_OPTION, 0, new ImageIcon("./Src/Icones/interrogacao.png")) == 0) {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        this.dispose();

        trayManager = new IconeNoTray(this, icone);
        trayManager.setBalaoSistemaTitulo("Shape To Table");
        trayManager.setToolTipText("Shape To Table");
        trayManager.setBalaoSistemaDescricao("O programa ainda está em execução!");
        trayManager.criarTrayIcon();

        try {
            trayManager.adicionarATray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_formWindowIconified

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ShapeToTableView().setVisible(true);

            }
        });

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnAdicionar;
    private javax.swing.JButton jBtnAjuda;
    private javax.swing.JButton jBtnImportar;
    private javax.swing.JButton jBtnLogs;
    private javax.swing.JButton jBtnRemover;
    private javax.swing.JCheckBox jCBoxAppend1;
    private javax.swing.JCheckBox jCBoxAppend2;
    private javax.swing.JCheckBox jCBoxFunction;
    private javax.swing.JComboBox<String> jCBxBanco;
    private javax.swing.JComboBox<String> jCBxShape;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLblAgendAtivado;
    private javax.swing.JLabel jLblHorario;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelText;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JTextArea jTALog;
    private javax.swing.JTable jTblImportacao;
    private javax.swing.JTextField jTxColunaId;
    private javax.swing.JTextField jTxNomeTabela;
    private javax.swing.JTextField jTxSrid;
    // End of variables declaration//GEN-END:variables

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Sol.png")));
        icone = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Sol2.png")));
    }

    public void acionarImportar() {
        new Thread() {
            @Override
            public void run() {

                int append = 0;
                int funcFaz = 0;

                CadastroShapeBancoDao pegarDadosAtivados = new CadastroShapeBancoDao();
                pegarDadosAtivados.getListarShpBanAtivado().forEach((item) -> {
                    ListaDeImportacao.add(item);
                });

                //ShapeToTableConvert Conversao = new ShapeToTableConvert();
                //Conversao.setTamanhoArrayParaStruct(ListaDeImportacao.size());
                jTALog.setText("");

                Date dataInicio = new Date();
                String dataI = new SimpleDateFormat("dd/MM/yyyy").format(dataInicio);
                String horaI = new SimpleDateFormat("HH:mm:ss").format(dataInicio);
                appendJTALog("[INICIO DO PROCESSO] " + dataI + " " + horaI);
                appendJTALog("\r\n\nINICIANDO IMPORTAÇÃO DE MAPA - QUANTIDADE DE SHAPES PARA CONVERTER: " + String.valueOf(ListaDeImportacao.size()));
//                System.out.println("Quantidade para importar: " + ListaDeImportacao.size());

                if (ListaDeImportacao.size() > 0) {

                    for (int i = 0; i < ListaDeImportacao.size(); i++) {

                        //if (JOptionPane.showConfirmDialog(null, "Deseja Continuar com a conversão?" , "Conversão", JOptionPane.YES_NO_OPTION) == 0) {
                        String p_host = ListaDeImportacao.get(i).getHost(); //System.out.println("Host: " + p_host);
                        int p_porta = ListaDeImportacao.get(i).getPorta(); //System.out.println("Porta: " + p_porta);
                        String p_sid = ListaDeImportacao.get(i).getSid(); //System.out.println("SID: " + p_sid);
                        String p_tns = ListaDeImportacao.get(i).getTns(); //System.out.println("TNS: " + p_tns);
                        String p_usuario = ListaDeImportacao.get(i).getUsuario();
                        String p_senha = ListaDeImportacao.get(i).getSenha();
                        String p_tableName = jTxNomeTabela.getText().toUpperCase().trim();
                        String p_caminhoShape = ListaDeImportacao.get(i).getCaminhoShape().substring(0, ListaDeImportacao.get(i).getCaminhoShape().length() - 4);
                        nomeDB = ListaDeImportacao.get(i).getNomeDB();
                        int p_sRID = Integer.valueOf(jTxSrid.getText().trim());
                        String p_idColumn = jTxColunaId.getText().toUpperCase().trim();

                        if (jCBoxAppend1.isSelected()) {
                            append = 1;
                        }
                        if (jCBoxAppend2.isSelected()) {
                            append = 2;
                        }
                        if (jCBoxFunction.isSelected()) {
                            funcFaz = 1;
                        }

                        appendJTALog("\r\n\n---------------------------------------------------------------------------------------");
                        appendJTALog("\r\n" + (i + 1) + "º ITEM SELECIONADO -> POR FAVOR AGUARDE, TENTANDO CONECTAR AO BANCO DE DADOS ORACLE...");

                        try {
                            if (!p_host.isEmpty()) { // Se TNS é vazio, usar conexão comum
                                //System.out.println("entrou em conversao usando host");
                                ConnectionDBOracle conexao = new ConnectionDBOracle();
                                Connection con;
                                conexao.setParametrosConexaoComum(p_host, p_sid, p_porta, p_usuario, p_senha);
                                try {
                                    con = conexao.getConnection();
                                    con.setAutoCommit(false);

                                    appendJTALog("\r\n\n-> DADOS DE CONEXÃO DO BANCO: " + nomeDB
                                            + "\r\n [HOST: " + p_host + " | PORTA: " + p_porta + " | SID: " + p_sid + "]");

//                                    Conversao.setDadosParaConversao(p_tableName, p_caminhoShape, p_sRID, p_idColumn, append, funcFaz, con);
                                    setDadosParaConversao(p_tableName, p_caminhoShape, p_sRID, p_idColumn, append, funcFaz, con);

                                    ArrayList<String> logs;

                                    logs = converter(); //logs = Conversao.converter();
                                    conexao.closeAll(con);

                                    for (int x = 0; x < logs.size(); x++) {
                                        appendJTALog(logs.get(x));
                                    }

                                } catch (Exception e) {
                                }

                            }
                        } catch (Exception ex) {
                            //Logger.getLogger(ShapeToTableView.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("problema de conexão com Host");
                        }

                        try {
                            if (!p_tns.isEmpty()) { // Se TNS não é vazio
                                //System.out.println("entrou em conversao usando tns");
                                ConnectionDBOracle conexao = new ConnectionDBOracle();
                                Connection con;
                                conexao.setParametrosConexaoTNS(p_tns, p_usuario, p_senha);
                                try {
                                    con = conexao.getConnection();
                                    con.setAutoCommit(false);

                                    appendJTALog("\r\n\n-> DADOS DE CONEXÃO DO BANCO: " + nomeDB + "\n [TNS:\n " + p_tns + "]");

//                                    Conversao.setDadosParaConversao(p_tableName, p_caminhoShape, p_sRID, p_idColumn, append, funcFaz, con);
                                    setDadosParaConversao(p_tableName, p_caminhoShape, p_sRID, p_idColumn, append, funcFaz, con);
                                    ArrayList<String> logs;

//                                    logs = Conversao.converter();
                                    logs = converter();
                                    conexao.closeAll(con);

                                    for (int x = 0; x < logs.size(); x++) {
                                        appendJTALog(logs.get(x));
                                    }
                                } catch (Exception e) {
                                }
                            }
                        } catch (Exception ex) {
                            //Logger.getLogger(ShapeToTableView.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("problema de conexão com tns");
                        }
                        //}
                    }
                    appendJTALog("\r\n\n---------------------------------------------------------------------------------------");
                    appendJTALog("\r\nIMPORTAÇÕES FINALIZADAS!");

                    Date dataFim = new Date();
                    String dataF = new SimpleDateFormat("dd/MM/yyyy").format(dataInicio);
                    String horaF = new SimpleDateFormat("HH:mm:ss").format(dataFim);
                    appendJTALog("\r\n\n[FIM DO PROCESSO] " + dataF + " " + horaF);

                    ManipuladorDeArquivos gerarLog = new ManipuladorDeArquivos();
                    //System.out.println("caminho: " + gerarLog.salvarLog(jTALog.getText())); 
                    path = gerarLog.salvarLog(jTALog.getText());
                    ListaDeImportacao.clear();
                    jBtnImportar.setEnabled(true);
                    processamentoAtivo = false;
                }
            }
        }.start();
        
        
    }

    //--------------------------------------------------------------------------
    // MÉTODOS ABAIXO COPIADOS DA CLASSE DE CONVERSÃO
    //--------------------------------------------------------------------------
    public void setDadosParaConversao(String tableName, String fileName, int sRID, String columnID, int append, int recompilaFaz, Connection conexao) throws SQLException, Exception {

        m_tableName = tableName;
        m_shapefileName = fileName;
        m_srid = sRID;
        m_idName = columnID;
        conn = conexao;
        skip_create_table = append;
        recompilaFazenda = recompilaFaz;
    }

    protected void prepareTableForData(Connection conn, DBFReaderJGeom dbfr, ShapefileFeatureJGeom sf, //static foi removido
            ShapefileReaderJGeom sfh)
            throws IOException, SQLException {
        ////////////////////////////////////////////////////////////////////////////   
        // Preparation of the database   
        ////////////////////////////////////////////////////////////////////////////   

        // Drop table   
        System.out.println("Dropping old table...");
//        logs.add("\r\n\n-> TABELA " + m_tableName + " ANTERIOR FOI APAGADA!");
        jTALog.append("\r\n\n-> TABELA " + m_tableName + " ANTERIOR FOI APAGADA!");
        jTALog.setCaretPosition(jTALog.getText().length());
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
            update = "DELETE FROM " + geomMetaDataTable + " WHERE table_name = '" + m_tableName.toUpperCase() + "'";
            stmt.executeUpdate(update);
            stmt.close();
        } catch (SQLException de) {
            System.out.println(de);
        }

        try {
            //Try to find and replace instances of "geometry" with m_geom   
            String relSchema = sf.getRelSchema(dbfr, m_idName);
            String updatedRelSchema = replaceAllWords1(relSchema, "geometry", m_geom);

            // Create feature table   
            System.out.println("Creating new table...");
//            logs.add("\r\n\n-> CRIANDO NOVA TABELA " + m_tableName + "...");
            jTALog.append("\r\n\n-> CRIANDO NOVA TABELA " + m_tableName + "...");
            jTALog.setCaretPosition(jTALog.getText().length());
            //System.out.println("RelSchema: " + sf.getRelSchema(dbfr, m_idName));   
            stmt = conn.createStatement();
            update = "CREATE TABLE " + m_tableName + " (" + /*sf.getRelSchema(dbfr, m_idName)*/ updatedRelSchema + ")";

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

    public ArrayList<String> converter() throws IOException, SQLException, Exception {

        // Open dbf and input files
        DBFReaderJGeom dbfr = new DBFReaderJGeom(m_shapefileName);
        //System.out.println("ShapeFile name: " + m_shapefileName);
//        logs.clear();
//        logs.add("\r\n\n-> CAMINHO SHAPE: " + m_shapefileName);
        jTALog.append("\r\n\n-> CAMINHO SHAPE: " + m_shapefileName);
        jTALog.setCaretPosition(jTALog.getText().length());

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
            m_start_id = 1;
        } else {
//            System.out.println("Appending to existing table -> APPEND = 1\n");
            jTALog.append("\r\n\n-> ATUALIZANDO DADOS DA TABELA");
            jTALog.setCaretPosition(jTALog.getText().length());

            if (skip_create_table == 2) {
                try {
//                    System.out.println("APPEND = 2");
                    PreparedStatement stmt = conn.prepareStatement("SELECT MAX(" + m_idName + ") AS ID FROM " + m_tableName);
                    ResultSet rs = stmt.executeQuery();
                    rs.next();

                    m_start_id = (rs.getInt("ID") + 1);
//                    System.out.println("Maior ID: " + m_start_id);
                    stmt.close();
                } catch (SQLException exF) {
                    System.out.println(exF);
                    System.out.println("caiu na exceção");
                }
            }

        }

        ////////////////////////////////////////////////////////////////////////////
        // Conversion from Feature to DB
        ////////////////////////////////////////////////////////////////////////////
        error_cnt = 0;
        int numFields = dbfr.numFields();
        numRecords = dbfr.numRecords();
//        logs.add("\r\n\n-> QUANTIDADE DE REGISTROS À CONVERTER DO ARQUIVO SHAPE: " + String.valueOf(numRecords));
        jTALog.append("\r\n\n-> QUANTIDADE DE REGISTROS À CONVERTER DO ARQUIVO SHAPE: " + String.valueOf(numRecords));
        jTALog.setCaretPosition(jTALog.getText().length());
        byte[] fieldTypes = new byte[numFields];
        for (int field = 0; field < numFields; field++) {
            fieldTypes[field] = dbfr.getFieldType(field);
            System.out.println("Campos: " + dbfr.getFieldName(field));
//            logs.add("\r\n [COLUNA" + (field + 1) + ": " + dbfr.getFieldName(field) + "]");
            jTALog.append("\r\n [COLUNA" + (field + 1) + ": " + dbfr.getFieldName(field) + "]");
            jTALog.setCaretPosition(jTALog.getText().length());
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

        StatusConversaoView popUpStatusConversao = new StatusConversaoView();
        popUpStatusConversao.setLocationRelativeTo(null);
        popUpStatusConversao.setVisible(true);

        for (int i = 0; i < numRecords; i++) {

            //Edit to adjust, or comment to remove screen output; default 10
            if ((i + 1) % 1 == 0) {
                //System.out.println("Converting record #" + (i + 1));
                popUpStatusConversao.setStatus(i + 1, numRecords, nomeDB);
            }

            if (i == (numRecords - 1)) {
                popUpStatusConversao.setStatusFinal("Indexando Registros.");
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
                    logs.add("\r\nREGISTRO #" + (i + 1) + " NÃO CONVERTIDO");
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
                    e.printStackTrace();
//                    logs.add("\r\nREGISTRO #" + (i + 1) + "NÃO CONVERTIDO");
                    jTALog.append("\r\nREGISTRO #" + (i + 1) + " NÃO CONVERTIDO");
                    jTALog.setCaretPosition(jTALog.getText().length());
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

//                logs.add("\r\n\n-> FUNÇÃO 'FNC_LOCALIZA_FAZ' FOI RECOMPILADA COM SUCESSO!");
                jTALog.append("\r\n\n-> FUNÇÃO 'FNC_LOCALIZA_FAZ' FOI RECOMPILADA COM SUCESSO!");
                jTALog.setCaretPosition(jTALog.getText().length());
            } catch (SQLException exF) {
                System.out.println(exF);
                //logs.add("\r\n\n-> FUNÇÃO 'FNC_LOCALIZA_FAZ' NÃO RECOMPILADA!\n [" + exF.getMessage().trim() + "]");
                jTALog.append("\r\n\n-> FUNÇÃO 'FNC_LOCALIZA_FAZ' NÃO RECOMPILADA!\n [" + exF.getMessage().trim() + "]");
                jTALog.setCaretPosition(jTALog.getText().length());
            }
        }

        if (skip_create_table == 2) {
            try {
                PreparedStatement deleteRegostrosAnt = conn.prepareStatement("DELETE FROM " + m_tableName + " WHERE " + m_idName + " < " + m_start_id);
                deleteRegostrosAnt.executeUpdate();
                conn.commit();
                deleteRegostrosAnt.close();
            } catch (SQLException exF) {
                System.out.println(exF);
            }
            try {
                PreparedStatement updateIDs = conn.prepareStatement("UPDATE " + m_tableName + " SET " + m_idName + " = ROWNUM");
                updateIDs.executeUpdate();
                conn.commit();
                updateIDs.close();
//                    System.out.println("Maior ID: " + m_start_id);
            } catch (SQLException exF) {
                System.out.println(exF);
            }

        }

        conn.commit();
        dbfr.closeDBF();
        sfh.closeShapefile();
        ps.close();
        psMig.close();
        psCom.close();
        popUpStatusConversao.dispose();

        if (error_cnt > 0) {
            System.out.println(error_cnt + " record(s) not converted.");
//            logs.add("\r\n\nTOTAL DE REGISTROS NÃO IMPORTADOS: " + error_cnt);
            jTALog.append("\r\n\nREGISTROS NÃO CONVERTIDOS: " + error_cnt);
            jTALog.setCaretPosition(jTALog.getText().length());
        }

        System.out.println((numRecords - error_cnt) + " record(s) converted.");
//        logs.add("\r\n\nTOTAL DE REGISTROS IMPORTADOS: " + numRecords + " LINHAS");
        jTALog.append("\r\n\nTOTAL DE REGISTROS IMPORTADOS: " + (numRecords - error_cnt) + " LINHAS");
        jTALog.setCaretPosition(jTALog.getText().length());
        System.out.println("Done.\n");

        //} //fim do IF que criei validando conexão
        return logs;
    }

}
