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
 * Matthias Mailänder - add labels
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.core;

import org.eclipse.chemclipse.support.text.ILabel;

public enum FilterMode implements ILabel {

	INCLUDE("inclusive"), //
	EXCLUDE("exclusive");

	private String label;

	private FilterMode(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getElements() {

		String[][] elements = new String[values().length][2];
		int counter = 0;
		for(FilterMode filterMode : values()) {
			elements[counter][0] = filterMode.label();
			elements[counter][1] = filterMode.name();
			counter++;
		}
		return elements;
	}
}
