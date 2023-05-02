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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.Map;

import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.HeaderDataListUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class HeaderDataEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private HeaderDataListUI tableViewer;
	private String column;

	public HeaderDataEditingSupport(HeaderDataListUI tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(column.equals(HeaderDataLabelProvider.VALUE)) {
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

		if(column == HeaderDataLabelProvider.VALUE) {
			return tableViewer.isEditEnabled();
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object getValue(Object element) {

		if(element instanceof Map.Entry) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>)element;
			if(column.equals(HeaderDataLabelProvider.VALUE)) {
				return entry.getValue();
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof Map.Entry) {
			IMeasurementInfo measurementInfo = tableViewer.getMeasurementInfo();
			if(measurementInfo != null) {
				/*
				 * Modify the value.
				 */
				Map.Entry<String, String> entry = (Map.Entry<String, String>)element;
				if(column.equals(HeaderDataLabelProvider.VALUE)) {
					measurementInfo.putHeaderData(entry.getKey(), (String)value);
				}
				/*
				 * Note:
				 * tableViewer.refresh(element);
				 * doesn't work here as the header data is a map,
				 * which must be reloaded on change.
				 */
				tableViewer.updateContent();
			}
		}
	}
}