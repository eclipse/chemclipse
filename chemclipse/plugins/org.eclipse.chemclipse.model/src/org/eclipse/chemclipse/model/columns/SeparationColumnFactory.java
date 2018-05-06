/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

	public static final String TYPE_NONE = "NONE";
	public static final String TYPE_POLAR = "POLAR";
	public static final String TYPE_APOLAR = "APOLAR";
	public static final String TYPE_SEMI_POLAR = "SEMI-POLAR";
	public static final String TYPE_DB1 = "DB1";
	public static final String TYPE_DB5 = "DB5";
	public static final String TYPE_DB1701 = "DB1701";

	public static List<ISeparationColumn> getSeparationColumns() {

		List<ISeparationColumn> separationColumns = new ArrayList<>();
		separationColumns.add(getSeparationColumn(TYPE_NONE));
		separationColumns.add(getSeparationColumn(TYPE_POLAR));
		separationColumns.add(getSeparationColumn(TYPE_APOLAR));
		separationColumns.add(getSeparationColumn(TYPE_SEMI_POLAR));
		separationColumns.add(getSeparationColumn(TYPE_DB1));
		separationColumns.add(getSeparationColumn(TYPE_DB5));
		separationColumns.add(getSeparationColumn(TYPE_DB1701));
		return separationColumns;
	}

	public static ISeparationColumn getSeparationColumn(String name) {

		/*
		 * TODO - Extend
		 */
		ISeparationColumn separationColumn;
		switch(name) {
			case TYPE_NONE:
				separationColumn = new SeparationColumn(name, "", "", "");
				break;
			case TYPE_POLAR:
				separationColumn = new SeparationColumn(name, "30m", "25µm", "");
				break;
			case TYPE_APOLAR:
				separationColumn = new SeparationColumn(name, "30m", "25µm", "");
				break;
			case TYPE_SEMI_POLAR:
				separationColumn = new SeparationColumn(name, "30m", "25µm", "");
				break;
			case TYPE_DB1:
				separationColumn = new SeparationColumn(name, "30m", "25µm", "");
				break;
			case TYPE_DB5:
				separationColumn = new SeparationColumn(name, "30m", "25µm", "");
				break;
			case TYPE_DB1701:
				separationColumn = new SeparationColumn(name, "30m", "25µm", "");
				break;
			default:
				separationColumn = new SeparationColumn(name, "30m", "25µm", "");
				break;
		}
		return separationColumn;
	}
}
