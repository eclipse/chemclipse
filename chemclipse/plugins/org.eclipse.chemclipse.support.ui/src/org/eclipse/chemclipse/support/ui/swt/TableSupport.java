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
package org.eclipse.chemclipse.support.ui.swt;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableSupport {

	private static final Logger logger = Logger.getLogger(TableSupport.class);
	//
	private static final String VALUE_DELIMITER = " ";

	public static void setColumnOrder(Table table, String columnOrder) {

		try {
			int[] order = convertColumnInfo(columnOrder);
			if(isValidData(table, order)) {
				table.setColumnOrder(order);
			}
		} catch(SWTException | IllegalArgumentException e) {
			/*
			 * On exception, default order will be used.
			 */
		}
	}

	public static void setColumnWidth(Table table, String columnWidth) {

		try {
			int[] width = convertColumnInfo(columnWidth);
			if(isValidData(table, width)) {
				int index = 0;
				for(TableColumn tableColumn : table.getColumns()) {
					tableColumn.setWidth(width[index++]);
				}
			}
		} catch(SWTException | IllegalArgumentException e) {
			/*
			 * On exception, default order will be used.
			 */
		}
	}

	public static String getColumnOrder(Table table) {

		return convertColumnOrder(table.getColumnOrder());
	}

	public static String getColumnWidth(Table table) {

		return convertColumnWidth(table.getColumns());
	}

	private static String convertColumnOrder(int[] columnOrder) {

		StringBuilder builder = new StringBuilder();
		for(int i : columnOrder) {
			builder.append(i);
			builder.append(VALUE_DELIMITER);
		}
		//
		return builder.toString().trim();
	}

	private static String convertColumnWidth(TableColumn[] tableColumns) {

		StringBuilder builder = new StringBuilder();
		for(TableColumn tableColumn : tableColumns) {
			builder.append(tableColumn.getWidth());
			builder.append(VALUE_DELIMITER);
		}
		//
		return builder.toString().trim();
	}

	private static int[] convertColumnInfo(String columnInfo) {

		if(columnInfo.isEmpty()) {
			return new int[0];
		}
		//
		String[] values = columnInfo.split(VALUE_DELIMITER);
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

	private static boolean isValidData(Table table, int[] values) {

		TableColumn[] tableColumns = table.getColumns();
		return values.length == tableColumns.length;
	}
}
