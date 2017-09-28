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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ScorePlotPage {

	//
	private PcaEditor pcaEditor;
	private ScorePlot scorePlot;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;

	public ScorePlotPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private int getPCX() {

		return spinnerPCx.getSelection();
	}

	private int getPCY() {

		return spinnerPCy.getSelection();
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Score Plot");
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
		spinnerComposite.setLayout(new GridLayout(7, false));
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
		//
		Button button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Score Plot");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				scorePlot.update(getPCX(), getPCY());
			}
		});
		button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Select Samples");
		button.addListener(SWT.Selection, e -> {
			pcaEditor.openSamplesSelectionDialog();
		});
		/*
		 * Plot the PCA chart.
		 */
		Composite compositeScorePlot = new Composite(parent, SWT.None);
		compositeScorePlot.setLayout(new GridLayout(1, true));
		compositeScorePlot.setLayoutData(new GridData(GridData.FILL_BOTH));
		scorePlot = new ScorePlot(compositeScorePlot);
		scorePlot.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		//
		tabItem.setControl(composite);
	}

	public void update() {

		Optional<IPcaResults> results = pcaEditor.getPcaResults();
		if(results.isPresent()) {
			updateSpinnerPCMaxima(results.get().getNumberOfPrincipleComponents());
			scorePlot.update(results.get(), getPCX(), getPCY());
		} else {
			scorePlot.deleteSeries();
		}
	}

	public void updateSelection() {

		scorePlot.update(getPCX(), getPCY());
	}

	private void updateSpinnerPCMaxima(int numberOfPrincipleComponents) {

		spinnerPCx.setMaximum(numberOfPrincipleComponents);
		spinnerPCx.setSelection(1); // PC1
		spinnerPCy.setMaximum(numberOfPrincipleComponents);
		spinnerPCy.setSelection(2); // PC2
	}
}
