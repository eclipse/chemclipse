/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.RetentionIndexTableViewerUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class RetentionIndexEditingSupport extends EditingSupport {

	private TextCellEditor cellEditor;
	private RetentionIndexTableViewerUI tableViewer;

	public RetentionIndexEditingSupport(RetentionIndexTableViewerUI tableViewer) {

		super(tableViewer);
		this.tableViewer = tableViewer;
		cellEditor = new TextCellEditor(tableViewer.getTable());
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
		if(element instanceof IRetentionIndexEntry) {
			IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)element;
			object = retentionIndexEntry.getName();
		}
		return object;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IRetentionIndexEntry) {
			IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)element;
			retentionIndexEntry.setName(value.toString().trim());
			tableViewer.refresh();
			tableViewer.updateContent();
		}
	}
}
