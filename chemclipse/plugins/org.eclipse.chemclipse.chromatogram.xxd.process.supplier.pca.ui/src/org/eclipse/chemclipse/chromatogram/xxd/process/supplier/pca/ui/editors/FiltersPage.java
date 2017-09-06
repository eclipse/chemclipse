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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.FiltersTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class FiltersPage {

	private Composite composite;
	private FiltersTable filtersTable;
	private PcaEditor pcaEditor;

	public FiltersPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		this.pcaEditor = pcaEditor;
		initialize(tabFolder);
	}

	private void disableAll() {

		setEnable(composite, false);
	}

	private void enableAll() {

		setEnable(composite, true);
	}

	private void initialize(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Filtration");
		composite = new Composite(tabFolder, SWT.None);
		composite.setLayout(new GridLayout(2, false));
		/*
		 * Create the section.
		 */
		filtersTable = new FiltersTable(composite, new GridData(GridData.FILL_BOTH));
		/*
		 * create button area
		 */
		Composite buttonComposite = new Composite(composite, SWT.None);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, true));
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		Button button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Create New Filter");
		button.addListener(SWT.Selection, e -> filtersTable.createNewFilter());
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove Selected Filters");
		button.addListener(SWT.Selection, e -> filtersTable.removeSelectedFilters());
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove All Filters");
		button.addListener(SWT.Selection, e -> filtersTable.removeAllFilters());
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Move Up Selected Filter");
		button.addListener(SWT.Selection, e -> filtersTable.moveUpSelectedFilter());
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Move Down Selected Filter");
		button.addListener(SWT.Selection, e -> filtersTable.moveDownSelectedFilter());
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Apply Filters");
		button.addListener(SWT.Selection, e -> {
			pcaEditor.reFiltrationData();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Select All Data (All Row in Data Table)");
		button.addListener(SWT.Selection, e -> pcaEditor.setSelectAllData(true));
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Deselect All Data (All Row in Data Table)");
		button.addListener(SWT.Selection, e -> pcaEditor.setSelectAllData(false));
		disableAll();
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

		Optional<PcaFiltrationData> pcaFiltrationData = pcaEditor.getPcaFiltrationData();
		if(pcaFiltrationData.isPresent()) {
			enableAll();
			filtersTable.setPcaFiltrationData(pcaFiltrationData.get());
			filtersTable.update();
		} else {
			disableAll();
		}
	}
}
