/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.support;

import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.FilterWizard;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.FiltersWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FiltersTable extends Composite {

	private IFilterSettings filterSettings = new FilterSettings();
	private Table table;

	public FiltersTable(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public IFilterSettings getFilterSettings() {

		return filterSettings;
	}

	public void moveSelectedDown() {

		List<IFilter> filters = filterSettings.getFilters();
		int i = table.getSelectionIndex();
		if(i > -1 && i < (filters.size() - 1)) {
			IFilter temp = filters.get(i);
			filters.set(i, filters.get(i + 1));
			filters.set(i + 1, temp);
			update();
		}
	}

	public void moveSelectedUp() {

		List<IFilter> filters = filterSettings.getFilters();
		int i = table.getSelectionIndex();
		if(i > 0) {
			IFilter temp = filters.get(i);
			filters.set(i, filters.get(i - 1));
			filters.set(i - 1, temp);
			update();
		}
	}

	public void createNewFilter() {

		FiltersWizard filtersWizard = new FiltersWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), filtersWizard);
		//
		if(Window.OK == wizardDialog.open()) {
			IFilter filter = filtersWizard.getFilterType();
			if(filter != null) {
				FilterWizard filterWizard = new FilterWizard(filter);
				wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), filterWizard);
				wizardDialog.setMinimumPageSize(300, 600);
				//
				if(Window.OK == wizardDialog.open()) {
					filterSettings.getFilters().add(filter);
					update();
				}
			}
		}
	}

	public void removeSelected() {

		Arrays.stream(table.getSelection()).forEach(i -> filterSettings.getFilters().remove(i.getData()));
		update();
	}

	public void removeAll() {

		filterSettings.getFilters().clear();
		update();
	}

	public void setInput(IFilterSettings filterSettings) {

		this.filterSettings = filterSettings;
		update();
	}

	@Override
	public void update() {

		table.removeAll();
		table.clearAll();
		//
		if(filterSettings != null) {
			List<IFilter> filters = filterSettings.getFilters();
			for(int i = 0; i < filters.size(); i++) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				IFilter filter = filters.get(i);
				tableItem.setText(0, filter.getName());
				tableItem.setText(1, filter.getDataTypeProcessing().toString());
				tableItem.setText(2, filter.getDescription());
				tableItem.setText(3, filter.getSelectionResult());
				tableItem.setData(filter);
			}
			//
			for(int i = 0; i < table.getColumns().length; i++) {
				table.getColumn(i).pack();
				table.getColumn(i).setWidth(table.getColumn(i).getWidth() + 20);
			}
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		table = createTable(this);
	}

	private Table createTable(Composite parent) {

		Table table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		//
		table.addListener(SWT.MouseDoubleClick, e -> {
			int i = table.getSelectionIndex();
			if(i >= 0) {
				IFilter filter = filterSettings.getFilters().get(i);
				FilterWizard filterWizard = new FilterWizard(filter);
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), filterWizard);
				//
				if(Window.OK == wizardDialog.open()) {
					update();
				}
			}
		});
		//
		String[] columns = new String[]{"Name", "Use", "Description", "Number of selected variables or error"};
		for(int i = 0; i < columns.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setText(columns[i]);
		}
		//
		return table;
	}
}
