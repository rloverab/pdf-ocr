/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ocr;

import Utils.GenerateCode;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.PdfBoxUtilities;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author rlove
 */
public class PDFReader {

    public static List<BufferedImage> readPDF(String pdfFilePath, float dpi) throws IOException {
        return readPDF(new File(pdfFilePath), dpi);
    }

    public static List<BufferedImage> readPDF(File file, float dpi) throws IOException {
        List<BufferedImage> pages;
        PDDocument document;
        PDFRenderer renderer;
        int numPages;

        pages = new ArrayList<>();
        document = PDDocument.load(file);
        numPages = document.getNumberOfPages();
        renderer = new PDFRenderer(document);

        for (int pageIndex = 0; pageIndex < numPages; pageIndex++) {
            pages.add(renderer.renderImageWithDPI(pageIndex, dpi));
        }

        document.close();
        return pages;
    }

    public static File createPdf(String pathname, String tempPath, String outputPath, float dpi) {
        CountDownLatch countDownLatch;
        ExecutorService executorService;
        List<File> tempFiles;
        File inputFile;
        File outputFile;
        File folderOcr;
        ITesseract tesseract;
        List<ITesseract.RenderedFormat> formats;
        Pattern pattern = Pattern.compile("^(.+)\\.[^.]+$");
        Matcher matcher;
        String pathTemp;
        String pathOcr;
        String formatPathname;

        outputFile = null;
        tesseract = new Tesseract();   // JNA Interface Mapping
        tesseract.setDatapath("tessdata"); // path to tessdata directory
        tesseract.setLanguage("spa");
        tesseract.setVariable("user_defined_dpi", Float.toString(dpi));
        formats = new ArrayList<>();
        formats.add(ITesseract.RenderedFormat.PDF);
        inputFile = new File(pathname);
        matcher = pattern.matcher(inputFile.getName());
        pathTemp = tempPath + File.separator;
        pathOcr = outputPath + File.separator;
        folderOcr = new File(pathOcr);
        tempFiles = new ArrayList<>();

        if (matcher.matches()) {
            String filename;

            filename = matcher.group(1);

            try {
                List<BufferedImage> pages;

                pages = readPDF(inputFile, dpi);
                formatPathname = "%s%s-%0" + Integer.toString(pages.size()).length() + "d";

                if (!pages.isEmpty()) {
                    String tempFilename;
                    
                    countDownLatch = new CountDownLatch(pages.size());
                    executorService = Executors.newFixedThreadPool(3);
                    tempFilename = GenerateCode.generateCode();
                    
                    for (int i = pages.size() - 1; i >= 0; i--) {
                        CreatePDFRunnable runnable;

                        runnable = new CreatePDFRunnable(
                                "spa",
                                dpi,
                                formats,
                                pages.get(i),
                                (i + 1),
                                pathTemp,
                                tempFilename,
                                formatPathname,
                                tempFiles,
                                countDownLatch);
                        executorService.execute(runnable);
                        pages.remove(i);
                    }

                    try {
                        countDownLatch.await();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    executorService.shutdown();

                    if (!folderOcr.exists()) {
                        folderOcr.mkdirs();
                    }

                    Collections.sort(tempFiles, Comparator.comparing(File::getName));
                    outputFile = new File(pathOcr + filename + ".pdf");
                    PdfBoxUtilities.mergePdf(tempFiles.toArray(new File[tempFiles.size()]), outputFile);

                    for (File file : tempFiles) {
                        file.delete();
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return outputFile;
    }

    public static void createPdfV1(String pathname, String outputPath, float dpi) {
        List<BufferedImage> pages;
        List<File> tempFiles;
        File inputFile;
        File folderOcr;
        ITesseract instance;
        List<ITesseract.RenderedFormat> formats;
        Pattern pattern = Pattern.compile("^(.+)\\.[^.]+$");
        Matcher matcher;
        String filename;
        String pathOriginal;
        String pathOcr;
        String format;

        instance = new Tesseract();   // JNA Interface Mapping
        instance.setDatapath("tessdata"); // path to tessdata directory
        instance.setLanguage("spa");
        instance.setVariable("user_defined_dpi", Float.toString(dpi));
        formats = new ArrayList<>();
        formats.add(ITesseract.RenderedFormat.PDF);
        inputFile = new File(pathname);
        tempFiles = new ArrayList<>();
        pages = new ArrayList<>();
        format = "%s%s-%0" + Integer.toString(pages.size()).length() + "d";
        System.out.println("format: " + format);
        matcher = pattern.matcher(inputFile.getName());
        pathOriginal = inputFile.getParent() + File.separator;
        pathOcr = pathOriginal + "Convertidos" + File.separator;
        folderOcr = new File(pathOcr);

        if (matcher.matches()) {
            List<String> outputBases;

            outputBases = new ArrayList<>();
            filename = matcher.group(1);
            System.out.println(filename);

            try {
                pages = readPDF(inputFile, dpi);
            } catch (IOException ex) {
                Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (int i = 0; i < pages.size(); i++) {
                String tempPathname;

                tempPathname = String.format(format, pathOriginal, filename, (i + 1));
                outputBases.add(tempPathname);
                tempFiles.add(new File(tempPathname + ".pdf"));
            }

            if (!pages.isEmpty()) {
                try {
                    instance.createDocumentsWithResults(
                            pages.toArray(new BufferedImage[pages.size()]),
                            new String[outputBases.size()],
                            outputBases.toArray(outputBases.toArray(new String[outputBases.size()])),
                            formats,
                            ITessAPI.TessPageIteratorLevel.RIL_BLOCK);
                } catch (TesseractException ex) {
                    Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

//            for (int i = 0; i < pages.size(); i++) {
//                BufferedImage page;
//                String tempPathname;
//                page = pages.get(i);
//                tempPathname = String.format(format, pathOriginal, filename, (i + 1));
//
//                try {
//                    instance.createDocumentsWithResults(
//                            page,
//                            null,
//                            tempPathname,
//                            list,
//                            ITessAPI.TessPageIteratorLevel.RIL_BLOCK);
//                    tempFiles.add(new File(tempPathname + ".pdf"));
//
//                } catch (TesseractException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
            if (!folderOcr.exists()) {
                folderOcr.mkdirs();
            }

            System.out.println(pathOcr + filename);
            PdfBoxUtilities.mergePdf(tempFiles.toArray(new File[tempFiles.size()]), new File(pathOcr + filename + ".pdf"));

            for (File file : tempFiles) {
                file.delete();
            }

            System.err.println("Pages: " + pages.size());
        }
    }
}
