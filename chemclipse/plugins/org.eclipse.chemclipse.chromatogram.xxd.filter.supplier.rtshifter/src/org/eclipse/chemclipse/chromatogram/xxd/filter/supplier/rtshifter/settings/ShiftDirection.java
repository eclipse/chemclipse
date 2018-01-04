/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings;

public enum ShiftDirection {
	FORWARD("Forward"), FAST_FORWARD("Fast Forward"), BACKWARD("Backward"), FAST_BACKWARD("Fast Backward");

	private String description;

	private ShiftDirection(String description) {
		this.description = description;
	}

	public String getDescription() {

		return description;
	}

	public static String[][] getElements() {

		String[][] elements = new String[values().length][2];
		int counter = 0;
		for(ShiftDirection segment : values()) {
			elements[counter][0] = segment.getDescription();
			elements[counter][1] = segment.name();
			counter++;
		}
		return elements;
	}
}
