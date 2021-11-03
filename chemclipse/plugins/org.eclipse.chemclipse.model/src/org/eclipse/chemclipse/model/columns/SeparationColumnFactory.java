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

	public static List<ISeparationColumn> getSeparationColumns() {

		List<ISeparationColumn> separationColumns = new ArrayList<>();
		separationColumns.add(getSeparationColumn(SeparationColumnType.DEFAULT));
		separationColumns.add(getSeparationColumn(SeparationColumnType.POLAR));
		separationColumns.add(getSeparationColumn(SeparationColumnType.APOLAR));
		separationColumns.add(getSeparationColumn(SeparationColumnType.SEMI_POLAR));
		separationColumns.add(getSeparationColumn(SeparationColumnType.DB1));
		separationColumns.add(getSeparationColumn(SeparationColumnType.DB5));
		separationColumns.add(getSeparationColumn(SeparationColumnType.DB1701));
		separationColumns.add(getSeparationColumn(SeparationColumnType.ZB1));
		separationColumns.add(getSeparationColumn(SeparationColumnType.WAX_PLUS));
		return separationColumns;
	}

	public static ISeparationColumn getSeparationColumn(String name) {

		return new SeparationColumn(name, "", "", "", "");
	}

	public static ISeparationColumn getSeparationColumn(SeparationColumnType type) {

		return new SeparationColumn(type.name(), type.value(), "", "", "");
	}

	public static ISeparationColumnIndices getSeparationColumnIndices(SeparationColumnType type) {

		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		separationColumnIndices.getSeparationColumn().setValue(type.value());
		return separationColumnIndices;
	}
}
