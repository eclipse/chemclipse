/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IndexNameMarker;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.IndexAssignerListUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class IndexAssignerEditingSupport extends EditingSupport {

	private TextCellEditor cellEditor;
	private IndexAssignerListUI tableViewer;

	public IndexAssignerEditingSupport(IndexAssignerListUI tableViewer) {

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
		if(element instanceof IndexNameMarker indexNameMarker) {
			object = indexNameMarker.getName();
		}
		return object;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IndexNameMarker indexNameMarker) {
			indexNameMarker.setName(value.toString().trim());
			tableViewer.refresh();
			tableViewer.updateContent();
		}
	}
}