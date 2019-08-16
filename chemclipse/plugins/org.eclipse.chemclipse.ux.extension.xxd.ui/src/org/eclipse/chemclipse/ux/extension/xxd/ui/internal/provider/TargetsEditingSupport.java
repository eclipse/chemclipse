/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
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
		if(column.equals(TargetsLabelProvider.VERIFIED_MANUALLY)) {
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

		if(column == TargetsLabelProvider.VERIFIED_MANUALLY) {
			return true;
		} else {
			return tableViewer.isEditEnabled();
		}
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof IIdentificationTarget) {
			IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
			switch(column) {
				case TargetsLabelProvider.VERIFIED_MANUALLY:
					return identificationTarget.isManuallyVerified();
				case TargetsLabelProvider.NAME:
					return identificationTarget.getLibraryInformation().getName();
				case TargetsLabelProvider.CAS:
					return identificationTarget.getLibraryInformation().getCasNumber();
				case TargetsLabelProvider.COMMENTS:
					return identificationTarget.getLibraryInformation().getComments();
				case TargetsLabelProvider.FORMULA:
					return identificationTarget.getLibraryInformation().getFormula();
				case TargetsLabelProvider.SMILES:
					return identificationTarget.getLibraryInformation().getSmiles();
				case TargetsLabelProvider.INCHI:
					return identificationTarget.getLibraryInformation().getInChI();
				case TargetsLabelProvider.CONTRIBUTOR:
					return identificationTarget.getLibraryInformation().getContributor();
				case TargetsLabelProvider.REFERENCE_ID:
					return identificationTarget.getLibraryInformation().getReferenceIdentifier();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IIdentificationTarget) {
			IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
			switch(column) {
				case TargetsLabelProvider.VERIFIED_MANUALLY:
					identificationTarget.setManuallyVerified((boolean)value);
					break;
				case TargetsLabelProvider.NAME:
					identificationTarget.getLibraryInformation().setName((String)value);
					break;
				case TargetsLabelProvider.CAS:
					identificationTarget.getLibraryInformation().setCasNumber((String)value);
					break;
				case TargetsLabelProvider.COMMENTS:
					identificationTarget.getLibraryInformation().setComments((String)value);
					break;
				case TargetsLabelProvider.FORMULA:
					identificationTarget.getLibraryInformation().setFormula((String)value);
					break;
				case TargetsLabelProvider.SMILES:
					identificationTarget.getLibraryInformation().setSmiles((String)value);
					break;
				case TargetsLabelProvider.INCHI:
					identificationTarget.getLibraryInformation().setInChI((String)value);
					break;
				case TargetsLabelProvider.CONTRIBUTOR:
					identificationTarget.getLibraryInformation().setContributor((String)value);
					break;
				case TargetsLabelProvider.REFERENCE_ID:
					identificationTarget.getLibraryInformation().setReferenceIdentifier((String)value);
					break;
			}
			tableViewer.refresh();
		}
	}
}
