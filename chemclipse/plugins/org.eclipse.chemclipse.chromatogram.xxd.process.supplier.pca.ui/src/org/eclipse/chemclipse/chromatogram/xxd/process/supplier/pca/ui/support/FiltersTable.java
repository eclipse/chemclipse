/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.FilterWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.FiltersWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FiltersTable {

	private PcaFiltrationData pcaFiltrationData;
	private Table table;

	public FiltersTable(Composite parent, Object layoutData) {
		this(parent, layoutData, new PcaFiltrationData());
	}

	public FiltersTable(Composite parent, Object layoutData, PcaFiltrationData pcaFiltrationData) {
		this.pcaFiltrationData = pcaFiltrationData;
		createTable(parent, layoutData);
	}

	public void createNewFilter() {

		List<IFilter> filters = pcaFiltrationData.getFilters();
		FiltersWizard filtersWizard = new FiltersWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), filtersWizard);
		if(Window.OK == wizardDialog.open()) {
			IFilter filter = filtersWizard.getFilterType();
			if(filter != null) {
				FilterWizard filterWizard = new FilterWizard(filter);
				wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), filterWizard);
				wizardDialog.setMinimumPageSize(300, 600);
				if(Window.OK == wizardDialog.open()) {
					filters.add(filter);
					update();
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
				IFilter filter = pcaFiltrationData.getFilters().get(i);
				FilterWizard filterWizard = new FilterWizard(filter);
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), filterWizard);
				wizardDialog.open();
				update();
			}
		});
		String[] columns = new String[]{"Name", "Description", "Number of selected row or error"};
		for(int i = 0; i < columns.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.None);
			tableColumn.setText(columns[i]);
		}
		update();
	}

	public PcaFiltrationData getPcaFiltrationData() {

		return pcaFiltrationData;
	}

	public void moveDownSelectedFilter() {

		List<IFilter> filters = pcaFiltrationData.getFilters();
		int i = table.getSelectionIndex();
		if(i > -1 && i < (filters.size() - 1)) {
			IFilter temp = filters.get(i);
			filters.set(i, filters.get(i + 1));
			filters.set(i + 1, temp);
			update();
		}
	}

	public void moveUpSelectedFilter() {

		List<IFilter> filters = pcaFiltrationData.getFilters();
		int i = table.getSelectionIndex();
		if(i > 0) {
			IFilter temp = filters.get(i);
			filters.set(i, filters.get(i - 1));
			filters.set(i - 1, temp);
			update();
		}
	}

	public void removeAllFilters() {

		pcaFiltrationData.getFilters().clear();
		update();
	}

	public void removeSelectedFilters() {

		Arrays.stream(table.getSelection()).forEach(i -> pcaFiltrationData.getFilters().remove(i.getData()));
		update();
	}

	public void setPcaFiltrationData(PcaFiltrationData pcaFiltrationData) {

		this.pcaFiltrationData = pcaFiltrationData;
	}

	public void update() {

		List<IFilter> filters = pcaFiltrationData.getFilters();
		table.removeAll();
		table.clearAll();
		for(int i = 0; i < filters.size(); i++) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			IFilter filter = filters.get(i);
			tableItem.setText(0, filter.getName());
			tableItem.setText(1, filter.getDescription());
			tableItem.setText(2, filter.getSelectionResult());
			tableItem.setData(filter);
		}
		for(int i = 0; i < table.getColumns().length; i++) {
			table.getColumn(i).pack();
			table.getColumn(i).setWidth(table.getColumn(i).getWidth() + 20);
		}
	}
}
