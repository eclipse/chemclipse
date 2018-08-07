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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.OverlaySettingsTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class OverlaySettingsCheckBoxEditingSupport extends EditingSupport {

	private CheckboxCellEditor cellEditor;
	private String type;

	public OverlaySettingsCheckBoxEditingSupport(TableViewer tableViewer, String type) {
		super(tableViewer);
		this.type = type;
		this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {

		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Object getValue(Object element) {

		if(element instanceof IChromatogramSelection) {
			IChromatogramSelection chromatogramSelection = (IChromatogramSelection)element;
			if(type.equals(OverlaySettingsTableViewer.OVERLAY_SELECTED)) {
				return chromatogramSelection.isOverlaySelected();
			} else if(type.equals(OverlaySettingsTableViewer.LOCK_OFFSET)) {
				return chromatogramSelection.isLockOffset();
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IChromatogramSelection) {
			IChromatogramSelection chromatogramSelection = (IChromatogramSelection)element;
			if(type.equals(OverlaySettingsTableViewer.OVERLAY_SELECTED)) {
				chromatogramSelection.setOverlaySelected((boolean)value);
			} else if(type.equals(OverlaySettingsTableViewer.LOCK_OFFSET)) {
				chromatogramSelection.setLockOffset((boolean)value);
			}
			chromatogramSelection.fireUpdateChange(false);
		}
	}
}
