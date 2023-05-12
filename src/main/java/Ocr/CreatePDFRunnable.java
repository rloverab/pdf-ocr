/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ocr;

import Utils.CharsetTools;
import Utils.GenerateCode;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author rlove
 */
public class CreatePDFRunnable implements Runnable {

    private final String lang;
    private final float dpi;
    private final List<ITesseract.RenderedFormat> formats;
    private final BufferedImage page;
    private final int numPage;
    private final String path;
    private final String filename;
    private final String formatPathname;
    private final List<File> tempFiles;
    private final CountDownLatch countDownLatch;

    public CreatePDFRunnable(String lang, float dpi, List<ITesseract.RenderedFormat> formats, BufferedImage page, int numPage, String path, String filename, String formatPathname, List<File> tempFiles, CountDownLatch countDownLatch) {
        this.lang = lang;
        this.dpi = dpi;
        this.formats = formats;
        this.page = page;
        this.numPage = numPage;
        this.path = path;
        this.filename = filename;
        this.formatPathname = formatPathname;
        this.tempFiles = tempFiles;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        ITesseract tesseract;
        String pathname;
        File folderTemp;
        String tempFilename;

        folderTemp = new File(path);

        if (!folderTemp.exists()) {
            folderTemp.mkdirs();
        }

        tesseract = new Tesseract();   // JNA Interface Mapping
        tesseract.setDatapath("tessdata"); // path to tessdata directory
        tesseract.setLanguage(lang);
        tesseract.setVariable("user_defined_dpi", Float.toString(dpi));
        pathname = String.format(formatPathname, path, filename, numPage);
        System.out.println(pathname);
        try {
            File tempFile;
            
            tesseract.createDocumentsWithResults(page,
                    null,
                    pathname,
                    formats,
                    ITessAPI.TessPageIteratorLevel.RIL_BLOCK);
            tempFile = new File(pathname + ".pdf");
            tempFiles.add(tempFile);

        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

        countDownLatch.countDown();
    }

}
