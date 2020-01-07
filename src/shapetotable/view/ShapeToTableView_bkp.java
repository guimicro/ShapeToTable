/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapetotable.view;

import java.awt.Toolkit;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import shapetotable.ManipuladorDeArquivos;
import shapetotable.ShapeToTableConvert;
import shapetotable.dao.CadastroBancoDao;
import shapetotable.dao.CadastroDiretorioDao;
import shapetotable.dao.CadastroShapeBancoDao;
import shapetotable.database.ConnectionDBOracle;
import shapetotable.domain.CadastroBanco;
import shapetotable.domain.CadastroDiretorio;
import shapetotable.domain.CadastroShapeBanco;

/**
 *
 * @author Guilherme
 */
public class ShapeToTableView_bkp extends javax.swing.JFrame {

    private final ArrayList<CadastroDiretorio> ListaDeShapes = new ArrayList<>();
    private final ArrayList<CadastroBanco> ListaDeBancos = new ArrayList<>();
    private final ArrayList<CadastroShapeBanco> ListaDeImportacao = new ArrayList<>();

    /**
     * Creates new form ShapeToFileView
     */
    public ShapeToTableView_bkp() {
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
        jCBoxAppend = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTALog = new javax.swing.JTextArea();
        jBtnImportar = new javax.swing.JButton();
        jLblConvertidos = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Shape To Table - SGIB");
        setName("frmPrincipal"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "CONFIGURAR: LISTA DE IMPORTAÇÃO DE MAPAS"));

        jLabel1.setText("MAPA (Shape)");

        jCBxShape.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
        jCBxShape.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jCBxShapeFocusGained(evt);
            }
        });

        jLabel2.setText("BANCO ORACLE (Conexão)");

        jCBxBanco.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione" }));
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
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

        jTxNomeTabela.setText("GEO_LAYER");

        jLabel4.setText("NOME COLUNA ID:");

        jTxColunaId.setText("ID");

        jLabel5.setText("SRID:");

        jTxSrid.setText("8307");

        jCBoxFunction.setSelected(true);
        jCBoxFunction.setText("FUNCTION FAZENDA");

        jCBoxAppend.setText("APPEND");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBoxAppend)
                    .addComponent(jCBoxFunction)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTxSrid, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTxColunaId, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTxNomeTabela, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxSrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jCBoxAppend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBoxFunction)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "LOG DE IMPORTAÇÃO"));

        jTALog.setColumns(20);
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );

        jBtnImportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/importação-de-banco-de-dados-60.png"))); // NOI18N
        jBtnImportar.setText("<html>\n<center><b>Importar\n<p> Mapa(s) </p></center>");
        jBtnImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnImportarActionPerformed(evt);
            }
        });

        jLblConvertidos.setText("dados: ");

        jMenuBar1.setBorder(new javax.swing.border.MatteBorder(null));

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

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBtnImportar))
                    .addComponent(jLblConvertidos))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jLblConvertidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBtnImportar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
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
    }//GEN-LAST:event_formWindowOpened

    private void jCBxShapeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCBxShapeFocusGained
        // TODO add your handling code here:
        ListarComboBoxShape();
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

        JOptionPane.showMessageDialog(null, "Para importar um MAPA à um BANCO DE DADOS é necessário selecionar o 'Shape' e a 'Conexão', e então [+] Adicionar."
                + "\n\nCaso não exista nenhuma configuração de Mapa (Shape) ou Banco cadastrados, vá ao menu 'Cadastros' e cadastre-os."
                + "\n\nObs: o Campo 'Ativado' precisa estar marcado para que a importação seja realizada!", "Shape To Table - Ajuda", JOptionPane.PLAIN_MESSAGE);

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

        new Thread() {
            @Override
            public void run() {

                int append = 0;
                int funcFaz = 0;

                CadastroShapeBancoDao pegarDadosAtivados = new CadastroShapeBancoDao();
                pegarDadosAtivados.getListarShpBanAtivado().forEach((item) -> {
                    ListaDeImportacao.add(item);
                });

                ShapeToTableConvert Conversao = new ShapeToTableConvert();
                //Conversao.setTamanhoArrayParaStruct(ListaDeImportacao.size());
                jTALog.setText("");
                appendJTALog("INICIANDO IMPORTAÇÃO DE MAPA - QUANTIDADE DE SHAPES PARA CONVERTER: " + String.valueOf(ListaDeImportacao.size()));
                //System.out.println("Quantidade para importar: " + ListaDeImportacao.size());

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
                        int p_sRID = Integer.valueOf(jTxSrid.getText().trim());
                        String p_idColumn = jTxColunaId.getText().toUpperCase().trim();

                        if (jCBoxAppend.isSelected()) {
                            append = 1;
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

                                    appendJTALog("\r\n\n-> DADOS DE CONEXÃO AO BANCO"
                                            + "\r\n [HOST: " + p_host + " | PORTA: " + p_porta + " | SID: " + p_sid + "]");
                                    appendJTALog("\r\n\n-> PROCESSANDO OS DADOS, POR FAVOR AGUARDE...");

                                    Conversao.setDadosParaConversao(p_tableName, p_caminhoShape, p_sRID, p_idColumn, append, funcFaz, con);

                                    ArrayList<String> logs;

                                    logs = Conversao.converter();
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

                                    appendJTALog("\r\n\n-> DADOS DE CONEXÃO AO BANCO" + "\n [TNS:\n " + p_tns + "]");
                                    appendJTALog("\r\n\n-> PROCESSANDO OS DADOS, POR FAVOR AGUARDE...");

                                    Conversao.setDadosParaConversao(p_tableName, p_caminhoShape, p_sRID, p_idColumn, append, funcFaz, con);
                                    ArrayList<String> logs;

                                    logs = Conversao.converter();
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
                    appendJTALog("\r\n\nIMPORTAÇÕES FINALIZADAS!");

                    ManipuladorDeArquivos gerarLog = new ManipuladorDeArquivos();
                    //System.out.println("caminho: " + gerarLog.salvarLog(jTALog.getText())); 
                    String path = gerarLog.salvarLog(jTALog.getText());
                    ListaDeImportacao.clear();
                    if (JOptionPane.showConfirmDialog(null, "Deseja abrir a pasta de Logs?", "Importação Completa", JOptionPane.YES_NO_OPTION, 0, new ImageIcon("./Src/Icones/interrogacao.png")) == 0) {
                        try {
                            Runtime.getRuntime().exec("explorer " + path);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }.start();
    }//GEN-LAST:event_jBtnImportarActionPerformed

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
            java.util.logging.Logger.getLogger(ShapeToTableView_bkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView_bkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView_bkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ShapeToTableView_bkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ShapeToTableView_bkp().setVisible(true);

            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnAdicionar;
    private javax.swing.JButton jBtnAjuda;
    private javax.swing.JButton jBtnImportar;
    private javax.swing.JButton jBtnRemover;
    private javax.swing.JCheckBox jCBoxAppend;
    private javax.swing.JCheckBox jCBoxFunction;
    private javax.swing.JComboBox<String> jCBxBanco;
    private javax.swing.JComboBox<String> jCBxShape;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLblConvertidos;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
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
    }
}