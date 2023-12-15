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

import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.TableItem;

public class InternalStandardEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;

	public InternalStandardEditingSupport(ExtendedTableViewer tableViewer, String column) {

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

		Object object = null;
		if(element instanceof IInternalStandard internalStandard) {
			if(column.equals(InternalStandardsLabelProvider.NAME)) {
				object = internalStandard.getName();
			}
			if(column.equals(InternalStandardsLabelProvider.CHEMICAL_CLASS)) {
				object = internalStandard.getChemicalClass();
			}
		}
		return object;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IInternalStandard internalStandard) {
			if(column.equals(InternalStandardsLabelProvider.NAME)) {
				String name = ((String)value).trim();
				if(isRenameAllowed(name)) {
					internalStandard.setName(name);
				}
			}
			if(column.equals(InternalStandardsLabelProvider.CHEMICAL_CLASS)) {
				String chemicalClass = ((String)value).trim();
				internalStandard.setChemicalClass(chemicalClass);
			}
			tableViewer.refresh();
		}
	}

	private boolean isRenameAllowed(String name) {

		for(TableItem tableItem : tableViewer.getTable().getItems()) {
			Object object = tableItem.getData();
			if(object instanceof IInternalStandard internalStandard) {
				if(internalStandard.getName().equals(name)) {
					return false;
				}
			}
		}
		return true;
	}
}
