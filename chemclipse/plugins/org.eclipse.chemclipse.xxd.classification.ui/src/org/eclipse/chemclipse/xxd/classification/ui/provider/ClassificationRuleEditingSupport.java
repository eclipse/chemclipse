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
import org.eclipse.chemclipse.xxd.classification.model.Reference;
import org.eclipse.chemclipse.xxd.classification.ui.swt.ClassificationDictionaryListUI;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class ClassificationRuleEditingSupport extends EditingSupport {

	private String column;
	private CellEditor cellEditor;
	private ClassificationDictionaryListUI tableViewer;
	//
	private String[] references = Reference.getItems();

	public ClassificationRuleEditingSupport(ClassificationDictionaryListUI tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(ClassificationRuleLabelProvider.REFERENCE.equals(column)) {
			this.cellEditor = new ComboBoxCellEditor(tableViewer.getTable(), references, SWT.READ_ONLY);
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

		return tableViewer.isEditEnabled();
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof ClassificationRule) {
			ClassificationRule classificationRule = (ClassificationRule)element;
			switch(column) {
				case ClassificationRuleLabelProvider.SEARCH_EXPRESSION:
					return classificationRule.getSearchExpression();
				case ClassificationRuleLabelProvider.CLASSIFICATION:
					return classificationRule.getClassification();
				case ClassificationRuleLabelProvider.REFERENCE:
					return getComboIndex(classificationRule.getReference().name(), references);
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ClassificationRule) {
			ClassificationRule classificationRule = (ClassificationRule)element;
			switch(column) {
				case ClassificationRuleLabelProvider.SEARCH_EXPRESSION:
					classificationRule.setSearchExpression((String)value);
					break;
				case ClassificationRuleLabelProvider.CLASSIFICATION:
					classificationRule.setClassification((String)value);
					break;
				case ClassificationRuleLabelProvider.REFERENCE:
					classificationRule.setReference(getReference(value));
					break;
			}
			//
			tableViewer.refresh();
			tableViewer.updateContent();
		}
	}

	private int getComboIndex(String item, String[] items) {

		int index = 0;
		exitloop:
		for(int i = 0; i < items.length; i++) {
			if(items[i].equals(item)) {
				index = i;
				break exitloop;
			}
		}
		return index;
	}

	private Reference getReference(Object value) {

		try {
			return Reference.valueOf(references[(int)value]);
		} catch(Exception e) {
			return Reference.NAME;
		}
	}
}