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
package Classes;

import java.io.File;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Roger Lovera <rloverab@yahoo.es>
 */
public final class Task {

    private String url;
    private File file;
    private int statusDownload;
    private int statusOcr;
    private int row;
    public static final int STAND_BY = 0;
    public static final int OK = 1;
    public static final int ERROR = 2;
    public static final int PROCESSING = 3;

    public Task(String url) {
        this.url = url;
        statusDownload = 0;
        statusOcr = 0;
        row = -1;
        file = null;
    }

    public Task(File file) {
        this.file = file;
        statusDownload = 0;
        statusOcr = 0;
        row = -1;
        url = "";
    }

    public String getUrl() {
        return url;
    }

    public File getFile() {
        return file;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        String filename;
        
        if(!url.isBlank()){
            String[] invalidChars = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};

            filename = FilenameUtils.getName(url);

            for (String _char : invalidChars) {
                filename = filename.replace(_char, "-");
            }
        }else{
            filename = file.getName();
        }

        return filename;
    }

    public int getStatusDownload() {
        return statusDownload;
    }

    public void setStatusDownload(int statusDownload) {
        this.statusDownload = statusDownload;
    }

    public int getStatusOcr() {
        return statusOcr;
    }

    public void setStatusOcr(int statusOcr) {
        this.statusOcr = statusOcr;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "Task{" + "url=" + url + ", file=" + file + ", statusDownload=" + statusDownload + ", statusOcr=" + statusOcr + ", row=" + row + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.url);
        hash = 19 * hash + Objects.hashCode(this.file);
        hash = 19 * hash + this.statusDownload;
        hash = 19 * hash + this.statusOcr;
        hash = 19 * hash + this.row;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        if (this.statusDownload != other.statusDownload) {
            return false;
        }
        if (this.statusOcr != other.statusOcr) {
            return false;
        }
        if (this.row != other.row) {
            return false;
        }
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return Objects.equals(this.file, other.file);
    }

    
}
