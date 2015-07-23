/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JFrameParser.java
 *
 * Created on 04.mai.2010, 17:10:49
 */
package logParser;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Jakob
 */
public class ParserView extends javax.swing.JFrame implements ListDataListener{

    private static int logNumber = 1;
    private static final String PROPERTY_LOG_FOLDER_LOCATION = "NWNlogsFolder";
    private static final String PROPERTY_INI_PLAYERNAME = "Player"; //location: NWN/nwplayer.ini
    private static String install_path;
    private static final String TEXT_FILE_EXTENSION = ".txt";
    private static final String INI_FILE_EXTENSION = ".ini";
    private static final String LOG_FILENAME = "\\nwclientLog";
    private static final String INI_PLAYER_FILENAME = "\\nwnplayer";
    private final GeneralStatsTableModel generalStatsTableModel = new GeneralStatsTableModel();
    private final DamageDealtTableModel damageDealtTableModel = new DamageDealtTableModel();
    private final DamageTakenTableModel damageTakenTableModel = new DamageTakenTableModel();
    private final DamageReducedTableModel damageReducedTableModel = new DamageReducedTableModel();
    private final HitPercentageTableModel hitPercentageTableModel = new HitPercentageTableModel();
    private final SpellsAndSavesTableModel spellsAndSavesTableModel = new SpellsAndSavesTableModel();
    private static Properties parserProperties = new Properties();
    private static final File PARSER_PROPERTIES_FILE = new File("HalgrothsLogParserInfo.txt");
    private static File combatLog;
    private static File newFile;
    private Parser parser;
    private AbstractParyListModel abstractParyListModel = new AbstractParyListModel();

    public ParserView() {
        tryToSetLookAndFeel();
        initComponents();
        setWindowSizeAndPosition();
        jTableGeneralStats.setModel(generalStatsTableModel);
        jTableGeneralStats.setAutoCreateRowSorter(true);
        jTableGeneralStats.getRowSorter().toggleSortOrder(1);
        jTableGeneralStats.getRowSorter().toggleSortOrder(1);
        jTableDamageDealt.setModel(damageDealtTableModel);
        jTableDamageDealt.setAutoCreateRowSorter(true);
        jTableDamageDealt.getRowSorter().toggleSortOrder(1);
        jTableDamageDealt.getRowSorter().toggleSortOrder(1);
        jTableDamageTaken.setModel(damageTakenTableModel);
        jTableDamageTaken.setAutoCreateRowSorter(true);
        jTableDamageTaken.getRowSorter().toggleSortOrder(1);
        jTableDamageTaken.getRowSorter().toggleSortOrder(1);
        jTableDamageReduced.setModel(damageReducedTableModel);
        jTableDamageReduced.setAutoCreateRowSorter(true);
        jTableDamageReduced.getRowSorter().toggleSortOrder(1);
        jTableDamageReduced.getRowSorter().toggleSortOrder(1);
        jTableHitPercentage.setModel(hitPercentageTableModel);
        jTableHitPercentage.setAutoCreateRowSorter(true);
        jTableHitPercentage.getRowSorter().toggleSortOrder(1);
        jTableHitPercentage.getRowSorter().toggleSortOrder(1);
        jTableSpellsAndSaves.setModel(spellsAndSavesTableModel);
        jTableSpellsAndSaves.setAutoCreateRowSorter(true);
        jTableSpellsAndSaves.getRowSorter().toggleSortOrder(1);
        jTableSpellsAndSaves.getRowSorter().toggleSortOrder(1);
    }

