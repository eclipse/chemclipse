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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.errorresidue.ErrorResidueBarChart;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.SamplesSelectionTree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ErrorResiduePage {

	private Combo comboDisplay;
	private Combo comboSortData;
	//
	private ErrorResidueBarChart errorResidueChart;
	//
	private PcaEditor pcaEditor;

	public ErrorResiduePage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		//
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Error Residues");
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
		Composite propertiesComposite = new Composite(parent, SWT.NONE);
		propertiesComposite.setLayout(new GridLayout(6, false));
		propertiesComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		comboDisplay = new Combo(propertiesComposite, SWT.READ_ONLY);
		comboDisplay.add("Display Samples");
		comboDisplay.add("Display Groups");
		comboDisplay.select(0);
		//
		comboSortData = new Combo(propertiesComposite, SWT.READ_ONLY);
		comboSortData.add("Sort by Group Name");
		comboSortData.add("Sort by Error Residue");
		comboSortData.add("Sort by Name");
		comboSortData.select(0);
		//
		Button button = new Button(propertiesComposite, SWT.PUSH);
		button.setText(" Reload Bar Plot ");
		button.addListener(SWT.Selection, e -> {
			errorResidueChart.setDisplayData(comboDisplay.getSelectionIndex());
			errorResidueChart.setSortType(comboSortData.getSelectionIndex());
			errorResidueChart.update();
		});
		button = new Button(propertiesComposite, SWT.PUSH);
		button.setText(" Select samples");
		button.addListener(SWT.Selection, e -> {
			Shell shell = new Shell(Display.getCurrent());
			shell.setLayout(new FillLayout());
			new SamplesSelectionTree(pcaEditor, shell);
			shell.pack();
			shell.open();
		});
		/*
		 * Plot the Error residue chart.
		 */
		Composite chartComposite = new Composite(parent, SWT.NONE);
		chartComposite.setLayout(new GridLayout(1, true));
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		errorResidueChart = new ErrorResidueBarChart(pcaEditor, chartComposite, formToolkit);
		tabItem.setControl(composite);
	}

	public void update() {

		errorResidueChart.update();
		comboDisplay.select(0);
		comboSortData.select(0);
	}

	public void updateSelection() {

		errorResidueChart.updateSelection();
	}
}
