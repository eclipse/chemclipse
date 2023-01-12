/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import java.util.Map;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class ColumMappingEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;
	private String[] columnTypes = SeparationColumnType.getItems();

	public ColumMappingEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(column.equals(ColumMappingLabelProvider.SEPRATION_COLUMN)) {
			this.cellEditor = new ComboBoxCellEditor(tableViewer.getTable(), columnTypes, SWT.READ_ONLY);
		} else {
			this.cellEditor = new TextCellEditor(tableViewer.getTable());
		}
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {

		return tableViewer.isEditEnabled();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Object getValue(Object element) {

		if(element instanceof Map.Entry setting) {
			switch(column) {
				case ColumMappingLabelProvider.SEPRATION_COLUMN:
					for(int i = 0; i < columnTypes.length; i++) {
						if(columnTypes[i].equals(setting.getValue())) {
							return i;
						}
					}
					return 0;
			}
		}
		return false;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof Map.Entry setting) {
			switch(column) {
				case ColumMappingLabelProvider.SEPRATION_COLUMN:
					setting.setValue(columnTypes[(int)value]);
					break;
			}
			tableViewer.refresh();
		}
	}
}
