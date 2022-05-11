/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adding delete type
 * Philip Wenig - delete type doesn't make sense here, moved to IPeak (markedAsDeleted)
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import org.eclipse.chemclipse.support.text.ILabel;

/**
 * This enumeration defines values which declare the type of the peak start and
 * end point.<br/>
 * E.g.:<br/>
 * BB - baseline baseline<br/>
 * BV - baseline valley<br/>
 * ... <br/>
 * B - baseline<br/>
 * V - valley<br/>
 * M - manual<br/>
 * D - deconvoluted<br/>
 * P - perpendicular
 *
 */
public enum PeakType implements ILabel {

	/*
	 * Some peak type defaults are listed here.
	 */
	DEFAULT("--"), //
	BB("BB (Baseline)"), //
	BV("BV (Baseline, Valley)"), //
	VV("VV (Valley)"), //
	VB("VB (Valley, Baseline)"), //
	MM("MM (Manual)"), //
	PV("PV (Perpendicular Drop, Valley"), //
	PB("PB (Perpendicular Drop, Baseline"), //
	VP("VP (Valley, Perpendicular Drop"), //
	BP("BP (Baseline, Perpendicular Drop"), //
	DD("DD (Deconvolution)");

	private String label = "";

	private PeakType(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}