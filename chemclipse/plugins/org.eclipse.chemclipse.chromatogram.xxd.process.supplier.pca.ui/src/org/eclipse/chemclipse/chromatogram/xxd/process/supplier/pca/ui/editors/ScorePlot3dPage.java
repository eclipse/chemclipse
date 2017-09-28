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
 * Rafael Aguayo - initial API and implementation
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d.ScorePlot3d;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ScorePlot3dPage {

	private PcaEditor pcaEditor;
	private ScorePlot3d scorePlot3d;
	private Spinner spinnerPCx;
	private Spinner spinnerPCy;
	private Spinner spinnerPCz;

	public ScorePlot3dPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("3D Score Plot");
		//
		Composite parent = new Composite(tabFolder, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(GridData.FILL_BOTH);
		/*
		 * Selection of the plotted PCs
		 */
		Composite spinnerComposite = new Composite(parent, SWT.NONE);
		spinnerComposite.setLayout(new GridLayout(8, false));
		spinnerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label label;
		GridData gridData = new GridData();
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
		label = new Label(spinnerComposite, SWT.NONE);
		label.setText(" PC Z-Axis: ");
		spinnerPCz = new Spinner(spinnerComposite, SWT.NONE);
		spinnerPCz.setMinimum(1);
		spinnerPCz.setMaximum(1);
		spinnerPCz.setIncrement(1);
		spinnerPCz.setLayoutData(gridData);
		//
		Button button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Score Plot");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				update();
			}
		});
		button = new Button(spinnerComposite, SWT.PUSH);
		button.setText(" Select samples");
		button.addListener(SWT.Selection, e -> {
			pcaEditor.openSamplesSelectionDialog();
		});
		/*
		 * Plot the PCA chart.
		 */
		Composite chartComposite = new Composite(parent, SWT.BORDER);
		chartComposite.setLayout(new GridLayout(1, true));
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		chartComposite.setLayoutData(gridData);
		scorePlot3d = new ScorePlot3d(chartComposite, new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		tabItem.setControl(parent);
	}

	public void update() {

		Optional<IPcaResults> pcaResults = pcaEditor.getPcaResults();
		if(pcaResults.isPresent()) {
			updateSpinnerPCMaxima(pcaResults.get().getNumberOfPrincipleComponents());
			scorePlot3d.update(pcaResults.get(), spinnerPCx.getSelection(), spinnerPCy.getSelection(), spinnerPCz.getSelection());
		} else {
			scorePlot3d.removeData();
		}
	}

	public void updateSelection() {

		scorePlot3d.updateSelection();
	}

	private void updateSpinnerPCMaxima(int numberOfPrincipleComponents) {

		spinnerPCx.setMaximum(numberOfPrincipleComponents);
		spinnerPCx.setSelection(1); // PC1
		spinnerPCy.setMaximum(numberOfPrincipleComponents);
		spinnerPCy.setSelection(2); // PC2
		spinnerPCz.setMaximum(numberOfPrincipleComponents);
		spinnerPCz.setSelection(3); // PC3
	}
}
