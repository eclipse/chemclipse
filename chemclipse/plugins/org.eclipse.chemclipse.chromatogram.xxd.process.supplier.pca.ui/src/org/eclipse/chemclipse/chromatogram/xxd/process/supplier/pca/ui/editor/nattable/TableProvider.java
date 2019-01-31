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

import java.util.HashSet;
import java.util.Set;

public class TableProvider {

	public static final int COLUMN_INDEX_SELECTED = 0;
	public static final int COLUMN_INDEX_COLOR = 1;
	public static final int COLUMN_INDEX_VARIABLES = 2;
	public static final int COLUMN_INDEX_PEAK_NAMES = 3;
	public static final int COLUMN_INDEX_CLASSIFICATIONS = 4;
	public static final String COLUMN_LABEL_PEAKS_NAMES = "COLUMN_PEAKS_NAMES";
	public static final String COLUMN_LABEL_RETENTION_TIMES = "COLUMN_RETENTION_TIMES";
	public static final String COLUMN_LABEL_SAMPLE_DATA = "COLUMN_SAMPLE_DATA";
	public static final String COLUMN_LABEL_SELECTED = "COLUMN_SELECTED";
	public static final String COLUMN_LABEL_CLASSIFICATIONS = "COLUMN_CLASSIFICATIONS";
	public static final String LABEL_COLOR = "LABEL_COLOR";
	public static final String ROW_LABEL_UNSELECTED = "ROW_LABEL_UNSELECTED";
	/**
	 * number of column which is used to describe sample data
	 */
	final public static int NUMER_OF_DESCRIPTION_COLUMN = 5;
	private TableData dataTable;
	private SortModel sortModel;

	public TableProvider(TableData dataTable, SortModel sortModel) {

		this.dataTable = dataTable;
		this.sortModel = sortModel;
	}

	public int getColumnCount() {

		return dataTable.getSamples().size() + NUMER_OF_DESCRIPTION_COLUMN;
	}

	public Set<String> getLabels(int columnIndex, int row) {

		int rowSorted = sortModel.getOrderRow().get(row);
		Set<String> lables = new HashSet<>();
		if(columnIndex == COLUMN_INDEX_SELECTED) {
			lables.add(COLUMN_LABEL_SELECTED);
		} else if(columnIndex == COLUMN_INDEX_COLOR) {
			int color = dataTable.getVariables().get(rowSorted).getColor();
			lables.add(getColorLabel(color));
		} else if(columnIndex == COLUMN_INDEX_VARIABLES) {
			lables.add(COLUMN_LABEL_RETENTION_TIMES);
		} else if(columnIndex == COLUMN_INDEX_PEAK_NAMES) {
			lables.add(COLUMN_LABEL_PEAKS_NAMES);
		} else if(columnIndex == COLUMN_INDEX_CLASSIFICATIONS) {
			lables.add(COLUMN_LABEL_CLASSIFICATIONS);
		} else {
			lables.add(COLUMN_LABEL_SAMPLE_DATA);
		}
		if(!dataTable.getVariables().get(rowSorted).isSelected() || !dataTable.getModifiableRowList()[rowSorted]) {
			lables.add(ROW_LABEL_UNSELECTED);
		}
		return lables;
	}

	public Set<String> getLabels() {

		Set<String> lables = new HashSet<>();
		lables.add(COLUMN_LABEL_SELECTED);
		lables.add(COLUMN_LABEL_RETENTION_TIMES);
		lables.add(COLUMN_LABEL_PEAKS_NAMES);
		lables.add(COLUMN_LABEL_CLASSIFICATIONS);
		lables.add(COLUMN_LABEL_SAMPLE_DATA);
		lables.add(ROW_LABEL_UNSELECTED);
		for(Integer color : dataTable.getColores()) {
			lables.add(getColorLabel(color));
		}
		return lables;
	}

	public TableData getDataTable() {

		return dataTable;
	}

	public String getColorLabel(Integer color) {

		return COLUMN_LABEL_CLASSIFICATIONS + "_" + color;
	}

	public int getRowCount() {

		return dataTable.getVariables().size();
	}
}
