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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.model;

import org.eclipse.chemclipse.support.text.ILabel;

public enum CondenseOption implements ILabel {

	OFF("Off", ""), //
	COARSE("Coarse", "0.0"), //
	STANDARD("Standard", "0.00"), //
	SENSITIVE("Sensitive", "0.000"); //

	private String label = "";
	private String decimalPattern = "";

	private CondenseOption(String label, String decimalPattern) {

		this.label = label;
		this.decimalPattern = decimalPattern;
	}

	public String label() {

		return label;
	}

	public String decimalPattern() {

		return decimalPattern;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}