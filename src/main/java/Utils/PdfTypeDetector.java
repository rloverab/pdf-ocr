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
package Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Roger Lovera <rloverab@yahoo.es>
 */
public class PdfTypeDetector {
    public static final int INDETERMINATE = 0;
    public static final int WITH_TEXT = 1;
    public static final int ONLY_IMAGES = 2;
    
    public static int getType(File file){
        int type;
        
        type = INDETERMINATE;
        
        try {
            PDDocument pdf = PDDocument.load(file);
            
            if(!pdf.isEncrypted()){
                PDFTextStripper stripper = new PDFTextStripper();
                
                type = stripper.getText(pdf).isBlank() ? ONLY_IMAGES : WITH_TEXT;
            }
            
            pdf.close();
        } catch (IOException ex) {
            Logger.getLogger(PdfTypeDetector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return type;
    }
}
