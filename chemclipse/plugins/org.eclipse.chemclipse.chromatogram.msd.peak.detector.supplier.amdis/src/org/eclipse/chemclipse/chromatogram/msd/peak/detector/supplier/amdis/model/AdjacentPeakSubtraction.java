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

public enum AdjacentPeakSubtraction {
	TWO("Two", "0"), //
	ONE("One", "1"), //
	NONE("None", "2");

	private String label = "";
	private String value = "";

	private AdjacentPeakSubtraction(String label, String value) {

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
				{TWO.getLabel(), TWO.getValue()}, //
				{ONE.getLabel(), ONE.getValue()}, //
				{NONE.getLabel(), NONE.getValue()}//
		};
	}
}
