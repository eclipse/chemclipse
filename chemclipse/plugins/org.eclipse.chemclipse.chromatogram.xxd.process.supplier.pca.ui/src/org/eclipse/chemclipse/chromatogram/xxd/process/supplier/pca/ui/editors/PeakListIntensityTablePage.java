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

import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.PeakListNatTable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.export.ExportData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class PeakListIntensityTablePage {

	private PcaEditor pcaEditor;
	private PeakListNatTable peakListIntensityTable;

	public PeakListIntensityTablePage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void createPeakListIntensityTableSection(Composite parent, FormToolkit formToolkit) {

		Composite client;
		GridLayout layout;
		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Peak Intensity Table");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		/*
		 * Set the layout for the client.
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		createTable(client, formToolkit);
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createProcessDataSection(Composite parent, FormToolkit formToolkit) {

		Composite client;
		GridLayout layout;
		/*
		 * Section
		 */
		Section section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Export data");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		/*
		 * Set the layout for the client.
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		section.setClient(client);
		formToolkit.paintBordersFor(client);
		/*
		 * create export button
		 */
		Button button = new Button(client, SWT.CHECK);
		button.setText("Export means group");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Button btn = (Button)e.getSource();
				peakListIntensityTable.getExporter().setExportGroupMeans(btn.getSelection());
			}
		});
		button.setSelection(true);
		button = new Button(client, SWT.CHECK);
		button.setText("Export samples");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Button btn = (Button)e.getSource();
				peakListIntensityTable.getExporter().setExportSamples(btn.getSelection());
			}
		});
		button.setSelection(true);
		button = new Button(client, SWT.Selection);
		button.setText(" Export as ");
		button.addListener(SWT.Selection, e -> exportTableDialog());
	}

	private void createTable(Composite client, FormToolkit formToolkit) {

		Composite composite = new Composite(client, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
		peakListIntensityTable = new PeakListNatTable(pcaEditor, composite, formToolkit);
		peakListIntensityTable.getNatTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void exportTableDialog() {

		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		dialog.setFilterNames(new String[]{"Excel(*.xlsx)", "Excel(97-2003)(*.xls)", "CSV(*.csv)"});
		dialog.setFilterExtensions(new String[]{"*.xlsx", "*.xls", "*.csv"});
		dialog.setFileName(ExportData.DEF_FILE_NAME + ".xlsx");
		dialog.setOverwrite(true);
		String path = dialog.open();
		if(path != null) {
			try {
				int filterIndex = dialog.getFilterIndex();
				switch(filterIndex) {
					case 1:
						peakListIntensityTable.getExporter().exportToXLS(path);
						break;
					case 2:
						peakListIntensityTable.getExporter().exportToCSV(path);
						break;
					default:
						peakListIntensityTable.getExporter().exportToXLSX(path);
						break;
				}
			} catch(IOException e1) {
				MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning", e1.getMessage());
			}
		}
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new GridLayout(1, false));
		scrolledForm.setText("Peak Intensity Table Editor");
		createPeakListIntensityTableSection(scrolledFormComposite, formToolkit);
		createProcessDataSection(scrolledFormComposite, formToolkit);
		tabItem.setControl(composite);
	}

	public void update() {

		peakListIntensityTable.update();
	}
}
