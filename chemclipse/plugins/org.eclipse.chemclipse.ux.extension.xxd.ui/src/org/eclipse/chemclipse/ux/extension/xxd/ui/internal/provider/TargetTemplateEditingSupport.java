/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.identifier.ITargetTemplate;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class TargetTemplateEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String column;

	public TargetTemplateEditingSupport(ExtendedTableViewer tableViewer, String column) {
		super(tableViewer);
		this.column = column;
		this.cellEditor = new TextCellEditor(tableViewer.getTable());
		this.tableViewer = tableViewer;
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

		if(element instanceof ITargetTemplate) {
			ITargetTemplate targetTemplate = (ITargetTemplate)element;
			switch(column) {
				// case TargetTemplateLabelProvider.NAME:
				// return targetTemplate.getName();
				case TargetTemplateLabelProvider.CAS:
					return targetTemplate.getCasNumber();
				case TargetTemplateLabelProvider.COMMENTS:
					return targetTemplate.getComments();
				case TargetTemplateLabelProvider.CONTRIBUTOR:
					return targetTemplate.getContributor();
				case TargetTemplateLabelProvider.REFERENCE_ID:
					return targetTemplate.getReferenceId();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ITargetTemplate) {
			ITargetTemplate targetTemplate = (ITargetTemplate)element;
			switch(column) {
				// case TargetTemplateLabelProvider.NAME:
				// targetTemplate.setName((String)value);
				// break;
				case TargetTemplateLabelProvider.CAS:
					targetTemplate.setCasNumber((String)value);
					break;
				case TargetTemplateLabelProvider.COMMENTS:
					targetTemplate.setComments((String)value);
					break;
				case TargetTemplateLabelProvider.CONTRIBUTOR:
					targetTemplate.setContributor((String)value);
					break;
				case TargetTemplateLabelProvider.REFERENCE_ID:
					targetTemplate.setReferenceId((String)value);
					break;
			}
			tableViewer.refresh();
		}
	}
}
