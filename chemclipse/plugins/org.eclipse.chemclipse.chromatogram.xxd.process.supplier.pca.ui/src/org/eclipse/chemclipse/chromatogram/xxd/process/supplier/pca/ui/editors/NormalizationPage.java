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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.NormalizationDataTables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class NormalizationPage {

	private Composite composite;
	private NormalizationDataTables normalizationDataTables;
	//
	private PcaEditor pcaEditor;

	public NormalizationPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
		disableAll();
	}

	private void createButton(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, true));
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Normalize data");
		button.addListener(SWT.Selection, e -> {
			pcaEditor.reNormalizationData();
		});
	}

	private void createTables(Composite parent) {

		ScrolledComposite scrollNormalizationTables = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrollNormalizationTables.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrollNormalizationTables.setExpandHorizontal(true);
		scrollNormalizationTables.setExpandVertical(true);
		Composite compositeNormalizationTables = new Composite(scrollNormalizationTables, SWT.NONE);
		scrollNormalizationTables.setContent(compositeNormalizationTables);
		compositeNormalizationTables.setLayout(new FillLayout());
		normalizationDataTables = new NormalizationDataTables(compositeNormalizationTables, null);
	}

	private void disableAll() {

		setEnable(composite, false);
	}

	private void enableAll() {

		setEnable(composite, true);
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		//
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Normalization");
		//
		composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		//
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		createTables(parent);
		createButton(parent);
		tabItem.setControl(composite);
	}

	private void setEnable(Composite parent, boolean enable) {

		for(Control control : parent.getChildren()) {
			if(control instanceof Composite) {
				Composite composite = (Composite)control;
				setEnable(composite, enable);
			}
			control.setEnabled(enable);
		}
	}

	public void update() {

		Optional<PcaNormalizationData> normalizationData = pcaEditor.getPcaNormalizationData();
		if(normalizationData.isPresent()) {
			enableAll();
			normalizationDataTables.setPcaNormalizationData(normalizationData.get());
			normalizationDataTables.update();
		} else {
			disableAll();
		}
	}
}
