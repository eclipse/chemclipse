/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.FilterWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.FiltersWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FiltersTable {

	private List<IFilter> filters;
	private Table table;

	public FiltersTable(Composite parent, Object layoutData) {
		this(parent, layoutData, new ArrayList<>());
	}

	public FiltersTable(Composite parent, Object layoutData, List<IFilter> filters) {
		this.filters = filters;
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));
		createTable(composite, new GridData(GridData.FILL_BOTH));
		Composite compositeButtons = new Composite(composite, SWT.None);
		compositeButtons.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		compositeButtons.setLayout(new FillLayout(SWT.VERTICAL | SWT.BEGINNING));
		createButtons(compositeButtons);
	}

	private void createButtons(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add");
		button.addListener(SWT.Selection, e -> {
			createNewFilter();
		});
		button = new Button(parent, SWT.PUSH);
		button.setText("Remove");
		button.addListener(SWT.Selection, e -> {
			Arrays.stream(table.getSelection()).forEach(i -> filters.remove(i.getData()));
			reload();
		});
		button = new Button(parent, SWT.PUSH);
		button.setText("Move Up");
		button.addListener(SWT.Selection, e -> {
			int i = table.getSelectionIndex();
			if(i > 0) {
				IFilter temp = filters.get(i);
				filters.set(i, filters.get(i - 1));
				filters.set(i - 1, temp);
				reload();
			}
		});
		button = new Button(parent, SWT.PUSH);
		button.setText("Move Down");
		button.addListener(SWT.Selection, e -> {
			int i = table.getSelectionIndex();
			if(i < (filters.size() - 1)) {
				IFilter temp = filters.get(i);
				filters.set(i, filters.get(i + 1));
				filters.set(i + 1, temp);
				reload();
			}
		});
	}

	private void createNewFilter() {

		FiltersWizard filtersWizard = new FiltersWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), filtersWizard);
		if(Window.OK == wizardDialog.open()) {
			IFilter filter = filtersWizard.getFilterType();
			if(filter != null) {
				FilterWizard filterWizard = new FilterWizard(filter);
				wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), filterWizard);
				if(Window.OK == wizardDialog.open()) {
					filters.add(filter);
					reload();
				}
			}
		}
	}

	private void createTable(Composite parent, Object layoutData) {

		table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(layoutData);
		table.addListener(SWT.MouseDoubleClick, e -> {
			int i = table.getSelectionIndex();
			if(i >= 0) {
				IFilter filter = filters.get(i);
				FilterWizard filterWizard = new FilterWizard(filter);
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), filterWizard);
				wizardDialog.open();
				reload();
			}
		});
		reload();
	}

	public List<IFilter> getFilters() {

		return filters;
	}

	public void reload() {

		table.clearAll();
		table.removeAll();
		String[] columns = new String[]{"Name", "Description"};
		for(int i = 0; i < columns.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.None);
			tableColumn.setText(columns[i]);
		}
		for(int i = 0; i < filters.size(); i++) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			IFilter filter = filters.get(i);
			tableItem.setData(filter);
			tableItem.setText(0, filter.getName());
			tableItem.setText(1, filter.getDescription());
		}
		for(int i = 0; i < columns.length; i++) {
			table.getColumn(i).pack();
		}
	}
}
