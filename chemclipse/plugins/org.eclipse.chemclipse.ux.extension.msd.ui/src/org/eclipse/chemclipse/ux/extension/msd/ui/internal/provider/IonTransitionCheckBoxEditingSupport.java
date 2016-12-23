/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIonTransition;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class IonTransitionCheckBoxEditingSupport extends EditingSupport {

	private CheckboxCellEditor cellEditor;
	private TableViewer tableViewer;

	public IonTransitionCheckBoxEditingSupport(TableViewer tableViewer) {
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

		if(element instanceof IMarkedIonTransition) {
			IMarkedIonTransition markedIonTransition = (IMarkedIonTransition)element;
			return markedIonTransition.isSelected();
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IMarkedIonTransition) {
			IMarkedIonTransition markedIonTransition = (IMarkedIonTransition)element;
			markedIonTransition.setSelected(Boolean.valueOf(value.toString()));
			tableViewer.refresh();
		}
	}
}
