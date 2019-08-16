/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class QuantSignalsEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;

	public QuantSignalsEditingSupport(ExtendedTableViewer tableViewer, String column) {
		super(tableViewer);
		this.column = column;
		if(column.equals(QuantSignalsLabelProvider.USE)) {
			this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
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

	@Override
	protected Object getValue(Object element) {

		if(element instanceof IQuantitationSignal) {
			IQuantitationSignal signal = (IQuantitationSignal)element;
			switch(column) {
				case QuantSignalsLabelProvider.RELATIVE_RESPONSE:
					return Double.toString(signal.getRelativeResponse());
				case QuantSignalsLabelProvider.UNCERTAINTY:
					return Double.toString(signal.getUncertainty());
				case QuantSignalsLabelProvider.USE:
					return signal.isUse();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IQuantitationSignal) {
			IQuantitationSignal signal = (IQuantitationSignal)element;
			switch(column) {
				case QuantSignalsLabelProvider.RELATIVE_RESPONSE:
					double relativeResponse = getValue(value, -1.0f);
					if(relativeResponse >= 0) {
						signal.setRelativeResponse(relativeResponse);
					}
					break;
				case QuantSignalsLabelProvider.UNCERTAINTY:
					double uncertainty = getValue(value, -1.0d);
					if(uncertainty >= 0) {
						signal.setUncertainty(uncertainty);
					}
					break;
				case QuantSignalsLabelProvider.USE:
					signal.setUse((boolean)value);
					break;
			}
			tableViewer.refresh();
		}
	}

	private double getValue(Object value, double def) {

		double result = def;
		if(value instanceof String) {
			try {
				result = Double.parseDouble((String)value);
			} catch(NumberFormatException e) {
				//
			}
		}
		return result;
	}
}
