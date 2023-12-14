/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class PeakCheckBoxEditingSupport extends EditingSupport {

	private CheckboxCellEditor cellEditor;
	private TableViewer tableViewer;

	public PeakCheckBoxEditingSupport(TableViewer tableViewer) {

		super(tableViewer);
		this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
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

		if(element instanceof IPeak peak) {
			return peak.isActiveForAnalysis();
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IPeak peak) {
			peak.setActiveForAnalysis((boolean)value);
			tableViewer.refresh();
		}
	}
}
