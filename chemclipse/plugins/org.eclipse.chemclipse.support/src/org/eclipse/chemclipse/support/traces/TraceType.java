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

import org.eclipse.chemclipse.support.text.ILabel;

public enum TraceType implements ILabel {

	GENERIC("Generic"), //
	MSD_NOMINAL("Quadrupole, IonTrap, ..."), //
	MSD_TANDEM("MS/MS, ..."), //
	MSD_HIGHRES("Orbitrap, TOF, ..."), //
	WSD_RASTERED("UV/Vis, DAD, ... (rastered)"), //
	WSD_HIGHRES("UV/Vis, DAD, ..."), //
	VSD_RASTERED("FTIR, Raman, ... (rastered)"); //

	private String label = "";

	private TraceType(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}
}