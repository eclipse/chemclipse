/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ValueFormat {

	private static DecimalFormat decimalFormatEnglish = null;
	private static DecimalFormat decimalFormatGerman = null;
	private static SimpleDateFormat simpleDateFormatEnglish = null;
	private static SimpleDateFormat simpleDateFormatGerman = null;

	private ValueFormat() {

	}

	/**
	 * Returns the decimal instance:
	 * "0.###", ENGLISH
	 * All values are displayed with a precision of 3 decimals.
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat getDecimalFormatEnglish() {

		if(decimalFormatEnglish == null) {
			decimalFormatEnglish = new DecimalFormat(("0.###"), new DecimalFormatSymbols(Locale.ENGLISH)); // $NON-NLS-1$
		}
		return decimalFormatEnglish;
	}

	/**
	 * Returns the decimal instance:
	 * "0.###", GERMAN
	 * All values are displayed with a precision of 3 decimals.
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat getDecimalFormatGerman() {

		if(decimalFormatGerman == null) {
			decimalFormatGerman = new DecimalFormat(("0.###"), new DecimalFormatSymbols(Locale.GERMAN)); // $NON-NLS-1$
		}
		return decimalFormatGerman;
	}

	/**
	 * Returns the English date format:
	 * yyyy/MM/dd
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static SimpleDateFormat getDateFormatEnglish() {

		if(simpleDateFormatEnglish == null) {
			simpleDateFormatEnglish = new SimpleDateFormat("yyyy/MM/dd"); // $NON-NLS-1$
		}
		return simpleDateFormatEnglish;
	}

	/**
	 * Returns the German date format:
	 * dd.MM.yyyy
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static SimpleDateFormat getDateFormatGerman() {

		if(simpleDateFormatGerman == null) {
			simpleDateFormatGerman = new SimpleDateFormat("dd.MM.yyyy"); // $NON-NLS-1$
		}
		return simpleDateFormatGerman;
	}
}
