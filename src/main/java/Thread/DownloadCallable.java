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

import Application.MDIMain;
import Classes.Task;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Roger Lovera <rloverab@yahoo.es>
 */
public class DownloadCallable implements Callable<Task> {

    private final Task task;
    private final CountDownLatch countDownLatch;
    private final String outputPath;

    public DownloadCallable(Task task, CountDownLatch countDownLatch, String outputPath) {
        this.task = task;
        this.countDownLatch = countDownLatch;
        this.outputPath = outputPath;
    }

    public DownloadCallable(Task task, String outputPath) {
        this.task = task;
        this.outputPath = outputPath;
        this.countDownLatch = null;
    }

    @Override
    public Task call() throws Exception {
        Task task;

        task = download();

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        
        return task;
    }

    private Task download() {
        String regexp;
        Pattern pattern;
        Matcher matcher;
//        String pathOriginals;
        File folder;

        regexp = "^(.+/repo/)(.+)(/\\d+-)(.+)(-\\d+-\\d+.+)$";
        pattern = Pattern.compile(regexp);
//        pathOriginals = "Originals";
        folder = new File(outputPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

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
                    File destination;

                    filename = outputPath + File.separator + task.getFilename();
                    destination = new File(filename);
                    FileUtils.copyURLToFile(new URL(url.toString()), destination);
                    task.setStatusDownload(Task.OK);
                    break;
                } catch (MalformedURLException ex) {
                    task.setStatusDownload(Task.ERROR);
                } catch (IOException ex) {
                    task.setStatusDownload(Task.ERROR);
                }
            }
        }

        return task;
    }
}
