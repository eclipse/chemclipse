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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.internal.editingsupport;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class SampleQuantCheckBoxEditingSupport extends EditingSupport {

	private CheckboxCellEditor cellEditor;
	private TableViewer tableViewer;

	public SampleQuantCheckBoxEditingSupport(TableViewer tableViewer) {

		super(tableViewer);
		this.tableViewer = tableViewer;
		cellEditor = new CheckboxCellEditor(tableViewer.getTable());
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

		if(element instanceof ISampleQuantSubstance sampleQuantSubstance) {
			return sampleQuantSubstance.isValidated();
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ISampleQuantSubstance sampleQuantSubstance) {
			sampleQuantSubstance.setValidated(Boolean.valueOf(value.toString()));
			tableViewer.refresh();
		}
	}
}
