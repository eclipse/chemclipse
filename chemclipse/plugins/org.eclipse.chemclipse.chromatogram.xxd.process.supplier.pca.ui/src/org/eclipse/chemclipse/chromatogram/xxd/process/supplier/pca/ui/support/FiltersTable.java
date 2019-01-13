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
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.FilterWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.FiltersWizard;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FiltersTable {

	private boolean autoUpdate;
	private PcaFiltrationData pcaFiltrationData;
	private Optional<ISamples<? extends IVariable, ? extends ISample>> samples;
	private Table table;

	public FiltersTable(Composite parent, Object layoutData) {

		this(parent, layoutData, new PcaFiltrationData());
	}

	public FiltersTable(Composite parent, Object layoutData, PcaFiltrationData pcaFiltrationData) {

		this.pcaFiltrationData = pcaFiltrationData;
		samples = Optional.empty();
		createTable(parent, layoutData);
	}

	private void autoUpdateVariables() {

		if(autoUpdate && samples.isPresent()) {
			pcaFiltrationData.process(samples.get(), new NullProgressMonitor());
		}
	}

	public void createNewFilter() {

		FiltersWizard filtersWizard = new FiltersWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), filtersWizard);
		if(Window.OK == wizardDialog.open()) {
			IFilter filter = filtersWizard.getFilterType();
			if(filter != null) {
				FilterWizard filterWizard = new FilterWizard(filter);
				wizardDialog = new BatchProcessWizardDialog(Display.getDefault().getActiveShell(), filterWizard);
				wizardDialog.setMinimumPageSize(300, 600);
				if(Window.OK == wizardDialog.open()) {
					pcaFiltrationData.getFilters().add(filter);
					autoUpdateVariables();
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
				if(Window.OK == wizardDialog.open()) {
					autoUpdateVariables();
				}
				update();
			}
		});
		String[] columns = new String[]{"Name", "Use for", "Description", "Number of selected variables or error"};
		for(int i = 0; i < columns.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.None);
			tableColumn.setText(columns[i]);
		}
		update();
	}

	public PcaFiltrationData getPcaFiltrationData() {

		return pcaFiltrationData;
	}

	public boolean isAutoUpdate() {

		return autoUpdate;
	}

	public void moveDownSelectedFilter() {

		List<IFilter> filters = pcaFiltrationData.getFilters();
		int i = table.getSelectionIndex();
		if(i > -1 && i < (filters.size() - 1)) {
			IFilter temp = filters.get(i);
			filters.set(i, filters.get(i + 1));
			filters.set(i + 1, temp);
			autoUpdateVariables();
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
			autoUpdateVariables();
			update();
		}
	}

	public void removeAllFilters() {

		pcaFiltrationData.getFilters().clear();
		autoUpdateVariables();
		update();
	}

	public void removeSelectedFilters() {

		Arrays.stream(table.getSelection()).forEach(i -> pcaFiltrationData.getFilters().remove(i.getData()));
		autoUpdateVariables();
		update();
	}

	public void setAutoUpdate(boolean autoUpdate) {

		this.autoUpdate = autoUpdate;
	}

	public void setPcaFiltrationData(PcaFiltrationData pcaFiltrationData) {

		this.pcaFiltrationData = pcaFiltrationData;
	}

	public void setSamples(ISamples<? extends IVariable, ? extends ISample> samples) {

		this.samples = Optional.of(samples);
	}

	public void update() {

		List<IFilter> filters = pcaFiltrationData.getFilters();
		table.removeAll();
		table.clearAll();
		for(int i = 0; i < filters.size(); i++) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			IFilter filter = filters.get(i);
			tableItem.setText(0, filter.getName());
			tableItem.setText(1, filter.getDataTypeProcessing().toString());
			tableItem.setText(2, filter.getDescription());
			tableItem.setText(3, filter.getSelectionResult());
			tableItem.setData(filter);
		}
		for(int i = 0; i < table.getColumns().length; i++) {
			table.getColumn(i).pack();
			table.getColumn(i).setWidth(table.getColumn(i).getWidth() + 20);
		}
	}

	public void updateVariables() {

		if(samples.isPresent()) {
			pcaFiltrationData.process(samples.get(), new NullProgressMonitor());
			update();
		}
	}
}
