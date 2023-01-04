/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class TargetReferenceEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;
	private ITargetDisplaySettings targetDisplaySettings = null;

	public TargetReferenceEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(TargetReferenceLabelProvider.VISIBLE.equals(column)) {
			this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
		} else {
			this.cellEditor = new TextCellEditor(tableViewer.getTable());
		}
		this.tableViewer = tableViewer;
	}

	public void setTargetDisplaySettings(ITargetDisplaySettings targetDisplaySettings) {

		this.targetDisplaySettings = targetDisplaySettings;
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

		if(element instanceof ITargetReference targetReference) {
			if(column.equals(TargetReferenceLabelProvider.VISIBLE)) {
				if(targetDisplaySettings != null) {
					return targetDisplaySettings.isVisible(targetReference);
				} else {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ITargetReference targetReference) {
			if(column.equals(TargetReferenceLabelProvider.VISIBLE)) {
				if(targetDisplaySettings != null) {
					boolean visible = (boolean)value;
					targetDisplaySettings.setVisible(targetReference, visible);
				}
			}
			tableViewer.refresh();
		}
	}
}
