/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class ScanListEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;
	//
	private ScanDataSupport scanDataSupport = new ScanDataSupport();

	public ScanListEditingSupport(ExtendedTableViewer tableViewer, String column) {
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

		if(column == ScanListLabelProvider.NAME) {
			if(element instanceof IScan) {
				ILibraryInformation libraryInformation = scanDataSupport.getLibraryInformation((IScan)element);
				return libraryInformation != null;
			}
			return true;
		} else {
			return tableViewer.isEditEnabled();
		}
	}

	@Override
	protected Object getValue(Object element) {

		boolean editIsEnabled = tableViewer.isEditEnabled();
		if(editIsEnabled && element instanceof IScan) {
			IScan scan = (IScan)element;
			switch(column) {
				case ScanListLabelProvider.NAME:
					ILibraryInformation libraryInformation = scanDataSupport.getLibraryInformation(scan);
					return (libraryInformation != null) ? libraryInformation.getName() : "";
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IScan) {
			IScan scan = (IScan)element;
			switch(column) {
				case ScanListLabelProvider.NAME:
					ILibraryInformation libraryInformation = scanDataSupport.getLibraryInformation(scan);
					if(libraryInformation != null) {
						libraryInformation.setName((String)value);
					}
					break;
			}
			tableViewer.refresh();
		}
	}
}
