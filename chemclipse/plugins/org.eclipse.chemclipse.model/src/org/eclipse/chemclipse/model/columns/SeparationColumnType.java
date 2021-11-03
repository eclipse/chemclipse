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

	DEFAULT("DEFAULT", "Default"), //
	POLAR("POLAR", "Polar"), //
	APOLAR("APOLAR", "Nonpolar"), //
	SEMI_POLAR("SEMI-POLAR", "Semipolar"),
	/*
	 * Specific columns might be removed soon as basic types.
	 * Mappings shall be added to group specific columns into
	 * one of the three above classes.
	 */
	DB1("DB1", "DB-1"), //
	DB5("DB5", "DB-5"), //
	DB1701("DB1701", "DB-1701"), //
	ZB1("ZB1", "ZB-1"), //
	WAX_PLUS("WAX+", "WAX+");

	private String value = "";
	private String label = "";

	private SeparationColumnType(String value, String label) {

		this.value = value;
		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public String value() {

		return value;
	}
}
