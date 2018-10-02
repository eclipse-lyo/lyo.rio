/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public class RandomTextGenerator {
	
	private Map<String, List<String>> drawers = new HashMap<String,List<String>>();
	private ArrayList<String> keys;
	static private Random rnd = new Random(System.nanoTime());

	public RandomTextGenerator() throws IOException{
		fillDrawers();
	}
	
	public String generateText(int words){
		StringBuilder sb = new StringBuilder();
		String word = keys.get(rnd.nextInt(keys.size()));
		sb.append(word + ' ');
		List<String> afters = drawers.get(word);
		if (afters != null && afters.size() > 0) {
			for(int count = 0; count < words; count++) {
				int next = rnd.nextInt(afters.size());
				word = afters.get(next);
				sb.append(word + ' ');
			}
		}
		return sb.toString().trim();
	}
	
	private void fillDrawers() throws IOException{
		InputStream is = this.getClass().getResourceAsStream("filler.txt"); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader( new InputStreamReader(is, "UTF-8")); //$NON-NLS-1$
		int ch;
		while ((ch = reader.read()) !=  -1) {
			if( Character.isLetterOrDigit(ch) ) {
				sb.append((char)ch); 
			} else {
				sb.append(' ');
			}
		}
		String str = sb.toString();
		StringTokenizer st = new StringTokenizer(str," \n\t\r"); //$NON-NLS-1$
		String prevToken = st.nextToken();
		while( st.hasMoreTokens() ) {
			String token = st.nextToken();
			List<String> list = drawers.get(prevToken);
			if( list == null ) {
				list = new ArrayList<String>();
				drawers.put(prevToken, list);
			}
			list.add(token);
			prevToken = token;
		}
		keys = new ArrayList<String>(drawers.keySet());
	}

	
}
