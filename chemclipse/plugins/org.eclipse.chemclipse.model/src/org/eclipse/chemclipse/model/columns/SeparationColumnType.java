/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - added labels
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

import org.eclipse.chemclipse.support.text.ILabel;

public enum SeparationColumnType implements ILabel {
	/*
	 * These are the basic 3 column types and the default
	 * to supply a generic option. Specific columns share
	 * be mapped to either one of these 4 types.
	 */
	DEFAULT("Default"), //
	POLAR("Polar"), //
	APOLAR("Nonpolar"), //
	SEMI_POLAR("Semipolar");

	private String label = "";

	private SeparationColumnType(String label) {

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