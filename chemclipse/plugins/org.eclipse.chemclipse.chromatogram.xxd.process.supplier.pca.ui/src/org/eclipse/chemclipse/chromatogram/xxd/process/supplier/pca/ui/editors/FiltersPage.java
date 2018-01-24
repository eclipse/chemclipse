/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.FiltersTable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class FiltersPage {

	private Composite composite;
	private Label countSelectedRow;
	private FiltersTable filtersTable;
	final private String MANUAL_SELECTION = "You can select/deselect rows manually and check results in page Data Table.";
	private PcaEditor pcaEditor;

	public FiltersPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		this.pcaEditor = pcaEditor;
		initialize(tabFolder);
	}

	private void applyFilters() {

		pcaEditor.getPcaFiltrationData().get().process(pcaEditor.getSamples().get(), new NullProgressMonitor());
		pcaEditor.updateFilters();
		updateLabelTotalSelection();
		filtersTable.update();
	}

	private void createButton(Composite parent) {

		Composite buttonComposite = new Composite(parent, SWT.None);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, true));
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		Button button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Apply Filters");
		button.addListener(SWT.Selection, e -> {
			applyFilters();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Create New Filter");
		button.addListener(SWT.Selection, e -> {
			filtersTable.createNewFilter();
			applyFilters();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove Selected Filters");
		button.addListener(SWT.Selection, e -> {
			filtersTable.removeSelectedFilters();
			applyFilters();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove All Filters");
		button.addListener(SWT.Selection, e -> {
			filtersTable.removeAllFilters();
			applyFilters();
		});
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
		Composite parent = new Composite(tabFolder, SWT.None);
		parent.setLayout(new GridLayout(1, false));
		pcaEditor.getNewPCAWorkflow(parent, null, pcaEditor);
		composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		/*
		 * Create the section.
		 */
		filtersTable = new FiltersTable(composite, new GridData(GridData.FILL_BOTH));
		/*
		 * create button area
		 */
		createButton(composite);
		/*
		 *
		 */
		countSelectedRow = new Label(parent, SWT.None);
		updateLabelTotalSelection();
		ImageHyperlink imageHyperlink = new ImageHyperlink(parent, SWT.None);
		imageHyperlink.setText(MANUAL_SELECTION);
		imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				pcaEditor.showDataTable();
			}
		});
		disableAll();
		tabItem.setControl(parent);
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
			updateLabelTotalSelection();
		} else {
			disableAll();
		}
	}

	private void updateLabelTotalSelection() {

		long count = 0;
		if(pcaEditor.getSamples().isPresent()) {
			count = pcaEditor.getSamples().get().getVariables().stream().filter(IVariable::isSelected).count();
		}
		countSelectedRow.setText("It will be selected " + count + " rows");
		countSelectedRow.getParent().layout();
	}
}