    private void tryToSetLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, "Couldn't set Windows Look and Feel", ex);
        }
    }
    
    private void setWindowSizeAndPosition() {
        try {
            tryToLoadPreviousSettings();
        } catch (IOException ex) {
            Logger.getLogger(ParserView.class.getName()).log(Level.INFO, "Could not find pervious properties. Using defaults.", ex);
        }
    }
    
    //TODO: Load defaults
    private void tryToLoadPreviousSettings() throws IOException {
        parserProperties.load(new FileReader(PARSER_PROPERTIES_FILE));
        int extendedState = Integer.parseInt(parserProperties.getProperty("State", String.valueOf(this.getExtendedState())));
        if (extendedState != MAXIMIZED_BOTH) {
            this.setBounds(
                    Integer.parseInt(parserProperties.getProperty("X", String.valueOf(this.getX()))),
                    Integer.parseInt(parserProperties.getProperty("Y", String.valueOf(this.getY()))),
                    Integer.parseInt(parserProperties.getProperty("W", String.valueOf(this.getWidth()))),
                    Integer.parseInt(parserProperties.getProperty("H", String.valueOf(this.getHeight()))));
        } else {
            this.setExtendedState(MAXIMIZED_BOTH);
        }
    }
    
    private void saveFrame() throws IOException {
        parserProperties.setProperty("State", String.valueOf(this.getExtendedState()));
        parserProperties.setProperty("X", String.valueOf(this.getX()));
        parserProperties.setProperty("Y", String.valueOf(this.getY()));
        parserProperties.setProperty("W", String.valueOf(this.getWidth()));
        parserProperties.setProperty("H", String.valueOf(this.getHeight()));
        parserProperties.store(new FileWriter(PARSER_PROPERTIES_FILE), null);
    }

    public static void setLogNumber(int newLogNumber) {
        logNumber = newLogNumber;
    }

    //TODO: Revise everything below. Tried to clean up slightly but it's still horribly messy.
    private void runParser(int logNumber) {
        try {
            install_path = tryToGetLogDirectory();
            combatLog = new File(install_path + "\\nwclientLog" + logNumber + TEXT_FILE_EXTENSION);
            tryToOpenLogfile(combatLog);
            parser.startParsing();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "The specified folder does not exist. Please select log directory again.");
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, "Could not locate input file at location: " + combatLog, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "You have not set the location of the log files. Please select log directory.");
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, "Could not locate input file at location: " + combatLog, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, "Not allowed access to: " + combatLog, ex);
        }
    }
    
