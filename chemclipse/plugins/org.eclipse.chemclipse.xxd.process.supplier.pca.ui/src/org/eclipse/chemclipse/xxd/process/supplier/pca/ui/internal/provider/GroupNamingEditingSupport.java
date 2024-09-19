/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt.SampleGroupAssignerListUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;

public class GroupNamingEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private final SampleGroupAssignerListUI tableViewer;
	private final String column;

	public GroupNamingEditingSupport(SampleGroupAssignerListUI tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(SampleGroupAssignerLabelProvider.SELECT.equals(column)) {
			this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
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

		if(element instanceof ISample sample) {
			switch(column) {
				case SampleGroupAssignerLabelProvider.SELECT:
					return sample.isSelected();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ISample sample) {
			switch(column) {
				case SampleGroupAssignerLabelProvider.SELECT:
					sample.setSelected((boolean)value);
					break;
			}
			tableViewer.refresh();
		}
	}
}
