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

import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.ComponentsSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ScorePlotPage {

	private ComponentsSelector componentsSelector;
	//
	private PcaEditor pcaEditor;
	private ScorePlot scorePlot;

	public ScorePlotPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
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
		spinnerComposite.setLayout(new GridLayout(3, false));
		componentsSelector = new ComponentsSelector(spinnerComposite, null);
		//
		//
		Button button = new Button(spinnerComposite, SWT.PUSH);
		button.setText("Reload Score Plot");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateSelection();
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
			componentsSelector.update(results.get());
			scorePlot.update(results.get(), componentsSelector.getX(), componentsSelector.getY());
		} else {
			scorePlot.deleteSeries();
		}
	}

	public void updateSelection() {

		if(pcaEditor.getPcaResults().isPresent()) {
			scorePlot.update(pcaEditor.getPcaResults().get(), componentsSelector.getX(), componentsSelector.getY());
		}
	}
}
