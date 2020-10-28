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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings;

public enum ModelPeakOption {
	ALL("MPx (All)", 0), //
	MP1("MP1", 1), //
	MP2("MP2", 2), //
	MP3("MP3", 3);

	private String label = "";
	private int value = 0;

	private ModelPeakOption(String label, int value) {

		this.label = label;
		this.value = value;
	}

	public String getLabel() {

		return label;
	}

	public int getValue() {

		return value;
	}

	public static String[][] getItems() {

		return new String[][]{//
				{ALL.getLabel(), Integer.toString(ALL.getValue())}, //
				{MP1.getLabel(), Integer.toString(MP1.getValue())}, //
				{MP2.getLabel(), Integer.toString(MP2.getValue())}, //
				{MP3.getLabel(), Integer.toString(MP3.getValue())} //
		};
	}
}
