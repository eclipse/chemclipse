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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class TargetsEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;

	public TargetsEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(column.equals(TargetsLabelProvider.VERIFIED)) {
			this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
		} else {
			this.cellEditor = new TextCellEditor(tableViewer.getTable());
		}
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {

		if(column == TargetsLabelProvider.VERIFIED) {
			return true;
		} else {
			return tableViewer.isEditEnabled();
		}
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof IIdentificationTarget identificationTarget) {
			if(column.equals(TargetsLabelProvider.VERIFIED)) {
				return identificationTarget.isVerified();
			}
			if(column.equals(TargetsLabelProvider.NAME)) {
				return identificationTarget.getLibraryInformation().getName();
			}
			if(column.equals(TargetsLabelProvider.CAS)) {
				return identificationTarget.getLibraryInformation().getCasNumber();
			}
			if(column.equals(TargetsLabelProvider.COMMENTS)) {
				return identificationTarget.getLibraryInformation().getComments();
			}
			if(column.equals(TargetsLabelProvider.FORMULA)) {
				return identificationTarget.getLibraryInformation().getFormula();
			}
			if(column.equals(TargetsLabelProvider.SMILES)) {
				return identificationTarget.getLibraryInformation().getSmiles();
			}
			if(column.equals(TargetsLabelProvider.INCHI)) {
				return identificationTarget.getLibraryInformation().getInChI();
			}
			if(column.equals(TargetsLabelProvider.CONTRIBUTOR)) {
				return identificationTarget.getLibraryInformation().getContributor();
			}
			if(column.equals(TargetsLabelProvider.REFERENCE_ID)) {
				return identificationTarget.getLibraryInformation().getReferenceIdentifier();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IIdentificationTarget identificationTarget) {
			if(column.equals(TargetsLabelProvider.VERIFIED)) {
				identificationTarget.setVerified((boolean)value);
			}
			if(column.equals(TargetsLabelProvider.NAME)) {
				identificationTarget.getLibraryInformation().setName((String)value);
			}
			if(column.equals(TargetsLabelProvider.CAS)) {
				identificationTarget.getLibraryInformation().setCasNumber((String)value);
			}
			if(column.equals(TargetsLabelProvider.COMMENTS)) {
				identificationTarget.getLibraryInformation().setComments((String)value);
			}
			if(column.equals(TargetsLabelProvider.FORMULA)) {
				identificationTarget.getLibraryInformation().setFormula((String)value);
			}
			if(column.equals(TargetsLabelProvider.SMILES)) {
				identificationTarget.getLibraryInformation().setSmiles((String)value);
			}
			if(column.equals(TargetsLabelProvider.INCHI)) {
				identificationTarget.getLibraryInformation().setInChI((String)value);
			}
			if(column.equals(TargetsLabelProvider.CONTRIBUTOR)) {
				identificationTarget.getLibraryInformation().setContributor((String)value);
			}
			if(column.equals(TargetsLabelProvider.REFERENCE_ID)) {
				identificationTarget.getLibraryInformation().setReferenceIdentifier((String)value);
			}
		}
		tableViewer.refresh();
		UpdateNotifierUI.update(tableViewer.getTable().getDisplay(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "The target has been edited.");
	}
}
