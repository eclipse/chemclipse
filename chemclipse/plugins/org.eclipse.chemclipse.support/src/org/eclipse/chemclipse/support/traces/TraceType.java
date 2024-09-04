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

	GENERIC("Generic", TraceGeneric.class), //
	MSD_NOMINAL("Quadrupole, IonTrap", TraceNominalMSD.class), //
	MSD_TANDEM("MS/MS", TraceTandemMSD.class), //
	MSD_HIGHRES("Orbitrap, TOF", TraceHighResMSD.class), //
	WSD_RASTERED("UV/Vis, DAD (rastered)", TraceRasteredWSD.class), //
	WSD_HIGHRES("UV/Vis, DAD", TraceHighResWSD.class), //
	VSD_RASTERED("FTIR, Raman (rastered)", TraceRasteredVSD.class); //

	private String label = "";
	private Class<? extends ITrace> clazz = null;

	private TraceType(String label, Class<? extends ITrace> clazz) {

		this.label = label;
		this.clazz = clazz;
	}

	@Override
	public String label() {

		return label;
	}

	public Class<? extends ITrace> clazz() {

		return clazz;
	}
}