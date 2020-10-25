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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.model;

public enum ScanDirection {
	HIGH_TO_LOW("High to Low", "-1"), //
	NONE("None", "0"), //
	LOW_TO_HIGH("Low to High", "1");

	private String label = "";
	private String value = "";

	private ScanDirection(String label, String value) {

		this.label = label;
		this.value = value;
	}

	public String getLabel() {

		return label;
	}

	public String getValue() {

		return value;
	}

	public static String[][] getItems() {

		return new String[][]{//
				{HIGH_TO_LOW.getLabel(), HIGH_TO_LOW.getValue()}, //
				{NONE.getLabel(), NONE.getValue()}, //
				{LOW_TO_HIGH.getLabel(), LOW_TO_HIGH.getValue()}//
		};
	}
}
