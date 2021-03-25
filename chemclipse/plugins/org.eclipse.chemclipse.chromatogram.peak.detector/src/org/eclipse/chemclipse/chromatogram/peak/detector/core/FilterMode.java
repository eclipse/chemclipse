/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Alexander Kerner - initial API and implementation
 * Philip Wenig - add elements for combo support
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.core;

public enum FilterMode {
	INCLUDE, //
	EXCLUDE;

	public static String[][] getElements() {

		String[][] elements = new String[values().length][2];
		int counter = 0;
		for(FilterMode filterMode : values()) {
			elements[counter][0] = filterMode.name(); // Label
			elements[counter][1] = filterMode.name(); // Value
			counter++;
		}
		return elements;
	}
}
