/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Daniel Mariano, Rafael Aguayo - additional functionality and UI improvements
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.RetentionTime2Filter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.LoadingPlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class LoadingPlotPage {

	private Table actualSelectionTable;
	//
	private LoadingPlot loadingPlot;
	//
	private PcaEditor pcaEditor;
	final private SortedSet<String> selectedData = new TreeSet<>();
	private Table selectedDataTable;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;

	public LoadingPlotPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		;
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void addDataToSelection() {

		selectedDataTable.clearAll();
		selectedDataTable.removeAll();
		for(TableItem item : actualSelectionTable.getItems()) {
			selectedData.add((String)item.getData());
		}
		Iterator<String> it = selectedData.iterator();
		while(it.hasNext()) {
			String retentionTime = it.next();
			TableItem tableItem = new TableItem(selectedDataTable, SWT.NONE);
			tableItem.setText(0, retentionTime);
			tableItem.setData(retentionTime);
		}
		removeAcutalSelection();
		loadingPlot.getActualSelection().clear();
		updateSelection();
		loadingPlot.update();
	}

	private void createFilter() {

		if(pcaEditor.getPcaFiltrationData().isPresent()) {
			PcaFiltrationData pcaFiltrationData = pcaEditor.getPcaFiltrationData().get();
			List<Integer> values = new ArrayList<>();
			for(TableItem item : selectedDataTable.getItems()) {
				Object data = item.getData();
				values.add(loadingPlot.getExtractedValues().get(data).getRetentionTime());
			}
			if(!values.isEmpty()) {
				Collections.sort(values);
				pcaFiltrationData.getFilters().add(new RetentionTime2Filter(values, false));
				pcaEditor.updateSamples();
				removeSelection();
			}
		}
	}

	private void createTables(Composite parent, Object layoutData) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout());
		actualSelectionTable = new Table(composite, SWT.BORDER);
		actualSelectionTable.setHeaderVisible(true);
		actualSelectionTable.setLinesVisible(true);
		actualSelectionTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn tableColumn = new TableColumn(actualSelectionTable, SWT.None);
		tableColumn.setText("Retention Time (Minutes)");
		tableColumn.setWidth(200);
		Button button = new Button(composite, SWT.None);
		button.setText("Add to selected list");
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addListener(SWT.Selection, e -> addDataToSelection());
		button = new Button(composite, SWT.None);
		button.setText("Remove from selected list");
		button.addListener(SWT.Selection, e -> removeDataFromSelection());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectedDataTable = new Table(composite, SWT.BORDER);
		selectedDataTable.setHeaderVisible(true);
		selectedDataTable.setLinesVisible(true);
		selectedDataTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		button = new Button(composite, SWT.None);
		button.setText("Remove selection");
		button.addListener(SWT.Selection, e -> removeSelection());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button = new Button(composite, SWT.None);
		button.setText("Create Filter");
		button.addListener(SWT.Selection, e -> createFilter());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableColumn = new TableColumn(selectedDataTable, SWT.None);
		tableColumn.setText("Retention Time (Minutes)");
		tableColumn.setWidth(200);
	}

	private int getPCX() {

		return spinnerPCx.getSelection();
	}

	private int getPCY() {

		return spinnerPCy.getSelection();
	}

	public SortedSet<String> getSelectedData() {

		return selectedData;
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Loading Plot");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		//
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(GridData.FILL_BOTH);
		/*
		 * Selection of the plotted PCs
		 */
		Composite spinnerComposite = new Composite(parent, SWT.NONE);
		spinnerComposite.setLayout(new GridLayout(9, false));
		spinnerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label label;
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText("PC X-Axis: ");
		spinnerPCx = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCx.setMinimum(1);
		spinnerPCx.setMaximum(1);
		spinnerPCx.setIncrement(1);
		spinnerPCx.setLayoutData(gridData);
		//
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText(" PC Y-Axis: ");
		spinnerPCy = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCy.setMinimum(1);
		spinnerPCy.setMaximum(1);
		spinnerPCy.setIncrement(1);
		spinnerPCy.setLayoutData(gridData);
		Button button = new Button(spinnerComposite, SWT.RADIO);
		button.setText("Display Retention Times");
		button.addListener(SWT.Selection, e -> {
			loadingPlot.setLabelsType(LoadingPlot.LABELS_RETENTION_TIME_MINUTES);
		});
		button.setSelection(true);
		button = new Button(spinnerComposite, SWT.RADIO);
		button.setText("Display Description");
		button.addListener(SWT.Selection, e -> {
			loadingPlot.setLabelsType(LoadingPlot.LABELS_DESCRIPTION);
		});
		button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Loading Plot");
		button.addListener(SWT.Selection, e -> loadingPlot.update(getPCX(), getPCY()));
		//
		/*
		 * Plot the PCA chart.
		 */
		Composite body = new Composite(parent, SWT.None);
		body.setLayout(new GridLayout(2, false));
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite chartComposite = new Composite(body, SWT.BORDER);
		chartComposite.setLayout(new GridLayout(1, false));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		loadingPlot = new LoadingPlot(chartComposite, this);
		loadingPlot.setLayoutData(new GridData(GridData.FILL_BOTH));
		createTables(body, new GridData(GridData.FILL_VERTICAL));
		//
		tabItem.setControl(composite);
	}

	private void removeAcutalSelection() {

		actualSelectionTable.clearAll();
		actualSelectionTable.removeAll();
	}

	private void removeDataFromSelection() {

		selectedDataTable.clearAll();
		selectedDataTable.removeAll();
		for(TableItem item : actualSelectionTable.getItems()) {
			selectedData.remove(item.getData());
		}
		Iterator<String> it = selectedData.iterator();
		while(it.hasNext()) {
			String retentionTime = it.next();
			TableItem tableItem = new TableItem(selectedDataTable, SWT.NONE);
			tableItem.setText(0, retentionTime);
			tableItem.setData(retentionTime);
		}
		removeAcutalSelection();
		loadingPlot.getActualSelection().clear();
		updateSelection();
		loadingPlot.update();
	}

	private void removeSelection() {

		removeAcutalSelection();
		selectedDataTable.clearAll();
		selectedDataTable.removeAll();
		selectedData.clear();
		loadingPlot.update();
	}

	public void update() {

		Optional<IPcaResults> results = pcaEditor.getPcaResults();
		if(results.isPresent()) {
			updateSpinnerPCMaxima(results.get().getNumberOfPrincipleComponents());
			removeSelection();
			loadingPlot.update(results.get(), getPCX(), getPCY());
		}
	}

	public void updateSelection() {

		actualSelectionTable.clearAll();
		actualSelectionTable.removeAll();
		for(String time : loadingPlot.getActualSelection()) {
			TableItem tableItem = new TableItem(actualSelectionTable, SWT.NONE);
			tableItem.setText(0, time);
			tableItem.setData(time);
		}
	}

	private void updateSpinnerPCMaxima(int numberOfPrincipleComponents) {

		spinnerPCx.setMaximum(numberOfPrincipleComponents);
		spinnerPCx.setSelection(1); // PC1
		spinnerPCy.setMaximum(numberOfPrincipleComponents);
		spinnerPCy.setSelection(2); // PC2
	}
}
