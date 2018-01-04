/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Converts date and string to a general format.<br/>
 */
public class DateSupport {

	private static final String pattern = "yyMMddHHmmssZ";

	/**
	 * Returns the actual Date in a general format.<br/>
	 * 
	 * @return String
	 */
	public static String getActualDate() {

		Date now = new Date();
		return getDate(now);
	}

	/**
	 * Returns a general string representation of the given date.
	 * 
	 * @param date
	 * @return String
	 */
	public static String getDate(Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
		return dateFormat.format(date);
	}

	/**
	 * Parses a general date string an gives back it's date representation.
	 * 
	 * @param date
	 * @return Date
	 * @throws ParseException
	 */
	public static Date getDate(String date) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
		return dateFormat.parse(date);
	}
}
