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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public class RandomTextGenerator {
	
	private Map<String,List<String>> index = new HashMap<String,List<String>>();
	static private Random rnd = new Random(System.nanoTime());

	public RandomTextGenerator() throws IOException{
		index();
	}
	
	public String generateText(int words){
		StringBuilder sb = new StringBuilder();
		ArrayList<String> wordList = new ArrayList<String>();
		wordList.addAll(index.keySet());
		int start = rnd.nextInt(index.size());
		String word = wordList.get(start);
		sb.append(word + ' ');
		int count = 0;
		while( count < words ) {
			List<String> afters = index.get(word);
			int next = rnd.nextInt(afters.size());
			word = afters.get(next);
			sb.append(word + ' ');
			count++;
		}
		
		return sb.toString().trim();
	}
	
	private void index() throws IOException{
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
		String prevToken = null;
		while( st.hasMoreTokens() ) {
			String token = st.nextToken();
			if( prevToken != null ) {
				List<String> list = index.get(prevToken);
				if( list == null ) {
					list = new ArrayList<String>();
					index.put(prevToken, list);
				}
				list.add(token);
			}
			prevToken = token;
		}
	}

	
}
