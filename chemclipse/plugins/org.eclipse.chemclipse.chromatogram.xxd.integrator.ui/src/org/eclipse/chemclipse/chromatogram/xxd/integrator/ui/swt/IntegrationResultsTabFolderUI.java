/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.results.IIntegrationResultsUpdater;

public class IntegrationResultsTabFolderUI extends Composite implements IIntegrationResultsUpdater {

	private TabFolder tabFolder;
	private TabItem overviewIntegrationResultsTab;
	private TabItem chromatogramIntegrationResultsTab;
	private TabItem peakIntegrationResultsTab;
	private OverviewIntegrationResultsUI overviewResultsUI;
	private ChromatogramIntegrationResultsUI chromatogramResultsUI;
	private PeakIntegrationResultsUI peakResultUI;

	public IntegrationResultsTabFolderUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	@Override
	public void update(ICombinedIntegrationResult combinedIntegrationResult) {

		if(combinedIntegrationResult != null) {
			IChromatogramIntegrationResults chromatogramIntegrationResults = combinedIntegrationResult.getChromatogramIntegrationResults();
			IPeakIntegrationResults peakIntegrationResults = combinedIntegrationResult.getPeakIntegrationResults();
			/*
			 * Calculate the overview values.
			 */
			double chromatogramArea = chromatogramIntegrationResults.getTotalChromatogramArea();
			double backgroundArea = chromatogramIntegrationResults.getTotalBackgroundArea();
			double peakArea = peakIntegrationResults.getTotalPeakArea();
			overviewResultsUI.update(chromatogramArea, backgroundArea, peakArea);
			peakResultUI.update(peakIntegrationResults);
			chromatogramResultsUI.update(chromatogramIntegrationResults);
		} else {
			overviewResultsUI.update(0.0d, 0.0d, 0.0d);
			chromatogramResultsUI.update(null);
			peakResultUI.update(null);
		}
		tabFolder.setSelection(overviewIntegrationResultsTab);
	}

	@Override
	public void update(IChromatogramIntegrationResults chromatogramIntegrationResults) {

		double chromatogramArea = chromatogramIntegrationResults.getTotalChromatogramArea();
		double backgroundArea = chromatogramIntegrationResults.getTotalBackgroundArea();
		double peakArea = 0.0d;
		overviewResultsUI.update(chromatogramArea, backgroundArea, peakArea);
		peakResultUI.update(null);
		chromatogramResultsUI.update(chromatogramIntegrationResults);
		tabFolder.setSelection(chromatogramIntegrationResultsTab);
	}

	@Override
	public void update(IPeakIntegrationResults peakIntegrationResults) {

		double chromatogramArea = 0.0d;
		double backgroundArea = 0.0d;
		double peakArea = peakIntegrationResults.getTotalPeakArea();
		overviewResultsUI.update(chromatogramArea, backgroundArea, peakArea);
		chromatogramResultsUI.update(null);
		peakResultUI.update(peakIntegrationResults);
		tabFolder.setSelection(peakIntegrationResultsTab);
	}

	/**
	 * Initializes the widget.
	 */
	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new FillLayout());
		tabFolder = new TabFolder(composite, SWT.BORDER);
		/*
		 * Overview
		 */
		overviewIntegrationResultsTab = new TabItem(tabFolder, SWT.NONE);
		overviewIntegrationResultsTab.setText("Overview");
		Composite overviewComposite = new Composite(tabFolder, SWT.NONE);
		overviewComposite.setLayout(new FillLayout());
		overviewIntegrationResultsTab.setControl(overviewComposite);
		overviewResultsUI = new OverviewIntegrationResultsUI(overviewComposite, SWT.NONE);
		/*
		 * Chromatogram Integration Results
		 */
		chromatogramIntegrationResultsTab = new TabItem(tabFolder, SWT.NONE);
		chromatogramIntegrationResultsTab.setText("Chromatogram Results");
		Composite chromatogramComposite = new Composite(tabFolder, SWT.NONE);
		chromatogramComposite.setLayout(new FillLayout());
		chromatogramIntegrationResultsTab.setControl(chromatogramComposite);
		chromatogramResultsUI = new ChromatogramIntegrationResultsUI(chromatogramComposite, SWT.NONE);
		/*
		 * Peak Integration Results
		 */
		peakIntegrationResultsTab = new TabItem(tabFolder, SWT.NONE);
		peakIntegrationResultsTab.setText("Peak Results");
		Composite peakComposite = new Composite(tabFolder, SWT.NONE);
		peakComposite.setLayout(new FillLayout());
		peakIntegrationResultsTab.setControl(peakComposite);
		peakResultUI = new PeakIntegrationResultsUI(peakComposite, SWT.NONE);
		/*
		 * It's important to pack the TabFolder!
		 */
		tabFolder.pack();
	}
}
