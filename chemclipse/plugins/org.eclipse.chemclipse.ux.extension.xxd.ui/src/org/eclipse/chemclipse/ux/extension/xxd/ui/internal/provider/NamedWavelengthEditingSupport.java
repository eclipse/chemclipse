/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DAD
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.wavelengths.NamedWavelength;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.validators.TraceValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class NamedWavelengthEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;
	//
	private TraceValidator traceValidator = new TraceValidator();

	public NamedWavelengthEditingSupport(ExtendedTableViewer tableViewer, String column) {

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

		if(element instanceof NamedWavelength) {
			NamedWavelength namedWavelength = (NamedWavelength)element;
			switch(column) {
				case NamedWavelengthsLabelProvider.WAVELENGTHS:
					return namedWavelength.getWavelengths();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof NamedWavelength) {
			NamedWavelength namedWavelength = (NamedWavelength)element;
			switch(column) {
				case NamedWavelengthsLabelProvider.WAVELENGTHS:
					IStatus status = traceValidator.validate(value);
					if(status.isOK()) {
						namedWavelength.setWavelengths(traceValidator.getTracesAsString());
					}
					break;
			}
			tableViewer.refresh();
		}
	}
}
