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
import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.RetentionTime2Filter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.LoadingPlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class LoadingPlotPage {

	//
	private LoadingPlot loadingPlot;
	//
	private PcaEditor pcaEditor;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;

	public LoadingPlotPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		;
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void addDataToSelection() {

		for(String selection : loadingPlot.getActualSelection()) {
			loadingPlot.getBaseChart().selectSeries(selection);
		}
		loadingPlot.getBaseChart().redraw();
	}

	private void createButtonsArea(Composite parent, Object layoutData) {

		Group composite = new Group(parent, SWT.None);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout());
		composite.setText("Create new retention times filter");
		Button button = new Button(composite, SWT.None);
		button.setText("Select Displayed Retention Times");
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addListener(SWT.Selection, e -> addDataToSelection());
		button = new Button(composite, SWT.None);
		button.setText("Deselect Displayed Retention Times");
		button.addListener(SWT.Selection, e -> removeDataFromSelection());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lable = new Label(composite, SWT.None);
		lable.setText("You can also select/deselect \n retention times manually using doubleclick");
		button = new Button(composite, SWT.None);
		button.setText("Cancel Selection");
		button.addListener(SWT.Selection, e -> removeSelection());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button = new Button(composite, SWT.None);
		button.setText("Create Filter");
		button.addListener(SWT.Selection, e -> createFilter());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ImageHyperlink imageHyperlink = new ImageHyperlink(composite, SWT.None);
		imageHyperlink.setText("You can find created filter in page \n Data Filtration");
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				pcaEditor.showFiltersPage();
			}
		});
		imageHyperlink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createFilter() {

		if(pcaEditor.getPcaFiltrationData().isPresent()) {
			PcaFiltrationData pcaFiltrationData = pcaEditor.getPcaFiltrationData().get();
			List<IRetentionTime> values = new ArrayList<>();
			for(String item : loadingPlot.getBaseChart().getSelectedSeriesIds()) {
				values.add(loadingPlot.getExtractedValues().get(item));
			}
			if(!values.isEmpty()) {
				Collections.sort(values);
				pcaFiltrationData.getFilters().add(new RetentionTime2Filter(IRetentionTime.copy(values), false));
				pcaEditor.updateSamples();
				removeSelection();
			}
		}
	}

	private int getPCX() {

		return spinnerPCx.getSelection();
	}

	private int getPCY() {

		return spinnerPCy.getSelection();
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
		button.addListener(SWT.Selection, e -> {
			if(pcaEditor.getPcaResults().isPresent()) {
				loadingPlot.update(pcaEditor.getPcaResults().get(), getPCX(), getPCY());
			}
		});
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
		createButtonsArea(body, new GridData(GridData.FILL_VERTICAL));
		//
		tabItem.setControl(composite);
	}

	private void removeDataFromSelection() {

		loadingPlot.deselect(loadingPlot.getActualSelection());
	}

	private void removeSelection() {

		loadingPlot.getBaseChart().resetSeriesSettings();
		loadingPlot.getActualSelection().clear();
		loadingPlot.getBaseChart().redraw();
	}

	public void update() {

		Optional<IPcaResults> results = pcaEditor.getPcaResults();
		if(results.isPresent()) {
			updateSpinnerPCMaxima(results.get().getNumberOfPrincipleComponents());
			removeSelection();
			loadingPlot.update(results.get(), getPCX(), getPCY());
		} else {
			loadingPlot.deleteSeries();
		}
	}

	private void updateSpinnerPCMaxima(int numberOfPrincipleComponents) {

		spinnerPCx.setMaximum(numberOfPrincipleComponents);
		spinnerPCx.setSelection(1); // PC1
		spinnerPCy.setMaximum(numberOfPrincipleComponents);
		spinnerPCy.setSelection(2); // PC2
	}
}
