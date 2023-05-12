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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Roger Lovera <rloverab@yahoo.es>
 */
public class GenerateCode {

    public static synchronized String generateCode() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSSSS");
        Date date = new Date();
        String dateString = formatter.format(date);
        return dateString;
    }
}
