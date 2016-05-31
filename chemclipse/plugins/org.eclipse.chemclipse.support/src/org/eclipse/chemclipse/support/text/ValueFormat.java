/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ValueFormat {

	public static final String DEFAULT_DECIMAL_PATTERN = "0.###"; // $NON-NLS-1$
	public static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd"; // $NON-NLS-1$
	//
	private static Map<String, DecimalFormat> decimalFormatsEnglish = null;
	private static Map<String, DecimalFormat> decimalFormatsGerman = null;
	private static Map<String, SimpleDateFormat> simpleDateFormatsEnglish = null;
	private static Map<String, SimpleDateFormat> simpleDateFormatsGerman = null;
	//
	private static final String DEFAULT_NUMBER_FORMAT = "DNF"; // $NON-NLS-1$
	private static Map<String, NumberFormat> numberFormatsEnglish = null;

	static {
		decimalFormatsEnglish = new HashMap<String, DecimalFormat>();
		decimalFormatsGerman = new HashMap<String, DecimalFormat>();
		simpleDateFormatsEnglish = new HashMap<String, SimpleDateFormat>();
		simpleDateFormatsGerman = new HashMap<String, SimpleDateFormat>();
		//
		numberFormatsEnglish = new HashMap<String, NumberFormat>();
	}

	/**
	 * Returns the decimal instance:
	 * "0.###", ENGLISH
	 * All values are displayed with a precision of 3 decimals.
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat getDecimalFormatEnglish() {

		if(decimalFormatsEnglish.get(DEFAULT_DECIMAL_PATTERN) == null) {
			decimalFormatsEnglish.put(DEFAULT_DECIMAL_PATTERN, new DecimalFormat((DEFAULT_DECIMAL_PATTERN), new DecimalFormatSymbols(Locale.ENGLISH)));
		}
		return decimalFormatsEnglish.get(DEFAULT_DECIMAL_PATTERN);
	}

	/**
	 * If the pattern is invalid, the default pattern will be returned.
	 * 
	 * @param decimalPattern
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat getDecimalFormatEnglish(String decimalPattern) {

		boolean patternIsValid = true;
		if(decimalFormatsEnglish.get(decimalPattern) == null) {
			try {
				decimalFormatsEnglish.put(decimalPattern, new DecimalFormat((decimalPattern), new DecimalFormatSymbols(Locale.ENGLISH)));
			} catch(Exception e) {
				patternIsValid = false;
			}
		}
		/*
		 * Return the default pattern if the given pattern is not valid.
		 */
		if(patternIsValid) {
			return decimalFormatsEnglish.get(decimalPattern);
		} else {
			return getDecimalFormatEnglish();
		}
	}

	/**
	 * Returns the english number format.
	 * You could use also getDecimalFormatEnglish.
	 * 
	 * @return NumberFormat
	 */
	public static NumberFormat getNumberFormatEnglish() {

		if(numberFormatsEnglish.get(DEFAULT_NUMBER_FORMAT) == null) {
			numberFormatsEnglish.put(DEFAULT_NUMBER_FORMAT, NumberFormat.getInstance(Locale.ENGLISH));
		}
		return numberFormatsEnglish.get(DEFAULT_NUMBER_FORMAT);
	}

	/**
	 * Returns the english number format with min/max digits.
	 * You could use also getDecimalFormatEnglish.
	 * 
	 * @return NumberFormat
	 */
	public static NumberFormat getNumberFormatEnglish(final int minimumFractionDigits, final int maximumFractionDigits) {

		String key = minimumFractionDigits + " " + maximumFractionDigits;
		if(numberFormatsEnglish.get(key) == null) {
			NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
			numberFormat.setMinimumFractionDigits(minimumFractionDigits);
			numberFormat.setMaximumFractionDigits(maximumFractionDigits);
			numberFormatsEnglish.put(key, numberFormat);
		}
		return numberFormatsEnglish.get(key);
	}

	/**
	 * Returns the English date format:
	 * yyyy/MM/dd
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static SimpleDateFormat getDateFormatEnglish() {

		if(simpleDateFormatsEnglish.get(DEFAULT_DATE_PATTERN) == null) {
			simpleDateFormatsEnglish.put(DEFAULT_DATE_PATTERN, new SimpleDateFormat(DEFAULT_DATE_PATTERN));
		}
		return simpleDateFormatsEnglish.get(DEFAULT_DATE_PATTERN);
	}

	/**
	 * If the pattern is invalid, the default english pattern will be returned.
	 * 
	 * @param datePattern
	 * @return
	 */
	public static SimpleDateFormat getDateFormatEnglish(String datePattern) {

		boolean patternIsValid = true;
		if(simpleDateFormatsEnglish.get(datePattern) == null) {
			try {
				simpleDateFormatsEnglish.put(datePattern, new SimpleDateFormat(datePattern));
			} catch(Exception e) {
				patternIsValid = false;
			}
		}
		/*
		 * Return the default pattern if the given pattern is not valid.
		 */
		if(patternIsValid) {
			return simpleDateFormatsEnglish.get(datePattern);
		} else {
			return getDateFormatEnglish();
		}
	}

	/**
	 * Returns the decimal instance:
	 * "0.###", GERMAN
	 * All values are displayed with a precision of 3 decimals.
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat getDecimalFormatGerman() {

		if(decimalFormatsGerman.get(DEFAULT_DECIMAL_PATTERN) == null) {
			decimalFormatsGerman.put(DEFAULT_DECIMAL_PATTERN, new DecimalFormat((DEFAULT_DECIMAL_PATTERN), new DecimalFormatSymbols(Locale.GERMAN)));
		}
		return decimalFormatsGerman.get(DEFAULT_DECIMAL_PATTERN);
	}

	/**
	 * Returns the German date format:
	 * dd.MM.yyyy
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static SimpleDateFormat getDateFormatGerman() {

		if(simpleDateFormatsGerman.get(DEFAULT_DATE_PATTERN) == null) {
			simpleDateFormatsGerman.put(DEFAULT_DATE_PATTERN, new SimpleDateFormat("dd.MM.yyyy"));
		}
		return simpleDateFormatsGerman.get(DEFAULT_DATE_PATTERN);
	}
}
