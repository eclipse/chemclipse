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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class QuantResponseEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;

	public QuantResponseEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		this.cellEditor = new TextCellEditor(tableViewer.getTable());
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

	@Override
	protected Object getValue(Object element) {

		if(element instanceof IResponseSignal signal) {
			if(column.equals(QuantResponseLabelProvider.RESPONSE)) {
				return Double.toString(signal.getResponse());
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IResponseSignal signal) {
			if(column.equals(QuantResponseLabelProvider.RESPONSE)) {
				double response = getValue(value, -1);
				if(response >= 0) {
					signal.setResponse(response);
				}
			}
			tableViewer.refresh();
		}
	}

	private double getValue(Object value, double def) {

		double result = def;
		if(value instanceof String stringValue) {
			try {
				result = Double.parseDouble(stringValue);
			} catch(NumberFormatException e) {
				//
			}
		}
		return result;
	}
}
