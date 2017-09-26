/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.DataPreprocessingSelection;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class PreprocessingPage {

	private DataPreprocessingSelection dataPreprocessing;
	//
	private PcaEditor pcaEditor;
	private Button scalingData;

	public PreprocessingPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void createButton(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, true));
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		scalingData = new Button(composite, SWT.PUSH);
		scalingData.setText("Preprocess Data");
		scalingData.addListener(SWT.Selection, e -> {
			pcaEditor.getPcaPreprocessingData().get().process(pcaEditor.getSamples().get(), new NullProgressMonitor());
			pcaEditor.updatePreprocessoring();
		});
		scalingData.setEnabled(false);
	}

	private void createTables(Composite parent) {

		ScrolledComposite scrollNormalizationTables = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrollNormalizationTables.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrollNormalizationTables.setExpandHorizontal(true);
		scrollNormalizationTables.setExpandVertical(true);
		Composite compositeNormalizationTables = new Composite(scrollNormalizationTables, SWT.NONE);
		scrollNormalizationTables.setContent(compositeNormalizationTables);
		compositeNormalizationTables.setLayout(new FillLayout());
		dataPreprocessing = new DataPreprocessingSelection(compositeNormalizationTables, null);
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		//
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Preprocessoring");
		//
		Composite parent = new Composite(tabFolder, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));
		pcaEditor.getNewPCAWorkflow(parent, null, pcaEditor);
		//
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		createTables(composite);
		createButton(composite);
		tabItem.setControl(parent);
	}

	public void update() {

		Optional<PcaPreprocessingData> normalizationData = pcaEditor.getPcaPreprocessingData();
		if(normalizationData.isPresent()) {
			dataPreprocessing.update(normalizationData.get());
			scalingData.setEnabled(true);
		} else {
			scalingData.setEnabled(false);
		}
	}
}
