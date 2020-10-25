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

public enum InstrumentType {
	QUADRUPOLE("Quadrupole", "0"), //
	ION_TRAP("Ion Trap", "1"), //
	MAGNETIC_SECTOR("Magnetic Sector", "2"), //
	SIM("SIM", "3");

	private String label = "";
	private String value = "";

	private InstrumentType(String label, String value) {

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
				{QUADRUPOLE.getLabel(), QUADRUPOLE.getValue()}, //
				{ION_TRAP.getLabel(), ION_TRAP.getValue()}, //
				{MAGNETIC_SECTOR.getLabel(), MAGNETIC_SECTOR.getValue()}, //
				{SIM.getLabel(), SIM.getValue()}//
		};
	}
}
