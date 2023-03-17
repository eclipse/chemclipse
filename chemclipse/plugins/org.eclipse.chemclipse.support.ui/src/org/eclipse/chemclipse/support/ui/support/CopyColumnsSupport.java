/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CopyColumnsSupport {

	private static final String DELIMITER = ",";

	public static String getColumns(Set<Integer> columnSelections) {

		return columnSelections.stream().sorted().map(i -> Integer.toString(i)).collect(Collectors.joining(DELIMITER));
	}

	public static int[] getColumns(String columnSelections) {

		List<Integer> columns = new ArrayList<>();
		String[] values = columnSelections.split(DELIMITER);
		for(String value : values) {
			try {
				columns.add(Integer.parseInt(value));
			} catch(NumberFormatException e) {
				//
			}
		}
		//
		return columns.stream().mapToInt(Integer::intValue).toArray();
	}
}