/*
 * Copyright (C) 2023 Roger Lovera <rloverab@yahoo.es>
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
package Ocr;

import Classes.Task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Roger Lovera <rloverab@yahoo.es>
 */
public class OcrRunnable implements Runnable {

    private final Task task;
    private final DefaultTableModel dtm;
    private final File file;
    private final String tempPath;
    private final String ocrPath;

    public OcrRunnable(Task task, DefaultTableModel dtm, File file, String tempPath, String ocrPath) {
        this.task = task;
        this.dtm = dtm;
        this.file = file;
        this.tempPath = tempPath;
        this.ocrPath = ocrPath;
    }

    @Override
    public void run() {
        execute();
    }

    private void execute() {
        File folderTemp;
        File folderOcr;

        folderTemp = new File(tempPath);
        folderOcr = new File(ocrPath);

        if (!folderTemp.exists()) {
            folderTemp.mkdirs();
        }

        folderTemp = null;

        if (!folderOcr.exists()) {
            folderOcr.mkdirs();
        }

        folderOcr = null;

        if (file.exists()) {
            File ocrPdf;
            String pathname;

            pathname = file.getPath();
//            file = null;
            System.out.println("ocrPDF");
            ocrPdf = PDFReader.createPdf(pathname, tempPath, ocrPath, 200);

            if (ocrPdf != null && ocrPdf.exists()) {
                task.setStatusOcr(Task.OK);
            } else {
                task.setStatusOcr(Task.ERROR);
            }
            dtm.setValueAt(task, task.getRow(), 0);
        }
    }
}
