/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;

public class SeparationColumnFactory {

	private static List<ISeparationColumn> separationColumns;
	private static SeparationColumnMapping separationColumnMapping;

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

		if(separationColumns == null) {
			separationColumns = new ArrayList<>();
			separationColumns.add(getSeparationColumn(SeparationColumnType.DEFAULT));
			separationColumns.add(getSeparationColumn(SeparationColumnType.POLAR));
			separationColumns.add(getSeparationColumn(SeparationColumnType.NON_POLAR));
			separationColumns.add(getSeparationColumn(SeparationColumnType.SEMI_POLAR));
		}
		//
		return separationColumns;
	}

	public static SeparationColumnType getSeparationColumnType(String name) {

		return getSeparationColumnType(getSeparationColumnMappingDefault(), name);
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
		separationColumnMapping.putAll(getSeparationColumnMappingDefault());
		//
		return getSeparationColumnType(separationColumnMapping, name);
	}

	private static SeparationColumnType getSeparationColumnType(SeparationColumnMapping separationColumnMapping, String name) {

		/*
		 * If no mapping is available or the value can't be parsed
		 * by SeparationColumnType, then DEFAULT is returned.
		 */
		SeparationColumnType separationColumnType = SeparationColumnType.DEFAULT;
		if(name != null) {
			separationColumnType = separationColumnMapping.getOrDefault(name, SeparationColumnType.DEFAULT);
		}
		//
		return separationColumnType;
	}

	private static SeparationColumnMapping getSeparationColumnMappingDefault() {

		if(separationColumnMapping == null) {
			/*
			 * Create the mapping.
			 */
			separationColumnMapping = new SeparationColumnMapping();
			/*
			 * Backward compatibility (RetentionIndexType)
			 */
			addColumnMapping(separationColumnMapping, "POLAR", SeparationColumnType.POLAR);
			addColumnMapping(separationColumnMapping, "SEMIPOLAR", SeparationColumnType.SEMI_POLAR);
			addColumnMapping(separationColumnMapping, "APOLAR", SeparationColumnType.NON_POLAR);
			/*
			 * Standards
			 */
			addColumnMapping(separationColumnMapping, SeparationColumnType.POLAR.name(), SeparationColumnType.POLAR);
			addColumnMapping(separationColumnMapping, SeparationColumnType.SEMI_POLAR.name(), SeparationColumnType.SEMI_POLAR);
			addColumnMapping(separationColumnMapping, SeparationColumnType.NON_POLAR.name(), SeparationColumnType.NON_POLAR);
		}
		//
		return separationColumnMapping;
	}

	private static void addColumnMapping(SeparationColumnMapping separationColumnMapping, String name, SeparationColumnType separationColumnType) {

		if(!separationColumnMapping.containsKey(name)) {
			separationColumnMapping.put(name, separationColumnType);
		}
	}
}