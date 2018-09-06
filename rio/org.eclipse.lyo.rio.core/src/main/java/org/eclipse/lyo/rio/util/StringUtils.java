/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution. 
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *    Jim Conallen - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.rio.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.lyo.rio.core.IConstants;


public class StringUtils {
	
	public static String isToString(InputStream is)  {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			try{
				try {
					BufferedReader reader = new BufferedReader( new InputStreamReader(is, IConstants.TEXT_ENCODING));
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append('\n'); 
					}
				} finally {
					is.close();
				}
			} catch (IOException e ){
				e.printStackTrace();
			}
			return sb.toString();
		} else {
			return null;
		}
	}
	
	public static String forHtml(String expr) {
		// convert each line to a paragraph
		StringTokenizer st = new StringTokenizer(expr, "\n"); //$NON-NLS-1$
		StringBuffer sb = new StringBuffer();
		while( st.hasMoreTokens()  ){
			sb.append(XmlUtils.encode(st.nextToken()) ); //$NON-NLS-1$ 
		}
		return sb.toString();
	}

	public static String xsdDateTime(Date date ) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		String dateTimeString = DatatypeConverter.printDateTime(c);
        return dateTimeString;
	}
	
	public static Date xsdDateTime(String dateStr) throws ParseException {
		java.util.Calendar cal = DatatypeConverter.parseDateTime(dateStr);
		return cal.getTime();
	}
	
	public static String rfc2822(Date date ) {
		String pattern = "EEE, dd MMM yyyy HH:mm:ss Z"; //$NON-NLS-1$
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public static Date rfc2822(String dateStr) throws ParseException {
		String pattern = "EEE, dd MMM yyyy HH:mm:ss Z"; //$NON-NLS-1$
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.parse(dateStr);
	}

	@SuppressWarnings("nls")
	public static String stringEscape(String title) {
		return title.replaceAll("\\\\", "\\\\\\\\");
	}
	
}
