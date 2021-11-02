/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.ui.internal.provider;

import java.util.Map;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class ColumExtractorEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;
	private static final String[] ITEMS = new String[]{ //
			SeparationColumnType.DEFAULT.value(), //
			SeparationColumnType.APOLAR.value(), //
			SeparationColumnType.SEMI_POLAR.value(), //
			SeparationColumnType.POLAR.value() //
	};

	public ColumExtractorEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(column.equals(ColumExtractorLabelProvider.SEPRATION_COLUMN)) {
			this.cellEditor = new ComboBoxCellEditor(tableViewer.getTable(), ITEMS, SWT.READ_ONLY);
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

		if(element instanceof Map.Entry) {
			Map.Entry setting = (Map.Entry)element;
			switch(column) {
				case ColumExtractorLabelProvider.SEPRATION_COLUMN:
					for(int i = 0; i < ITEMS.length; i++) {
						if(ITEMS[i].equals(setting.getValue())) {
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

		if(element instanceof Map.Entry) {
			Map.Entry setting = (Map.Entry)element;
			switch(column) {
				case ColumExtractorLabelProvider.SEPRATION_COLUMN:
					setting.setValue(ITEMS[(int)value]);
					break;
			}
			tableViewer.refresh();
		}
	}
}
