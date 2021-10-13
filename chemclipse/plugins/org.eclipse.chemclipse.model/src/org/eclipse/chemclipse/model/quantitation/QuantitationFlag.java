/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import org.eclipse.chemclipse.support.text.ILabel;

public enum QuantitationFlag implements ILabel {

	NONE("", ""), //
	ZERO("0", "Z"), //
	NEGATIVE("< 0", "N"), //
	LOWER_MIN_AREA("< Min Area", "L"), //
	HIGHER_MAX_AREA("> Max Area", "H");

	private String label = "";
	private String shortcut = "";

	private QuantitationFlag(String label, String shortcut) {

		this.label = label;
		this.shortcut = shortcut;
	}

	public String label() {

		return label;
	}

	public String shortcut() {

		return shortcut;
	}

	public static String[][] getOptions() {

		QuantitationFlag[] values = values();
		String[][] elements = new String[values.length][2];
		//
		int counter = 0;
		for(QuantitationFlag value : values) {
			elements[counter][0] = value.label();
			elements[counter][1] = value.name();
			counter++;
		}
		//
		return elements;
	}
}