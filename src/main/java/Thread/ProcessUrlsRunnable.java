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
package Thread;

import Application.MDIMain;
import Classes.Task;
import Ocr.OcrRunnable;
import Ocr.PDFReader;
import Utils.PdfTypeDetector;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Roger Lovera <rloverab@yahoo.es>
 */
public class ProcessUrlsRunnable implements Runnable {

    private final DefaultTableModel dtm;
    private int downloads;
    private final List<JButton> jButtons;
    private boolean running;
    private final String downloadPath;
    private final String tempPath;
    private final String ocrPath;
    private final ExecutorService executorDownloads;
    private final ExecutorService executorOcr;

    public ProcessUrlsRunnable(DefaultTableModel dtm, List<JButton> jButtons, String downloadPath, String tempPath, String ocrPath) {
        this.dtm = dtm;
        this.jButtons = jButtons;
        this.downloadPath = downloadPath;
        this.tempPath = tempPath;
        this.ocrPath = ocrPath;
        this.executorDownloads = Executors.newFixedThreadPool(10);
        this.executorOcr = Executors.newFixedThreadPool(3);
        this.running = false;
    }

    public int getDownloads() {
        return downloads;
    }

    @Override
    public void run() {
        jButtons.forEach(e -> e.setEnabled(false));
        startProcessigUrls();
        stopProcessingUrls();
        jButtons.forEach(e -> e.setEnabled(true));
    }

    public void startProcessigUrls() {
        if (!running) {
            int rowCount;

            running = true;
            rowCount = dtm.getRowCount();

            for (int row = 0; row < rowCount; row++) {
                Task task;

                task = (Task) dtm.getValueAt(row, 0);
                task.setRow(row);
                executorDownloads.execute(new Step01DownloadRunnable(task, dtm, downloadPath, tempPath, ocrPath, executorOcr));
            }

            while (!completed()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProcessUrlsRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void stopProcessingUrls() {
        running = false;
        executorDownloads.shutdown();
        executorOcr.shutdown();
    }

    public boolean completed() {
        int rowCount;
        int taskCompleted;

        rowCount = dtm.getRowCount();
        taskCompleted = 0;

        for (int row = 0; row < rowCount; row++) {
            Task task;
            boolean cond1;
            boolean cond2;

            task = (Task) dtm.getValueAt(row, 0);
            cond1 = task.getStatusDownload() == Task.OK && (task.getStatusOcr() == Task.OK || task.getStatusOcr() == Task.ERROR);
            cond2 = task.getStatusDownload() == Task.ERROR;
            taskCompleted += (cond1 || cond2) ? 1 : 0;
        }

        return rowCount == taskCompleted;
    }
}

class Step01DownloadRunnable implements Runnable {

    private final Task task;
    private final DefaultTableModel dtm;
    private final String downloadPath;
    private final String tempPath;
    private final String ocrPath;
    private final ExecutorService executorOcr;

    public Step01DownloadRunnable(Task task, DefaultTableModel dtm, String downloadPath, String tempPath, String ocrPath, ExecutorService executorOcr) {
        this.task = task;
        this.dtm = dtm;
        this.downloadPath = downloadPath;
        this.tempPath = tempPath;
        this.ocrPath = ocrPath;
        this.executorOcr = executorOcr;
    }

    @Override
    public void run() {
        execute();
    }

    private void execute() {
        String regexp;
        Pattern pattern;
        Matcher matcher;
        File folderDownload;
        File folderOcr;

        regexp = "^(.+\\/repo\\/)(.+)(\\/\\d+-)(\\d+|\\[object_Object\\])(.+)$";
        pattern = Pattern.compile(regexp);
        folderDownload = new File(downloadPath);
        folderOcr = new File(ocrPath);

        if (!folderDownload.exists()) {
            folderDownload.mkdirs();
        }

        folderDownload = null;

        if (!folderOcr.exists()) {
            folderOcr.mkdirs();
        }

        folderOcr = null;
        System.gc();

        for (int i = 0; i <= 4; i = i + 2) {
            StringBuilder url;

            url = new StringBuilder();
            matcher = pattern.matcher(task.getUrl());

            if (matcher.find()) {
                try {
                    url
                            .append(matcher.group(1))
                            .append(matcher.group(i == 0 ? 2 : i))
                            .append(matcher.group(3))
                            .append(matcher.group(i == 0 ? 4 : i))
                            .append(URLEncoder.encode(matcher.group(5), StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(MDIMain.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    String filename;
                    File file;

                    filename = downloadPath + File.separator + task.getFilename();
                    file = new File(filename);
                    FileUtils.copyURLToFile(new URL(url.toString()), file, 10000, 10000);
                    task.setStatusDownload(Task.OK);

                    if (task.getStatusDownload() == Task.OK) {
                        switch (PdfTypeDetector.getType(file)) {
                            case PdfTypeDetector.ONLY_IMAGES:
                                executorOcr.execute(new OcrRunnable(task, dtm, file, tempPath, ocrPath));
                                break;
                            case PdfTypeDetector.WITH_TEXT:
                                File target;

                                target = new File(ocrPath + File.separator + file.getName());
                                Files.move(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                task.setStatusOcr(Task.OK);
                                break;
                            default:
                                task.setStatusOcr(Task.ERROR);
                        }

                    } else {
                        task.setStatusOcr(Task.ERROR);
                    }

                    break;
                } catch (MalformedURLException ex) {
                    System.err.println(ex.getMessage());
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

        if (task.getStatusDownload() != Task.OK) {
            task.setStatusDownload(Task.ERROR);
            task.setStatusOcr(Task.ERROR);
        }

        dtm.setValueAt(task, task.getRow(), 0);
    }
}