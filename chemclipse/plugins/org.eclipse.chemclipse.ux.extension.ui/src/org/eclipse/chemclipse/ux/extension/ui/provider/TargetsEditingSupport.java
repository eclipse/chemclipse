/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class TargetsEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private TableViewer tableViewer;
	private String column;

	public TargetsEditingSupport(TableViewer tableViewer, String column) {
		super(tableViewer);
		this.column = column;
		if(column.equals(TargetsLabelProvider.VERIFIED_MANUALLY)) {
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

		return true;
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof IIdentificationTarget) {
			IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
			switch(column) {
				case TargetsLabelProvider.VERIFIED_MANUALLY:
					return identificationTarget.isManuallyVerified();
				case TargetsLabelProvider.NAME:
					return identificationTarget.getLibraryInformation().getName();
				case TargetsLabelProvider.CAS:
					return identificationTarget.getLibraryInformation().getCasNumber();
				case TargetsLabelProvider.COMMENTS:
					return identificationTarget.getLibraryInformation().getComments();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IIdentificationTarget) {
			IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
			switch(column) {
				case TargetsLabelProvider.VERIFIED_MANUALLY:
					identificationTarget.setManuallyVerified((boolean)value);
					break;
				case TargetsLabelProvider.NAME:
					identificationTarget.getLibraryInformation().setName((String)value);
					break;
				case TargetsLabelProvider.CAS:
					identificationTarget.getLibraryInformation().setCasNumber((String)value);
					break;
				case TargetsLabelProvider.COMMENTS:
					identificationTarget.getLibraryInformation().setComments((String)value);
					break;
			}
			tableViewer.refresh();
		}
	}
}
