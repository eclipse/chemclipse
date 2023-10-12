/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.ui.swt;

import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.xxd.classification.ui.provider.ClassificationRuleEditingSupport;
import org.eclipse.chemclipse.xxd.classification.ui.provider.ClassificationRuleFilter;
import org.eclipse.chemclipse.xxd.classification.ui.provider.ClassificationRuleLabelProvider;
import org.eclipse.chemclipse.xxd.classification.ui.provider.ClassificationRuleTableComparator;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Composite;

public class ClassificationDictionaryListUI extends ExtendedTableViewer {

	private static final String[] TITLES = ClassificationRuleLabelProvider.TITLES;
	private static final int[] BOUNDS = ClassificationRuleLabelProvider.BOUNDS;
	//
	private ClassificationRuleLabelProvider labelProvider = new ClassificationRuleLabelProvider();
	private ClassificationRuleTableComparator tableComparator = new ClassificationRuleTableComparator();
	//
	private IUpdateListener updateListener;
	//
	private ClassificationRuleFilter listFilter = new ClassificationRuleFilter();

	public ClassificationDictionaryListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateContent() {

		if(updateListener != null) {
			updateListener.update();
		}
	}

	public void clear() {

		setInput(null);
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			tableViewerColumn.setEditingSupport(new ClassificationRuleEditingSupport(this, label));
		}
	}
}