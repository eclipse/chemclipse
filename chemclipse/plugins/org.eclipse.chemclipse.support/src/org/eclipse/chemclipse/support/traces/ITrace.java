/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.traces;

import org.eclipse.chemclipse.support.text.ValueFormat;

public interface ITrace {

	String PREFIX_SCALE_FACTOR = "(x";
	String POSTFIX_SCALE_FACTOR = ")";
	String INFIX_HIGHRES_RANGE_STANDARD = "±";
	String INFIX_HIGHRES_RANGE_SIMPLE = "+-";
	String POSTFIX_HIGHRES_PPM = "ppm";
	int MILLION = 1000000;

	double getScaleFactor();

	/**
	 * The scale factor must be > 0.
	 * 
	 * @param scaleFactor
	 * @return {@link ITrace}
	 */
	ITrace setScaleFactor(double scaleFactor);

	default String getScaleFactorAsString() {

		if(getScaleFactor() != 1) {
			StringBuilder builder = new StringBuilder();
			builder.append(" ");
			builder.append(PREFIX_SCALE_FACTOR);
			builder.append(ValueFormat.getDecimalFormatEnglish("0.0####").format(getScaleFactor()));
			builder.append(POSTFIX_SCALE_FACTOR);
			return builder.toString();
		} else {
			return "";
		}
	}
}