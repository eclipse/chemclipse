/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.math;

import java.text.DecimalFormat;
import java.util.function.Function;

import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.text.ILabel;
import org.eclipse.chemclipse.support.text.ValueFormat;

/**
 * Methods are defined to offer m/z round options.
 * 
 */
public enum IonRoundMethod implements ILabel {

	DEFAULT(v -> Math.round(v), "Default: round to nearest integer"), //
	MINUS_00(v -> round(v, 10), createDescription(-0.0d)), //
	MINUS_01(v -> round(v, 9), createDescription(-0.1d)), //
	MINUS_02(v -> round(v, 8), createDescription(-0.2d)), //
	MINUS_03(v -> round(v, 7), createDescription(-0.3d)), //
	MINUS_04(v -> round(v, 6), createDescription(-0.4d)), //
	MINUS_05(v -> round(v, 5), createDescription(-0.5d)), //
	MINUS_06(v -> round(v, 4), createDescription(-0.6d)), //
	MINUS_07(v -> round(v, 3), createDescription(-0.7d)), //
	MINUS_08(v -> round(v, 2), createDescription(-0.8d)), //
	MINUS_09(v -> round(v, 1), createDescription(-0.9d));

	private final Function<Double, Long> function;
	private final String label;

	private IonRoundMethod(Function<Double, Long> function, String label) {

		this.function = function;
		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	/**
	 * Round the given mz value to integer.
	 * The mz must be > 0, otherwise 0 is returned.
	 * Negative mz are not allowed.
	 * 
	 * @param mz
	 * @return int
	 */
	public int round(double mz) {

		if(mz > 0 && !Double.isInfinite(mz) && !Double.isNaN(mz)) {
			return function.apply(mz).intValue();
		}
		return 0;
	}

	public static String[][] getOptions() {

		IonRoundMethod[] methods = values();
		String[][] elements = new String[methods.length][2];
		//
		int counter = 0;
		for(IonRoundMethod method : methods) {
			elements[counter][0] = method.label();
			elements[counter][1] = method.name();
			counter++;
		}
		//
		return elements;
	}

	public static IonRoundMethod getActive() {

		return PreferenceSupplier.getIonRoundMethod();
	}

	public static void setActive(IonRoundMethod ionRoundMethod) {

		PreferenceSupplier.setIonRoundMethod(ionRoundMethod);
	}

	/**
	 * Offset values between 0 and 10 are allowed.
	 * 
	 * @param value
	 * @param offset
	 * @return long
	 */
	private static long round(double value, long offset) {

		long result;
		long basis = (long)value;
		//
		if(offset < 10) {
			long delta = Math.round((value - basis) * 10);
			result = (delta >= offset) ? basis + 1 : basis;
		} else {
			result = basis;
		}
		//
		return result;
	}

	private static String createDescription(double offset) {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0");
		return "Round m/z from " + decimalFormat.format(offset) + " (incl.) to +" + decimalFormat.format(offset + 1.0d) + " (excl.)";
	}
}
