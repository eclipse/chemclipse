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
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

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
public enum PeakType {
	/*
	 * Some peak type defaults are listed here.
	 */
	DEFAULT("no peak type information available"), //
	BB("baseline" + " - " + "baseline"), //
	BV("baseline" + " - " + "valey"), //
	VV("valey" + " - " + "valey"), //
	VB("valey" + " - " + "baseline"), //
	MM("manual" + " - " + "manual"), //
	PV("perpendicular" + " - " + "valey"), //
	PB("perpendicular" + " - " + "baseline"), //
	VP("valey" + " - " + "perpendicular"), //
	BP("baseline" + " - " + "perpendicular"), //
	DD("deconvoluted" + " - " + "deconvoluted"), //
	DELETED("this peak was deleted");

	private String description = "";

	private PeakType(String description) {
		this.description = description;
	}

	public String getDescription() {

		return description;
	}
}
