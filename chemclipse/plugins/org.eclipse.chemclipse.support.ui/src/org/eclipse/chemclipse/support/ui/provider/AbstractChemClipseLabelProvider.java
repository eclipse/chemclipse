/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - locale dependent implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.provider;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

public abstract class AbstractChemClipseLabelProvider extends LabelProvider implements ITableLabelProvider {

	private DecimalFormat decimalFormat;

	/**
	 * The pattern "0.000" is used by default.
	 */
	public AbstractChemClipseLabelProvider() {

		createDecimalFormatInstance("0.000");
	}

	/**
	 * Add a valid decimal format pattern, e.g. "0.###" or "0.0##".
	 * 
	 * @param pattern
	 */
	public AbstractChemClipseLabelProvider(String pattern) {

		createDecimalFormatInstance(pattern);
	}

	private void createDecimalFormatInstance(String pattern) {

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
		String format = "0.000";
		try {
			decimalFormat = new DecimalFormat(pattern, decimalFormatSymbols);
		} catch(NullPointerException e) {
			decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		} catch(IllegalArgumentException e) {
			decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		}
	}

	public DecimalFormat createScientificDecimalFormatInstance() {

		DecimalFormat scientificDecimalFormat;
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
		String format = "0.###E0";
		try {
			scientificDecimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		} catch(NullPointerException e) {
			scientificDecimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		} catch(IllegalArgumentException e) {
			scientificDecimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		}
		return scientificDecimalFormat;
	}

	public DecimalFormat createIntegerDecimalFormatInstance() {

		DecimalFormat scientificDecimalFormat;
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
		String format = "0";
		try {
			scientificDecimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		} catch(NullPointerException e) {
			scientificDecimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		} catch(IllegalArgumentException e) {
			scientificDecimalFormat = new DecimalFormat(format, decimalFormatSymbols);
		}
		return scientificDecimalFormat;
	}

	public DecimalFormat getDecimalFormat() {

		return decimalFormat;
	}
}
