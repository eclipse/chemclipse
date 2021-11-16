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
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

import java.util.ArrayList;
import java.util.List;

public class SeparationColumnFactory {

	public static final String TYPE_DEFAULT = "Default";
	public static final String TYPE_POLAR = "Polar";
	public static final String TYPE_APOLAR = "Nonpolar";
	public static final String TYPE_SEMI_POLAR = "Semipolar";
	/*
	 * Specific columns might be removed soon as basic types.
	 * Mappings shall be added to group specific columns into
	 * one of the three above classes.
	 */
	public static final String TYPE_DB1 = "DB-1";
	public static final String TYPE_DB5 = "DB-5";
	public static final String TYPE_DB1701 = "DB-1701";
	public static final String TYPE_ZB1 = "ZB-1";
	public static final String TYPE_WAX_PLUS = "WAX+";

	public static List<ISeparationColumn> getSeparationColumns() {

		List<ISeparationColumn> separationColumns = new ArrayList<>();
		separationColumns.add(getSeparationColumn(TYPE_DEFAULT));
		separationColumns.add(getSeparationColumn(TYPE_POLAR));
		separationColumns.add(getSeparationColumn(TYPE_APOLAR));
		separationColumns.add(getSeparationColumn(TYPE_SEMI_POLAR));
		separationColumns.add(getSeparationColumn(TYPE_DB1));
		separationColumns.add(getSeparationColumn(TYPE_DB5));
		separationColumns.add(getSeparationColumn(TYPE_DB1701));
		separationColumns.add(getSeparationColumn(TYPE_ZB1));
		separationColumns.add(getSeparationColumn(TYPE_WAX_PLUS));
		return separationColumns;
	}

	public static ISeparationColumn getSeparationColumn(String name) {

		return new SeparationColumn(name, "", "", "");
	}

	public static ISeparationColumnIndices getSeparationColumnIndices(String name) {

		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		separationColumnIndices.getSeparationColumn().setName(name);
		return separationColumnIndices;
	}
}
