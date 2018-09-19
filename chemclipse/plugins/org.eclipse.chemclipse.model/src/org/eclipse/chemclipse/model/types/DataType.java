/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.types;

public enum DataType {
	AUTO_DETECT, // Auto-Detect
	MSD_NOMINAL, // Quadrupole, Ion Trap
	MSD_TANDEM, // MS/MS
	MSD_HIGHRES, // Orbitrap, TOF
	MSD, // mass selective data
	CSD, // current selective data
	WSD, // wavelength selective data
	XIR, // Infrared detectors, FTIR, NIR, MIR
	NMR, // Nuclear magnetic resonance
	CAL, // Retention Index Calibration
	PCR, // Polymerase Chain Reaction
	SEQ // Sequences
}
