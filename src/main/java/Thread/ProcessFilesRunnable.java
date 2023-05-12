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
package Thread;

import Classes.Task;
import Ocr.OcrRunnable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rlove
 */
public class ProcessFilesRunnable implements Runnable {

    private final DefaultTableModel dtm;
    private final List<JButton> jButtons;
    private boolean running;
    private final String tempPath;
    private final String ocrPath;
    private final ExecutorService executorOcr;

    public ProcessFilesRunnable(DefaultTableModel dtm, List<JButton> jButtons, String tempPath, String ocrPath) {
        this.dtm = dtm;
        this.jButtons = jButtons;
        this.tempPath = tempPath;
        this.ocrPath = ocrPath;
        this.executorOcr = Executors.newFixedThreadPool(3);
        this.running = false;
    }
    
    public void startProcessigLocal() {
        if (!running) {
            int rowCount;

            running = true;
            rowCount = dtm.getRowCount();

            for (int row = 0; row < rowCount; row++) {
                Task task;

                task = (Task) dtm.getValueAt(row, 0);

                if(task.getStatusOcr() != Task.OK && task.getStatusOcr() != Task.ERROR){
                    task.setRow(row);
                    executorOcr.execute(new OcrRunnable(task, dtm, task.getFile(), tempPath, ocrPath));
                }
            }

            while (!completed()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProcessFilesRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void stopProcessingLocal() {
        running = false;
        executorOcr.shutdown();
    }
    
    public boolean completed() {
        int rowCount;
        int taskCompleted;

        rowCount = dtm.getRowCount();
        taskCompleted = 0;

        for (int row = 0; row < rowCount; row++) {
            Task task;
            boolean cond;

            task = (Task) dtm.getValueAt(row, 0);
            cond = task.getStatusOcr() == Task.OK || task.getStatusOcr() == Task.ERROR;
            taskCompleted += cond ? 1 : 0;
        }

        return rowCount == taskCompleted;
    }

    @Override
    public void run() {
        jButtons.forEach(e -> e.setEnabled(false));
        startProcessigLocal();
        stopProcessingLocal();
        jButtons.forEach(e -> e.setEnabled(true));
        
    }
}