//    create and load default properties
    private static String tryToGetLogDirectory() throws FileNotFoundException, IOException {
        Reader reader;
        reader = new FileReader(PARSER_PROPERTIES_FILE);
        parserProperties.load(reader);
        reader.close();
        File inputDir = new File(parserProperties.getProperty(PROPERTY_LOG_FOLDER_LOCATION, "C:\\Program Files\\NeverwinterNights\\NWN\\logs"));
        if(inputDir.exists()){
            return parserProperties.getProperty(PROPERTY_LOG_FOLDER_LOCATION);
        }
        return "";
    }
    
    //TODO: Revise this calling procedure. Wrong method name, and exceptions are never thrown.
    private void tryToOpenLogfile(File file) throws FileNotFoundException, SecurityException {
        if (file.exists()) {
            parser = new Parser(abstractParyListModel);
            parser.addParserListener(generalStatsTableModel);
            parser.addParserListener(damageDealtTableModel);
            parser.addParserListener(damageTakenTableModel);
            parser.addParserListener(damageReducedTableModel);
            parser.addParserListener(hitPercentageTableModel);
            parser.addParserListener(spellsAndSavesTableModel);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonSelectLogDirectory = new javax.swing.JButton();
        jButtonStart = new javax.swing.JButton();
        jButtonReset = new javax.swing.JButton();
        jTextFieldUniques = new java.awt.TextField();
        jButtonStop = new javax.swing.JButton();
        jTextFieldTotalKills = new java.awt.TextField();
        jTextFieldUptime = new javax.swing.JTextField();
        jButtonCrash = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jScrollPaneGeneralStats = new javax.swing.JScrollPane();
        jTableGeneralStats = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDamageDealt = new javax.swing.JTable();
        jScrollPaneDamageTaken = new javax.swing.JScrollPane();
        jTableDamageTaken = new javax.swing.JTable();
        jScrollPaneDamageReduced = new javax.swing.JScrollPane();
        jTableDamageReduced = new javax.swing.JTable();
        jScrollPaneHitPercentage = new javax.swing.JScrollPane();
        jTableHitPercentage = new javax.swing.JTable();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaSpellInfo = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTableSpellsAndSaves = new javax.swing.JTable();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextPaneLoot = new javax.swing.JTextPane();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextPaneServerInfo = new javax.swing.JTextPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaPrintInfo = new javax.swing.JTextArea();
        jButtonPrintInfo = new javax.swing.JButton();
        jCheckBoxAutoCheckParty = new javax.swing.JCheckBox();
        jButtonResetPartylist = new javax.swing.JButton();
        jTextFieldAddToList = new javax.swing.JTextField();
        jLabelAddAttacker = new javax.swing.JLabel();
        jTextFieldRemoveFromList = new javax.swing.JTextField();
        jLabelRemoveAttacker = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jListPartyOverview = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Halgroth's NWN Log Parser v1.8");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                formPropertyChange(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(51, 51, 255)));

        jButtonSelectLogDirectory.setText("Select Log Directory");
        jButtonSelectLogDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectLogDirectoryActionPerformed(evt);
            }
        });

        jButtonStart.setText("Start");
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        jButtonReset.setText("Reset");
        jButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });

        jTextFieldUniques.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextFieldUniques.setEditable(false);
        jTextFieldUniques.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldUniquesActionPerformed(evt);
            }
        });

        jButtonStop.setText("Stop");
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });

        jTextFieldTotalKills.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextFieldTotalKills.setEditable(false);
        jTextFieldTotalKills.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTotalKillsActionPerformed(evt);
            }
        });

        jTextFieldUptime.setEditable(false);
        jTextFieldUptime.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jTextFieldUptime.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jButtonCrash.setText("Crash");
        jButtonCrash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCrashActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButtonSelectLogDirectory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldUniques, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldTotalKills, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldUptime, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCrash)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonStop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonStart)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButtonSelectLogDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonReset)
                .addComponent(jButtonStop)
                .addComponent(jButtonCrash))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldUniques, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldTotalKills, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldUptime))
                .addContainerGap())
        );

        jButtonReset.getAccessibleContext().setAccessibleName("jResetButton");
        jButtonCrash.getAccessibleContext().setAccessibleName("jButtonCrash");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 255, 255)));

        jTableGeneralStats.setModel(generalStatsTableModel);
        jTableGeneralStats.setColumnSelectionAllowed(true);
        jScrollPaneGeneralStats.setViewportView(jTableGeneralStats);

        jTabbedPane.addTab("General Stats", jScrollPaneGeneralStats);

        jTableDamageDealt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTableDamageDealt);

        jTabbedPane.addTab("Damage Dealt", jScrollPane1);

        jTableDamageTaken.setModel(damageTakenTableModel);
        jTableDamageTaken.setCellSelectionEnabled(true);
        jScrollPaneDamageTaken.setViewportView(jTableDamageTaken);
        jTableDamageTaken.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jTabbedPane.addTab("Damage Taken", jScrollPaneDamageTaken);

        jTableDamageReduced.setModel(damageReducedTableModel);
        jTableDamageReduced.setCellSelectionEnabled(true);
        jScrollPaneDamageReduced.setViewportView(jTableDamageReduced);
        jTableDamageReduced.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jTabbedPane.addTab("Damage Reduced", jScrollPaneDamageReduced);

        jTableHitPercentage.setModel(hitPercentageTableModel);
        jTableHitPercentage.setCellSelectionEnabled(true);
        jScrollPaneHitPercentage.setViewportView(jTableHitPercentage);

        jTabbedPane.addTab("Hit Percentage", jScrollPaneHitPercentage);

        jSplitPane1.setDividerLocation(650);

        jTextAreaSpellInfo.setColumns(20);
        jTextAreaSpellInfo.setRows(5);
        jScrollPane5.setViewportView(jTextAreaSpellInfo);

        jSplitPane1.setLeftComponent(jScrollPane5);

        jTableSpellsAndSaves.setModel(spellsAndSavesTableModel);
        jTableSpellsAndSaves.setCellSelectionEnabled(true);
        jScrollPane6.setViewportView(jTableSpellsAndSaves);

        jSplitPane1.setRightComponent(jScrollPane6);

        jTabbedPane.addTab("Spells & Saves", jSplitPane1);

        jSplitPane2.setDividerLocation(750);

        jTextPaneLoot.setContentType("text/html"); // NOI18N
        jScrollPane9.setViewportView(jTextPaneLoot);

        jSplitPane2.setLeftComponent(jScrollPane9);

        jTextPaneServerInfo.setContentType("text/html"); // NOI18N
        jScrollPane10.setViewportView(jTextPaneServerInfo);

        jSplitPane2.setRightComponent(jScrollPane10);

        jTabbedPane.addTab("Loot & Server Info", jSplitPane2);

        jTextAreaPrintInfo.setColumns(20);
        jTextAreaPrintInfo.setRows(5);
        jScrollPane2.setViewportView(jTextAreaPrintInfo);

        jButtonPrintInfo.setText("Print Info");
        jButtonPrintInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintInfoActionPerformed(evt);
            }
        });

        jCheckBoxAutoCheckParty.setSelected(true);
        jCheckBoxAutoCheckParty.setText("Auto check party");
        jCheckBoxAutoCheckParty.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jCheckBoxAutoCheckParty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAutoCheckPartyActionPerformed(evt);
            }
        });

        jButtonResetPartylist.setText("Reset Partylist");
        jButtonResetPartylist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetPartylistActionPerformed(evt);
            }
        });

        jTextFieldAddToList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldAddToListActionPerformed(evt);
            }
        });

        jLabelAddAttacker.setLabelFor(jTextFieldAddToList);
        jLabelAddAttacker.setText("Add attacker");

        jTextFieldRemoveFromList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldRemoveFromListActionPerformed(evt);
            }
        });

        jLabelRemoveAttacker.setText("Remove attacker");

        jListPartyOverview.setModel(abstractParyListModel);
        jScrollPane8.setViewportView(jListPartyOverview);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButtonPrintInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonResetPartylist)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxAutoCheckParty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                        .addComponent(jLabelRemoveAttacker)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldRemoveFromList, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelAddAttacker)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAddToList, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonPrintInfo)
                    .addComponent(jButtonResetPartylist)
                    .addComponent(jCheckBoxAutoCheckParty)
                    .addComponent(jTextFieldAddToList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAddAttacker)
                    .addComponent(jTextFieldRemoveFromList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelRemoveAttacker))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addContainerGap())
        );

        jButtonPrintInfo.getAccessibleContext().setAccessibleName("jPrintInfoButton");

        jTabbedPane.addTab("Print", jPanel3);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
        );

        jTabbedPane.getAccessibleContext().setAccessibleName("tab1Parser");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSelectLogDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectLogDirectoryActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new java.io.File("."));
        jFileChooser.setDialogTitle("Select nwnlog directory");
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        /**
         *  disable the "All files" option.
         */
        jFileChooser.setAcceptAllFileFilterUsed(false);
        
        String url = "";
        if (jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            url = jFileChooser.getSelectedFile().toString();
            url = url.replaceAll("%20", " ");
        }
        Properties defaultProperties = new Properties();
        try {
            if (!PARSER_PROPERTIES_FILE.exists()) {
                PARSER_PROPERTIES_FILE.createNewFile();
                Reader reader = new FileReader(PARSER_PROPERTIES_FILE);
                defaultProperties.load(reader);
                reader.close();
                defaultProperties.setProperty(PROPERTY_LOG_FOLDER_LOCATION, url);
                defaultProperties.store(new FileWriter(PARSER_PROPERTIES_FILE), url);
            } else {
                defaultProperties.remove(PROPERTY_LOG_FOLDER_LOCATION);
                defaultProperties.setProperty(PROPERTY_LOG_FOLDER_LOCATION, url);
                defaultProperties.store(new FileWriter(PARSER_PROPERTIES_FILE), url);
            }
        } catch (IOException ex) {
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonSelectLogDirectoryActionPerformed

    static File getCombatLog() {
        return combatLog;
    }
    
    /*
     * Creates a new file with incremented log number. Tests if this file exists and whether it is newer than the current one.
     * "newer" ? newFile : oldFile
     */
    public static File searchForNewInputFile() {
        newFile = new File(install_path + LOG_FILENAME + (logNumber + 1) + TEXT_FILE_EXTENSION);
//        System.out.println(currentFile + ": " + currentFile.lastModified());
//        System.out.println(newFile + ": " + newFile.lastModified());
        if(newFile.exists() && (newFile.lastModified() > combatLog.lastModified())){ //29.06.2012 - changed from >= to > because it really shouldn't matter and > is a safer choice when avoiding duplicate file entries
            setLogNumber(logNumber + 1);
            combatLog = newFile;
            return newFile;
        }
        else return combatLog;
    }

    /**
     * Method to check login name in nwnplayer.ini
     * @return Player username, or empty string if file not found
     */
    public static String readUsernameFromFile() {
        File nwnplayer_ini = new File(install_path + INI_PLAYER_FILENAME + INI_FILE_EXTENSION);
        try {
            return tryToReadUsername(nwnplayer_ini);
        } catch (IOException ex) {
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, "Failed to read username. Verify path to nwnplayer.ini: " + nwnplayer_ini, ex);
            return "";
        }
    }

    private static String tryToReadUsername(File nwnplayer_ini) throws FileNotFoundException, IOException {
        IniProperties nwnplayerProperties = new IniProperties();
        try (FileInputStream in = new FileInputStream(nwnplayer_ini)) {
            nwnplayerProperties.load(in);
        }
        String username = nwnplayerProperties.getProperty(PROPERTY_INI_PLAYERNAME);
        return username;
    }

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed
        if (parser != null) {
            parser.stopUpdate();
            parser.clear();
        }
        runParser(1);
    }//GEN-LAST:event_jButtonStartActionPerformed

    private void jTextFieldUniquesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUniquesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUniquesActionPerformed

    private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
        if (parser != null) parser.clear();
    }//GEN-LAST:event_jButtonResetActionPerformed

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        if(parser != null) parser.stopUpdate();
    }//GEN-LAST:event_jButtonStopActionPerformed

    private void jTextFieldTotalKillsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTotalKillsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTotalKillsActionPerformed

    private void jTextFieldAddToListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldAddToListActionPerformed
        if(parser != null){
            parser.addToAttackerList(jTextFieldAddToList.getText());
            jTextFieldAddToList.setText("");
            jButtonPrintInfoActionPerformed(evt);
        }
}//GEN-LAST:event_jTextFieldAddToListActionPerformed

    private void jButtonResetPartylistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetPartylistActionPerformed
        if(parser != null) parser.clearParty();
}//GEN-LAST:event_jButtonResetPartylistActionPerformed

    private void jCheckBoxAutoCheckPartyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAutoCheckPartyActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jCheckBoxAutoCheckPartyActionPerformed

    private void jButtonPrintInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintInfoActionPerformed
        if(parser != null) parser.printToTextArea();
}//GEN-LAST:event_jButtonPrintInfoActionPerformed

    private void jTextFieldRemoveFromListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldRemoveFromListActionPerformed
        if(parser != null){
            parser.removeFromAttackerList(jTextFieldRemoveFromList.getText());
            jTextFieldRemoveFromList.setText("");
            jButtonPrintInfoActionPerformed(evt);
        }
    }//GEN-LAST:event_jTextFieldRemoveFromListActionPerformed

    private void jButtonCrashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCrashActionPerformed
        if (parser != null) {
            parser.stopUpdate();
            parser.restart();
        }
    }//GEN-LAST:event_jButtonCrashActionPerformed

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_formPropertyChange

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            saveFrame();
        } catch (IOException ex) {
            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private class GeneralStatsTableModel extends AbstractTableModel implements Parser.ParserListener {

        private List<Attacker> attackers = new ArrayList<Attacker>();
        private String[] columnNames = {"Name", "Damage Dealt", "Attack Bonus", "Armor Class", "Hit Percentage", "Damage Taken", "XP Gained"};

        public GeneralStatsTableModel(List<Attacker> attackers) {
            this.attackers = attackers;
        }

        private GeneralStatsTableModel() {
        }

        public void updateAttackers(List<Attacker> attackers) {
            this.attackers = attackers;
            fireTableDataChanged();
            jTextFieldUniques.setText(parser.displayUniques());
            jTextFieldTotalKills.setText(parser.displayTotalKills());
        }

        public int getRowCount() {
            if (attackers == null) {
                return 0;
            } else {
                return attackers.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount()) {
                throw new IllegalStateException("Illegal row index");
            }
            Attacker attacker = attackers.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return attacker.getName();

                case 1:
                    return attacker.getTotalDamage();

                case 2:
                    return attacker.getAB();

                case 3:
                    return attacker.getArmorClass();

                case 4:
                    return attacker.percentageHit();

                case 5:
                    return attacker.getTotalDamageTaken();
                    
                case 6:
                    return attacker.getXpGained();

                default:
                    return "No input";
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        public void dataUpdated(ArrayList<Attacker> attackers) {
            updateAttackers(attackers);
        }
    }

    private class DamageDealtTableModel extends AbstractTableModel implements Parser.ParserListener {

        private List<Attacker> attackers = new ArrayList<Attacker>();
        private String[] columnNames = {"Name", "Total", "Per Hit", "Physical", "Acid", "Cold", "Divine", "Electrical",
                                    "Fire", "Magical", "Negative", "Positive", "Sonic", "Kills", "XP Gained", "Max Hit"};

        public DamageDealtTableModel(List<Attacker> attackers) {
            this.attackers = attackers;
        }

        private DamageDealtTableModel() {
        }

        public void updateAttackers(List<Attacker> attackers) {
            this.attackers = attackers;
            fireTableDataChanged();
            jTextFieldUniques.setText(parser.displayUniques());
            jTextFieldTotalKills.setText(parser.displayTotalKills());
        }

        public int getRowCount() {
            if (attackers == null) {
                return 0;
            } else {
                return attackers.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount()) {
                throw new IllegalStateException("Illegal row index");
            }
            Attacker attacker = attackers.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return attacker.getName();

                case 1:
                    return attacker.getTotalDamage();

                case 2:
                    return attacker.getAverageDamage();

                case 3:
                    return attacker.getPhysicalDamage();

                case 4:
                    return attacker.getAcidDamage();

                case 5:
                    return attacker.getColdDamage();

                case 6:
                    return attacker.getDivineDamage();

                case 7:
                    return attacker.getElectricalDamage();

                case 8:
                    return attacker.getFireDamage();

                case 9:
                    return attacker.getMagicalDamage();

                case 10:
                    return attacker.getNegativeDamage();

                case 11:
                    return attacker.getPositiveDamage();

                case 12:
                    return attacker.getSonicDamage();

                case 13:
                    return attacker.getKills();

                case 14:
                    return attacker.getXpGained();

                case 15:
                    return attacker.getMaxNormalHit();

                default:
                    return "No input";
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        public void dataUpdated(ArrayList<Attacker> attackers) {
            updateAttackers(attackers);
        }
    }

    private class DamageTakenTableModel extends AbstractTableModel implements Parser.ParserListener {

        private List<Attacker> attackers = new ArrayList<Attacker>();
        private String[] columnNames = {"Name", "Total", "Average HP", "Physical", "Acid", "Cold", "Divine", "Electrical", "Fire",
                                        "Magical", "Negative", "Positive", "Sonic", "Max Hit", "Deaths"};

        public DamageTakenTableModel(List<Attacker> attackers) {
            this.attackers = attackers;
        }

        private DamageTakenTableModel() {
        }

        public void updateAttackers(List<Attacker> attackers) {
            this.attackers = attackers;
            fireTableDataChanged();
            jTextFieldUniques.setText(parser.displayUniques());
            jTextFieldTotalKills.setText(parser.displayTotalKills());
        }

        public int getRowCount() {
            if (attackers == null) {
                return 0;
            } else {
                return attackers.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount()) {
                throw new IllegalStateException("Illegal row index");
            }
            Attacker attacker = attackers.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return attacker.getName();

                case 1:
                    return attacker.getTotalDamageTaken();

                case 2:
                    return attacker.averageHitPoints();

                case 3:
                    return attacker.getPhysicalDamageTaken();

                case 4:
                    return attacker.getAcidDamageTaken();

                case 5:
                    return attacker.getColdDamageTaken();

                case 6:
                    return attacker.getDivineDamageTaken();

                case 7:
                    return attacker.getElectricalDamageTaken();

                case 8:
                    return attacker.getFireDamageTaken();

                case 9:
                    return attacker.getMagicalDamageTaken();

                case 10:
                    return attacker.getNegativeDamageTaken();

                case 11:
                    return attacker.getPositiveDamageTaken();

                case 12:
                    return attacker.getSonicDamageTaken();

                case 13:
                    return attacker.getMaxDamageTaken();

                case 14:
                    return attacker.getDeaths();

                default:
                    return "No input";
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        public void dataUpdated(ArrayList<Attacker> attackers) {
            updateAttackers(attackers);
        }
    }

    private class DamageReducedTableModel extends AbstractTableModel implements Parser.ParserListener {

        private List<Attacker> attackers = new ArrayList<Attacker>();
        private String[] columnNames = {"Name", "Resisted", "Reduced", "Physical", "Acid", "Cold", "Divine", "Electrical", "Fire",
                                        "Magical", "Negative", "Positive", "Sonic"};

        public DamageReducedTableModel(List<Attacker> attackers) {
            this.attackers = attackers;
        }

        private DamageReducedTableModel() {
        }

        public void updateAttackers(List<Attacker> attackers) {
            this.attackers = attackers;
            fireTableDataChanged();
            jTextFieldUniques.setText(parser.displayUniques());
            jTextFieldTotalKills.setText(parser.displayTotalKills());
        }

        public int getRowCount() {
            if (attackers == null) {
                return 0;
            } else {
                return attackers.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount()) {
                throw new IllegalStateException("Illegal row index");
            }
            Attacker attacker = attackers.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return attacker.getName();

                case 1:
                    return attacker.getDamageResisted();

                case 2:
                    return attacker.getDamageReduced();

                case 3:
                    return attacker.getDamageImmunityPhysical();

                case 4:
                    return attacker.getDamageImmunityAcid();

                case 5:
                    return attacker.getDamageImmunityCold();

                case 6:
                    return attacker.getDamageImmunityDivine();

                case 7:
                    return attacker.getDamageImmunityElectrical();

                case 8:
                    return attacker.getDamageImmunityFire();

                case 9:
                    return attacker.getDamageImmunityMagical();

                case 10:
                    return attacker.getDamageImmunityNegative();

                case 11:
                    return attacker.getDamageImmunityPositive();

                case 12:
                    return attacker.getDamageImmunitySonic();

                default:
                    return "No input";
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        public void dataUpdated(ArrayList<Attacker> attackers) {
            updateAttackers(attackers);
        }
    }

    private class HitPercentageTableModel extends AbstractTableModel implements Parser.ParserListener {

        private List<Attacker> attackers = new ArrayList<Attacker>();
        private String[] columnNames = {"Name", "Total attempts", "Total hits landed", "Regular hits", "Auto hits", "Critical hits",
                                        "Total missed hits", "Regular missed", "Auto missed", "Concealed hits", "Hit %", "Miss %",
                                        "Critical Hit % (of total)", "Critical Hit % (of hits)", "Conceal %", "AC", "Highest ab", "Highest total ab",
                                        "Lowest ab", "Lowest total ab"};

        public HitPercentageTableModel(List<Attacker> attackers) {
        this.attackers = attackers;
        }

        private HitPercentageTableModel() {
        }

        public void updateAttackers(List<Attacker> attackers) {
            this.attackers = attackers;
            fireTableDataChanged();
            jTextFieldUniques.setText(parser.displayUniques());
            jTextFieldTotalKills.setText(parser.displayTotalKills());
        }

        public int getRowCount() {
            if (attackers == null) {
                return 0;
            } else {
                return attackers.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount()) {
                throw new IllegalStateException("Illegal row index");
            }
            Attacker attacker = attackers.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return attacker.getName();

                case 1:
                    return attacker.getNumberOfOutgoingAttacks();

                case 2:
                    return attacker.getTotalHitsLanded();

                case 3:
                    return attacker.getHasHit();

                case 4:
                    return attacker.getAutoHit();

                case 5:
                    return attacker.getCriticalHit();

                case 6:
                    return attacker.getTotalMissedHits();

                case 7:
                    return attacker.getHasMissed();

                case 8:
                    return attacker.getAutoMiss();

                case 9:
                    return attacker.getConcealedOutgoingAttacks();

                case 10:
                    return attacker.percentageHit();

                case 11:
                    return attacker.percentageMissOfTotal();

                case 12:
                    return attacker.percentageCriticalHitOfTotal();

                case 13:
                    return attacker.percentageCriticalHitOfHits();

                case 14:
                    return attacker.percentageConceal();

                case 15:
                    return attacker.getArmorClass();

                case 16:
                    return attacker.getMaxAB();

                case 17:
                    return attacker.getHighestTotalAB();

                case 18:
                    return attacker.getLowestAB();

                case 19:
                    return attacker.getLowestTotalAB();

                default:
                    return "No input";
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        public void dataUpdated(ArrayList<Attacker> attackers) {
            updateAttackers(attackers);
        }
    }

    private class SpellsAndSavesTableModel extends AbstractTableModel implements Parser.ParserListener {

        private List<Attacker> attackers = new ArrayList<Attacker>();
        private String[] columnNames = {"Name", "Fortitude", "Reflex", "Will", "Successful Saves", "Failed Saves"};

        public SpellsAndSavesTableModel(List<Attacker> attackers) {
            this.attackers = attackers;
        }

        private SpellsAndSavesTableModel() {
        }

        public void updateAttackers(List<Attacker> attackers) {
            this.attackers = attackers;
            fireTableDataChanged();
            jTextFieldUniques.setText(parser.displayUniques());
            jTextFieldTotalKills.setText(parser.displayTotalKills());
        }

        public int getRowCount() {
            if (attackers == null) {
                return 0;
            } else {
                return attackers.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= getRowCount()) {
                throw new IllegalStateException("Illegal row index");
            }
            Attacker attacker = attackers.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return attacker.getName();

                case 1:
                    return attacker.getFortitudeSave();

                case 2:
                    return attacker.getReflexSave();

                case 3:
                    return attacker.getWillSave();

                case 4:
                    return attacker.getNumberOfSuccessfulSaves();

                case 5:
                    return attacker.getNumberOfFailedSaves();

                default:
                    return "No input";
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        public void dataUpdated(ArrayList<Attacker> attackers) {
            updateAttackers(attackers);
        }
    }

//    ListDataListener method
    public void intervalAdded(ListDataEvent e) {
//        throw new UnsupportedOperationException();
    }
//    ListDataListener method
    public void intervalRemoved(ListDataEvent e) {
//        throw new UnsupportedOperationException();
    }
//    ListDataListener method
    public void contentsChanged(ListDataEvent e) {
//        throw new UnsupportedOperationException();
    }
    
    public void setAbstractPartyListModel(AbstractParyListModel partyListModel){
        abstractParyListModel = partyListModel;
        abstractParyListModel.addListDataListener(this);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ParserView().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCrash;
    private javax.swing.JButton jButtonPrintInfo;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JButton jButtonResetPartylist;
    private javax.swing.JButton jButtonSelectLogDirectory;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JButton jButtonStop;
    public static javax.swing.JCheckBox jCheckBoxAutoCheckParty;
    private javax.swing.JLabel jLabelAddAttacker;
    private javax.swing.JLabel jLabelRemoveAttacker;
    private javax.swing.JList jListPartyOverview;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JScrollPane jScrollPaneDamageReduced;
    private javax.swing.JScrollPane jScrollPaneDamageTaken;
    private javax.swing.JScrollPane jScrollPaneGeneralStats;
    private javax.swing.JScrollPane jScrollPaneHitPercentage;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableDamageDealt;
    private javax.swing.JTable jTableDamageReduced;
    private javax.swing.JTable jTableDamageTaken;
    private javax.swing.JTable jTableGeneralStats;
    private javax.swing.JTable jTableHitPercentage;
    private javax.swing.JTable jTableSpellsAndSaves;
    public static javax.swing.JTextArea jTextAreaPrintInfo;
    public static javax.swing.JTextArea jTextAreaSpellInfo;
    private javax.swing.JTextField jTextFieldAddToList;
    private javax.swing.JTextField jTextFieldRemoveFromList;
    private java.awt.TextField jTextFieldTotalKills;
    private java.awt.TextField jTextFieldUniques;
    public static javax.swing.JTextField jTextFieldUptime;
    public static javax.swing.JTextPane jTextPaneLoot;
    public static javax.swing.JTextPane jTextPaneServerInfo;
    // End of variables declaration//GEN-END:variables
}
