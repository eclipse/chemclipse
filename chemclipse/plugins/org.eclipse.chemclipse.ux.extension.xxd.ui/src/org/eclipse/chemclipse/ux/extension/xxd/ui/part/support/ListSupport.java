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
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Table;

public class ListSupport {

	private static final Logger logger = Logger.getLogger(ListSupport.class);
	//
	private static final String COLUMN_DELIMITER = " ";

	public void setColumnOrder(Table table, String columnOrder) {

		try {
			int[] columns = convertColumnOrder(columnOrder);
			table.setColumnOrder(columns);
		} catch(SWTException | IllegalArgumentException e) {
			/*
			 * On exception, default order will be used.
			 */
		}
	}

	public String getColumnOrder(Table table) {

		return convertColumnOrder(table.getColumnOrder());
	}

	private String convertColumnOrder(int[] columnOrder) {

		StringBuilder builder = new StringBuilder();
		for(int i : columnOrder) {
			builder.append(i);
			builder.append(COLUMN_DELIMITER);
		}
		//
		return builder.toString().trim();
	}

	private int[] convertColumnOrder(String columnOrder) {

		String[] values = columnOrder.split(COLUMN_DELIMITER);
		int size = values.length;
		int[] columns = new int[size];
		for(int i = 0; i < size; i++) {
			try {
				columns[i] = Integer.parseInt(values[i]);
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		}
		//
		return columns;
	}
}
