/*
 * Copyright (C) 2023 rlove
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package Application;

import Classes.Task;
import Thread.ProcessFilesRunnable;
import Thread.ProcessUrlsRunnable;
import com.vdurmont.emoji.EmojiParser;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author rlove
 */
public class MDIMain extends javax.swing.JFrame {

    /**
     * Creates new form MDIMainV2
     */
    public MDIMain() {
        initComponents();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.png")));
        tblTasksUrls.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                Task task;
                int row;
                int column;
                int progress;

                row = e.getFirstRow();
                column = e.getColumn();
                progress = 0;

                if (column == 0) {
                    task = (Task) tblTasksUrls.getValueAt(row, 0);
                    tblTasksUrls.setValueAt(task.getUrl(), row, 1);
                    tblTasksUrls.setValueAt(task.getFilename(), row, 2);
                    tblTasksUrls.setValueAt(getEmoji(task.getStatusDownload()), row, 3);
                    tblTasksUrls.setValueAt(getEmoji(task.getStatusOcr()), row, 4);

                }

                for (int r = 0; r < tblTasksUrls.getRowCount(); r++) {
                    Task t;

                    t = (Task) tblTasksUrls.getValueAt(r, 0);
                    progress += t.getStatusDownload() == Task.OK || t.getStatusDownload() == Task.ERROR ? 1 : 0;
                    progress += t.getStatusOcr() == Task.OK || t.getStatusOcr() == Task.ERROR ? 1 : 0;
                    barProgressUrls.setValue(progress);
                }

                if (barProgressUrls.getValue() == barProgressUrls.getMaximum()) {
                    barProgressUrls.setValue(0);
                }
            }
        });
        tblTasksUrls.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTasksUrls.setRowSelectionAllowed(true);
        tblTasksUrls.setColumnSelectionAllowed(false);
        fixColumnWidthsTableTasksUrls();
        tblTasksLocal.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                Task task;
                int row;
                int column;
                int progress;

                row = e.getFirstRow();
                column = e.getColumn();
                progress = 0;

                if (column == 0) {
                    task = (Task) tblTasksLocal.getValueAt(row, 0);
                    tblTasksLocal.setValueAt(task.getFile().getPath(), row, 1);
                    tblTasksLocal.setValueAt(task.getFilename(), row, 2);
                    tblTasksLocal.setValueAt(getEmoji(task.getStatusOcr()), row, 3);
                }

                for (int r = 0; r < tblTasksLocal.getRowCount(); r++) {
                    Task t;

                    t = (Task) tblTasksLocal.getValueAt(r, 0);
                    progress += t.getStatusOcr() == Task.OK || t.getStatusOcr() == Task.ERROR ? 1 : 0;
                    barProgressLocal.setValue(progress);
                }

                if (barProgressLocal.getValue() == barProgressLocal.getMaximum()) {
                    barProgressLocal.setValue(0);
                }
            }
        });
        tblTasksLocal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTasksLocal.setRowSelectionAllowed(true);
        tblTasksLocal.setColumnSelectionAllowed(false);
        
        fixColumnWidthsTableTasksLocal();
    }

    private void alignCellTableTasks(JTable table) {
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            DefaultTableCellRenderer headerRenderer;

            headerRenderer = (DefaultTableCellRenderer) table.getColumnModel().getColumn(i).getHeaderRenderer();
            headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            if (i >= 3) {
                DefaultTableCellRenderer cellRenderer;

                cellRenderer = (DefaultTableCellRenderer) table.getColumnModel().getColumn(i).getCellRenderer();
                cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            }
        }
    }

    private void fixColumnWidthsTableTasksUrls() {
        int[] columnWidths = {0, 0, -1, 80, 80};
        TableColumnModel tcm;
        TableColumnModel tcmh;

        tcm = tblTasksUrls.getColumnModel();
        tcmh = tblTasksUrls.getTableHeader().getColumnModel();

        for (int i = 0; i < columnWidths.length; i++) {
            if (columnWidths[i] >= 0) {
                tcm.getColumn(i).setPreferredWidth(columnWidths[i]);
                tcm.getColumn(i).setMaxWidth(columnWidths[i]);
                tcm.getColumn(i).setMinWidth(columnWidths[i]);
                tcmh.getColumn(i).setPreferredWidth(columnWidths[i]);
                tcmh.getColumn(i).setMaxWidth(columnWidths[i]);
                tcmh.getColumn(i).setMinWidth(columnWidths[i]);
            }
        }
    }

    private void fixColumnWidthsTableTasksLocal() {
        int[] columnWidths = {0, 0, -1, 80};
        TableColumnModel tcm;
        TableColumnModel tcmh;

        tcm = tblTasksLocal.getColumnModel();
        tcmh = tblTasksLocal.getTableHeader().getColumnModel();

        for (int i = 0; i < columnWidths.length; i++) {
            if (columnWidths[i] >= 0) {
                tcm.getColumn(i).setPreferredWidth(columnWidths[i]);
                tcm.getColumn(i).setMaxWidth(columnWidths[i]);
                tcm.getColumn(i).setMinWidth(columnWidths[i]);
                tcmh.getColumn(i).setPreferredWidth(columnWidths[i]);
                tcmh.getColumn(i).setMaxWidth(columnWidths[i]);
                tcmh.getColumn(i).setMinWidth(columnWidths[i]);
            }
        }
    }

    private int addTaskToTableUrls(List<Task> tasks) {
        DefaultTableModel dtm;
        int rows;

        dtm = (DefaultTableModel) tblTasksUrls.getModel();
        rows = 0;

        for (int rowIndex = 0; rowIndex < tblTasksUrls.getRowCount(); rowIndex++) {
            Task _task;

            _task = (Task) dtm.getValueAt(rowIndex, 0);
            tasks.removeIf(task -> task.getUrl().equals(_task.getUrl()));
        }

        for (Task task : tasks) {
            Object[] rowData = {
                task,
                task.getUrl(),
                task.getFilename(),
                getEmoji(task.getStatusDownload()),
                getEmoji(task.getStatusOcr()),};

            dtm.addRow(rowData);
            rows++;
        }

        return rows;
    }

    private String getEmoji(int status) {
        String emoji;

        switch (status) {
            case Task.OK:
                emoji = ":heavy_check_mark:";
                break;
            case Task.ERROR:
                emoji = ":x:";
                break;
            case Task.PROCESSING:
                emoji = ":floppy_disk:";
                break;
            default:
                emoji = "";
                break;
        }

        return EmojiParser.parseToUnicode(emoji);
    }

    private File[] openFileChooser() {
        JFileChooser fileChooser;
        FileNameExtensionFilter filter;
        File[] files;
        int selection;

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        filter = new FileNameExtensionFilter("Archivos PDF", "pdf");
        fileChooser.setFileFilter(filter);
        selection = fileChooser.showOpenDialog(btnAddLinksLocal);
        files = new File[0];

        if (selection == JFileChooser.APPROVE_OPTION) {
            files = fileChooser.getSelectedFiles();
        }

        return files;
    }

    private void openExplorer(String folderPath) throws IOException{
        File folder;
        
        folder = new File(folderPath);
        
        if(!folder.exists()){
            folder.mkdirs();
        }
        
        if(Desktop.isDesktopSupported()){
            Desktop desktop;
            
            desktop = Desktop.getDesktop();
            desktop.open(folder);
        }
    }
    
    private int addTaskToTableLocal(File[] files) {
        DefaultTableModel dtm;
        int rows;

        dtm = (DefaultTableModel) tblTasksLocal.getModel();
        rows = 0;

        for (File file : files) {
            boolean exists;

            exists = false;

            for (int r = 0; r < tblTasksLocal.getRowCount(); r++) {
                Task t;

                t = (Task) tblTasksLocal.getValueAt(r, 0);

                if (file.getPath().equalsIgnoreCase(t.getFile().getPath())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                Task task;
                Object[] objects;

                task = new Task(file);
                objects = new Object[]{
                    task,
                    task.getFile().getParent(),
                    task.getFilename(),
                    getEmoji(task.getStatusOcr())
                };
                dtm.addRow(objects);
                rows++;
            }
        }

        return rows;
    }

    private void removeRow(JTable table) {
        int selectedRow;

        selectedRow = table.getSelectedRow();

        if (selectedRow >= 0) {
            DefaultTableModel dtm;

            dtm = (DefaultTableModel) table.getModel();
            dtm.removeRow(selectedRow);
        }
    }

    private void removeAllRows(JTable table) {
        DefaultTableModel dtm;

        dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
    }

    private void executeProcessUrls() {
        Thread thread;
        DefaultTableModel dtm;
        ProcessUrlsRunnable pur;
        List<JButton> jButtons;

        jButtons = new ArrayList<>();
        jButtons.add(btnAddLinksUrls);
        jButtons.add(btnClearListUrls);
        jButtons.add(btnDeleteUrl);
        jButtons.add(btnProcessUrls);
        barProgressUrls.setMinimum(0);
        barProgressUrls.setMaximum(tblTasksUrls.getRowCount() * 2);
        barProgressUrls.setValue(0);
        dtm = (DefaultTableModel) tblTasksUrls.getModel();
        pur = new ProcessUrlsRunnable(dtm, jButtons, "Descargas", "Temp", "Ocr");
        thread = new Thread(pur);
        thread.start();
    }

    private void executeProcessLocal() {
        Thread thread;
        DefaultTableModel dtm;
        ProcessFilesRunnable pfr;
        List<JButton> jButtons;

        jButtons = new ArrayList<>();
        jButtons.add(btnAddLinksLocal);
        jButtons.add(btnClearListLocal);
        jButtons.add(btnDeleteFile);
        jButtons.add(btnProcessLocal);
        barProgressLocal.setMinimum(0);
        barProgressLocal.setMaximum(tblTasksLocal.getRowCount());
        barProgressLocal.setValue(0);
        dtm = (DefaultTableModel) tblTasksLocal.getModel();
        pfr = new ProcessFilesRunnable(dtm, jButtons, "Temp", "Ocr");
        thread = new Thread(pfr);
        thread.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabUrls = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTasksUrls = new javax.swing.JTable();
        btnAddLinksUrls = new javax.swing.JButton();
        btnDeleteUrl = new javax.swing.JButton();
        btnClearListUrls = new javax.swing.JButton();
        btnProcessUrls = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        barProgressUrls = new javax.swing.JProgressBar();
        tabLocal = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTasksLocal = new javax.swing.JTable();
        btnAddLinksLocal = new javax.swing.JButton();
        btnDeleteFile = new javax.swing.JButton();
        btnClearListLocal = new javax.swing.JButton();
        btnProcessLocal = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        barProgressLocal = new javax.swing.JProgressBar();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PDF-OCR | v1.0");
        setResizable(false);

        tblTasksUrls.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Task", "URL", "Archivo", "Descargado", "OCR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTasksUrls.setColumnSelectionAllowed(true);
        tblTasksUrls.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblTasksUrls);
        tblTasksUrls.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblTasksUrls.getColumnModel().getColumnCount() > 0) {
            tblTasksUrls.getColumnModel().getColumn(0).setResizable(false);
            tblTasksUrls.getColumnModel().getColumn(1).setResizable(false);
            tblTasksUrls.getColumnModel().getColumn(2).setResizable(false);
            tblTasksUrls.getColumnModel().getColumn(3).setResizable(false);
            tblTasksUrls.getColumnModel().getColumn(4).setResizable(false);
        }

        btnAddLinksUrls.setText("Agregar enlaces");
        btnAddLinksUrls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLinksUrlsActionPerformed(evt);
            }
        });

        btnDeleteUrl.setText("Eliminar enlace");
        btnDeleteUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteUrlActionPerformed(evt);
            }
        });

        btnClearListUrls.setText("Vaciar lista");
        btnClearListUrls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearListUrlsActionPerformed(evt);
            }
        });

        btnProcessUrls.setText("Procesar enlaces");
        btnProcessUrls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessUrlsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAddLinksUrls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDeleteUrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearListUrls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcessUrls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnAddLinksUrls)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDeleteUrl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClearListUrls)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnProcessUrls)
                        .addGap(0, 145, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Progreso");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barProgressUrls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barProgressUrls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout tabUrlsLayout = new javax.swing.GroupLayout(tabUrls);
        tabUrls.setLayout(tabUrlsLayout);
        tabUrlsLayout.setHorizontalGroup(
            tabUrlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabUrlsLayout.setVerticalGroup(
            tabUrlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUrlsLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("URLs", tabUrls);

        tblTasksLocal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Task", "Ruta", "Archivo", "OCR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTasksLocal.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblTasksLocal);
        tblTasksLocal.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblTasksLocal.getColumnModel().getColumnCount() > 0) {
            tblTasksLocal.getColumnModel().getColumn(0).setResizable(false);
            tblTasksLocal.getColumnModel().getColumn(1).setResizable(false);
            tblTasksLocal.getColumnModel().getColumn(2).setResizable(false);
            tblTasksLocal.getColumnModel().getColumn(3).setResizable(false);
        }

        btnAddLinksLocal.setText("Agregar archivos");
        btnAddLinksLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLinksLocalActionPerformed(evt);
            }
        });

        btnDeleteFile.setText("Eliminar archivo");
        btnDeleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteFileActionPerformed(evt);
            }
        });

        btnClearListLocal.setText("Vaciar lista");
        btnClearListLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearListLocalActionPerformed(evt);
            }
        });

        btnProcessLocal.setText("Procesar archivos");
        btnProcessLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessLocalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAddLinksLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDeleteFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearListLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcessLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnAddLinksLocal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDeleteFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClearListLocal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnProcessLocal)
                        .addGap(0, 145, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Progreso");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barProgressLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barProgressLocal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout tabLocalLayout = new javax.swing.GroupLayout(tabLocal);
        tabLocal.setLayout(tabLocalLayout);
        tabLocalLayout.setHorizontalGroup(
            tabLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabLocalLayout.setVerticalGroup(
            tabLocalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabLocalLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Local", tabLocal);

        fileMenu.setMnemonic('a');
        fileMenu.setText("Archivo");

        jMenu1.setMnemonic('d');
        jMenu1.setText("Directorios");

        jMenuItem1.setText("Descargas");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem2.setText("OCR");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        fileMenu.add(jMenu1);
        fileMenu.add(jSeparator2);

        exitMenuItem.setMnemonic('s');
        exitMenuItem.setText("Salir");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setMnemonic('y');
        helpMenu.setText("Ayuda");

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("Acerca de...");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void btnProcessLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessLocalActionPerformed
        executeProcessLocal();
    }//GEN-LAST:event_btnProcessLocalActionPerformed

    private void btnClearListLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearListLocalActionPerformed
        removeAllRows(tblTasksLocal);
    }//GEN-LAST:event_btnClearListLocalActionPerformed

    private void btnDeleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteFileActionPerformed
        removeRow(tblTasksLocal);
    }//GEN-LAST:event_btnDeleteFileActionPerformed

    private void btnAddLinksLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLinksLocalActionPerformed
        File[] files;

        files = openFileChooser();

        if (files.length > 0) {
            int rows;

            rows = addTaskToTableLocal(files);
            JOptionPane.showMessageDialog(btnAddLinksLocal,
                    "Archivos agregados: " + rows,
                    "Agregar archivos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnAddLinksLocalActionPerformed

    private void btnProcessUrlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessUrlsActionPerformed
        executeProcessUrls();
    }//GEN-LAST:event_btnProcessUrlsActionPerformed

    private void btnClearListUrlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearListUrlsActionPerformed
        removeAllRows(tblTasksUrls);
    }//GEN-LAST:event_btnClearListUrlsActionPerformed

    private void btnDeleteUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteUrlActionPerformed
        removeRow(tblTasksUrls);
    }//GEN-LAST:event_btnDeleteUrlActionPerformed

    private void btnAddLinksUrlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLinksUrlsActionPerformed
        JDialogLinks jdl;
        List<Task> tasks;

        jdl = new JDialogLinks(this, true);
        jdl.setLocationRelativeTo(this.btnAddLinksUrls);
        jdl.setVisible(true);
        tasks = jdl.getTasks();

        if (!tasks.isEmpty()) {
            int rows;

            rows = addTaskToTableUrls(tasks);
            JOptionPane.showMessageDialog(btnAddLinksUrls,
                    "Enlaces agregados: " + rows,
                    "Agregar enlaces",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnAddLinksUrlsActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            openExplorer("Descargas");
        } catch (IOException ex) {
            Logger.getLogger(MDIMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        try {
            openExplorer("OCR");
        } catch (IOException ex) {
            Logger.getLogger(MDIMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        JDialogAbout jDialogAbout;
        
        jDialogAbout = new JDialogAbout(this, true);
        jDialogAbout.setLocationRelativeTo(this);
        jDialogAbout.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

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
            java.util.logging.Logger.getLogger(MDIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MDIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MDIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MDIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MDIMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JProgressBar barProgressLocal;
    private javax.swing.JProgressBar barProgressUrls;
    private javax.swing.JButton btnAddLinksLocal;
    private javax.swing.JButton btnAddLinksUrls;
    private javax.swing.JButton btnClearListLocal;
    private javax.swing.JButton btnClearListUrls;
    private javax.swing.JButton btnDeleteFile;
    private javax.swing.JButton btnDeleteUrl;
    private javax.swing.JButton btnProcessLocal;
    private javax.swing.JButton btnProcessUrls;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel tabLocal;
    private javax.swing.JPanel tabUrls;
    private javax.swing.JTable tblTasksLocal;
    private javax.swing.JTable tblTasksUrls;
    // End of variables declaration//GEN-END:variables

}
