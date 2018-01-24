/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IGroup;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

public class TableProvider {

	public static final int COLUMN_INDEX_PEAK_NAMES = 2;
	public static final int COLUMN_INDEX_RETENTION_TIMES = 1;
	public static final int COLUMN_INDEX_SELECTED = 0;
	public static final String COLUMN_LABEL_GROUP_DATA = "COLUMN_GROUP_DATA";
	public static final String COLUMN_LABEL_PEAKS_NAMES = "COLUMN_PEAKS_NAMES";
	public static final String COLUMN_LABEL_RETENTION_TIMES = "COLUMN_RETENTION_TIMES";
	public static final String COLUMN_LABEL_SAMPLE_DATA = "COLUMN_SAMPLE_DATA";
	public static final String COLUMN_LABEL_SELECTED = "COLUMN_SELECTED";
	/**
	 * number of column which is used to describe sample data
	 */
	final public static int NUMER_OF_DESCRIPTION_COLUMN = 3;
	private TableData dataTable;

	public TableProvider(TableData dataTable) {
		this.dataTable = dataTable;
	}

	public int getColumnCount() {

		return dataTable.getSamples().size() + NUMER_OF_DESCRIPTION_COLUMN;
	}

	public String getColumnLable(int columnIndex) {

		if(columnIndex == COLUMN_INDEX_SELECTED) {
			return COLUMN_LABEL_SELECTED;
		} else if(columnIndex == COLUMN_INDEX_RETENTION_TIMES) {
			return COLUMN_LABEL_RETENTION_TIMES;
		} else if(columnIndex == COLUMN_INDEX_PEAK_NAMES) {
			return COLUMN_LABEL_PEAKS_NAMES;
		} else {
			ISample sample = getDataTable().getSamples().get(columnIndex - NUMER_OF_DESCRIPTION_COLUMN);
			if(sample instanceof IGroup) {
				return COLUMN_LABEL_GROUP_DATA;
			} else {
				return COLUMN_LABEL_SAMPLE_DATA;
			}
		}
	}

	public TableData getDataTable() {

		return dataTable;
	}

	public int getRowCount() {

		return dataTable.getVariables().size();
	}
}
