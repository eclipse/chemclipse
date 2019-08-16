/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class SampleQuantTextEditingSupport extends EditingSupport {

	private TextCellEditor cellEditor;
	private TableViewer tableViewer;

	public SampleQuantTextEditingSupport(TableViewer tableViewer) {
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

		return true;
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof ISampleQuantSubstance) {
			ISampleQuantSubstance sampleQuantSubstance = (ISampleQuantSubstance)element;
			return Double.toString(sampleQuantSubstance.getMinMatchQuality());
		}
		return "";
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ISampleQuantSubstance) {
			ISampleQuantSubstance sampleQuantSubstance = (ISampleQuantSubstance)element;
			sampleQuantSubstance.setMinMatchQuality(Double.valueOf(value.toString()));
			tableViewer.refresh();
		}
	}
}
