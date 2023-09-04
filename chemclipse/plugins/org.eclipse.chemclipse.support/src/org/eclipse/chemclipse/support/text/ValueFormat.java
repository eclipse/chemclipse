/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add function expressions
 *******************************************************************************/
package org.eclipse.chemclipse.support.text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class ValueFormat {

	public static final String DEFAULT_DECIMAL_PATTERN = "0.###"; // $NON-NLS-1$
	public static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd"; // $NON-NLS-1$
	public static final String FULL_DATE_PATTERN = "yyyy/MM/dd HH:mm:ss"; // $NON-NLS-1$
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
	 * Use static methods only.
	 */
	private ValueFormat() {

	}

	/**
	 * Returns the decimal instance:
	 * "0.###", ENGLISH
	 * All values are displayed with a precision of 3 decimals.
	 * 
	 * DON'T MODIFY THIS INSTANCE, e.g.
	 * decimalFormat.setMaximumFractionDigits(0);
	 * decimalFormat.setMaximumIntegerDigits(0);
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
	 * It's allowed to modify this pattern.
	 * 
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat getDecimalFormatEnglishModifiable() {

		return new DecimalFormat((DEFAULT_DECIMAL_PATTERN), new DecimalFormatSymbols(Locale.ENGLISH));
	}

	/**
	 * If the pattern is invalid, the default pattern will be returned.
	 * 
	 * DON'T MODIFY THIS INSTANCE, e.g.
	 * decimalFormat.setMaximumFractionDigits(0);
	 * decimalFormat.setMaximumIntegerDigits(0);
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
	 * It's allowed to modify this pattern.
	 * 
	 * @param decimalPattern
	 * @return {@link DecimalFormat}
	 */
	public static DecimalFormat getDecimalFormatEnglishModifiable(String decimalPattern) {

		DecimalFormat decimalFormat;
		try {
			decimalFormat = new DecimalFormat((decimalPattern), new DecimalFormatSymbols(Locale.ENGLISH));
		} catch(Exception e) {
			decimalFormat = getDecimalFormatEnglishModifiable();
		}
		return decimalFormat;
	}

	/**
	 * Returns the english number format.
	 * You could use also getDecimalFormatEnglish.
	 * 
	 * DON'T MODIFY THIS INSTANCE, e.g.
	 * numberFormat.setMaximumFractionDigits(0);
	 * numberFormat.setMaximumIntegerDigits(0);
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
	 * DON'T MODIFY THIS INSTANCE, e.g.
	 * numberFormat.setMaximumFractionDigits(0);
	 * numberFormat.setMaximumIntegerDigits(0);
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

	public static <T> Function<T, String> asString() {

		return value -> value == null ? "" : String.valueOf(value);
	}

	public static <T extends Number> Function<T, String> asNumber(NumberFormat numberFormat) {

		return number -> number == null ? "-" : numberFormat.format(number);
	}

	public static <T extends Number> Function<T, String> asNumber() {

		if(Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
			return asNumber(getDecimalFormatGerman());
		} else {
			return asNumber(getDecimalFormatEnglish());
		}
	}

	public static <T extends Date> Function<T, String> asDate() {

		if(Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
			return asDate(getDateFormatGerman());
		} else {
			return asDate(getDateFormatEnglish());
		}
	}

	public static <T extends Date> Function<T, String> asDate(DateFormat dateFormat) {

		return date -> date == null ? "-" : dateFormat.format(date);
	}

	public static Function<Boolean, String> asBool() {

		return asBool("yes", "no");
	}

	public static Function<Boolean, String> asBool(String trueValue, String falseValue) {

		return value -> value == null ? "" : value ? trueValue : falseValue;
	}
}