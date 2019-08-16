/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - locale dependent implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;

public abstract class AbstractChemClipseLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

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

		decimalFormat = ValueFormat.getDecimalFormatEnglish(pattern);
	}

	public DecimalFormat createScientificDecimalFormatInstance() {

		return ValueFormat.getDecimalFormatEnglish("0.###E0");
	}

	public DecimalFormat createIntegerDecimalFormatInstance() {

		return ValueFormat.getDecimalFormatEnglish("0");
	}

	public DecimalFormat getDecimalFormat() {

		return decimalFormat;
	}
}
