/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.cas;

/*
 * https://en.wikipedia.org/wiki/CAS_Registry_Number
 * 7732-18-5
 * [1]-[2]-[3]
 * [1]: 1-7 digits
 * [2]: 2 digits
 * [3]: checksum
 * = 10^9 possibilities: 1000000000
 */
public class CasSupport {

	public static final int MAX_ID = 1000000000;
	private static final String SEPARATOR = "-";
	private static final char SEPARATOR_CHAR = '-';

	public static boolean verifyChecksum(String cas) {

		/*
		 * 0-00-0
		 */
		if(cas != null) {
			cas = cas.trim();
			int length = cas.length();
			if(length >= 6) {
				long count = cas.chars().filter(c -> c == SEPARATOR_CHAR).count();
				if(count == 2) {
					if(cas.charAt(length - 2) == SEPARATOR_CHAR && cas.charAt(length - 5) == SEPARATOR_CHAR) {
						try {
							int casNumber = Integer.parseInt(cas.replaceAll(SEPARATOR, ""));
							return verifyChecksum(casNumber);
						} catch(NumberFormatException e) {
							//
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean verifyChecksum(int cas) {

		if(cas >= 0 && cas <= MAX_ID) {
			String casNumber = Integer.toString(cas);
			int length = casNumber.length() - 1; // The last char is the checksum.
			int factor = length;
			int sum = 0;
			for(int i = 0; i < length; i++) {
				char character = casNumber.charAt(i);
				if(Character.isDigit(character)) {
					int value = Integer.parseInt(Character.toString(character));
					sum += value * factor;
					factor--;
				}
			}
			int checksum = (sum % 10);
			char character = casNumber.charAt(length);
			if(Character.isDigit(character)) {
				int casChecksum = Integer.parseInt(Character.toString(character));
				return casChecksum == checksum;
			}
		}
		//
		return false;
	}
}
