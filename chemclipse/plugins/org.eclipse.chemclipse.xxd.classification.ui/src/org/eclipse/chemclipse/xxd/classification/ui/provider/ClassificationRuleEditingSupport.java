/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.ui.provider;

import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.chemclipse.xxd.classification.ui.swt.ClassificationDictionaryListUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class ClassificationRuleEditingSupport extends EditingSupport {

	private String column;
	private CellEditor cellEditor;
	private ClassificationDictionaryListUI tableViewer;

	public ClassificationRuleEditingSupport(ClassificationDictionaryListUI tableViewer, String column) {

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

		if(element instanceof ClassificationRule) {
			ClassificationRule rule = (ClassificationRule)element;
			switch(column) {
				case ClassificationRuleLabelProvider.SEARCH_EXPRESSION:
					return rule.getSearchExpression();
				case ClassificationRuleLabelProvider.CLASSIFICATION:
					return rule.getClassification();
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ClassificationRule) {
			ClassificationRule rule = (ClassificationRule)element;
			switch(column) {
				case ClassificationRuleLabelProvider.SEARCH_EXPRESSION:
					rule.setSearchExpression((String)value);
					break;
				case ClassificationRuleLabelProvider.CLASSIFICATION:
					rule.setClassification((String)value);
					break;
			}
			//
			tableViewer.refresh();
			tableViewer.updateContent();
		}
	}
}
