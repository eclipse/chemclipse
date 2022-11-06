/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;

public class SeparationColumnFactory {

	private static final Logger logger = Logger.getLogger(SeparationColumnType.class);

	public static String getColumnLabel(ISeparationColumn separationColumn, int labelLength) {

		if(separationColumn != null) {
			String label = separationColumn.getSeparationColumnType().label();
			if(label.length() > labelLength) {
				label = label.substring(0, labelLength) + "...";
			}
			return label;
		}
		//
		return "";
	}

	public static List<ISeparationColumn> getSeparationColumns() {

		List<ISeparationColumn> separationColumns = new ArrayList<>();
		separationColumns.add(getSeparationColumn(SeparationColumnType.DEFAULT));
		separationColumns.add(getSeparationColumn(SeparationColumnType.POLAR));
		separationColumns.add(getSeparationColumn(SeparationColumnType.NON_POLAR));
		separationColumns.add(getSeparationColumn(SeparationColumnType.SEMI_POLAR));
		return separationColumns;
	}

	public static ISeparationColumn getSeparationColumn(String name) {

		return getSeparationColumn(name, "", "", "");
	}

	public static ISeparationColumn getSeparationColumn(String name, String length, String diameter, String phase) {

		SeparationColumnType separationColumnType = mapNameToColumnType(name);
		return new SeparationColumn(name, separationColumnType, length, diameter, phase);
	}

	public static ISeparationColumn getSeparationColumn(SeparationColumnType separationColumnType) {

		return new SeparationColumn(separationColumnType.name(), separationColumnType, "", "", "");
	}

	public static ISeparationColumnIndices getSeparationColumnIndices(SeparationColumnType separationColumnType) {

		ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
		ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
		separationColumn.setName(separationColumnType.name());
		separationColumn.setSeparationColumnType(separationColumnType);
		return separationColumnIndices;
	}

	private static SeparationColumnType mapNameToColumnType(String name) {

		/*
		 * Load the user defined column mappings
		 * Add the default mappings to match basic column types on demand.
		 */
		SeparationColumnMapping separationColumnMapping = new SeparationColumnMapping();
		separationColumnMapping.load(PreferenceSupplier.getSeparationColumnMappings());
		/*
		 * Backward compatibility (RetentionIndexType)
		 */
		addColumnMapping(separationColumnMapping, RetentionIndexType.POLAR.name(), SeparationColumnType.POLAR);
		addColumnMapping(separationColumnMapping, RetentionIndexType.SEMIPOLAR.name(), SeparationColumnType.SEMI_POLAR);
		addColumnMapping(separationColumnMapping, RetentionIndexType.APOLAR.name(), SeparationColumnType.NON_POLAR);
		/*
		 * Standards
		 */
		addColumnMapping(separationColumnMapping, SeparationColumnType.POLAR.name(), SeparationColumnType.POLAR);
		addColumnMapping(separationColumnMapping, SeparationColumnType.SEMI_POLAR.name(), SeparationColumnType.SEMI_POLAR);
		addColumnMapping(separationColumnMapping, SeparationColumnType.NON_POLAR.name(), SeparationColumnType.NON_POLAR);
		/*
		 * Additionally map the values of RetentionIndexType
		 */
		/*
		 * If no mapping is available or the value can't be parsed
		 * by SeparationColumnType, then DEFAULT is returned.
		 */
		SeparationColumnType separationColumnType = SeparationColumnType.DEFAULT;
		String value = separationColumnMapping.get(name);
		if(value != null) {
			try {
				separationColumnType = SeparationColumnType.valueOf(value);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		//
		return separationColumnType;
	}

	private static void addColumnMapping(SeparationColumnMapping separationColumnMapping, String name, SeparationColumnType separationColumnType) {

		if(!separationColumnMapping.containsKey(name)) {
			separationColumnMapping.put(name, separationColumnType.name());
		}
	}
}