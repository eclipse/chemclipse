/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.PeakListNatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class PeakListIntensityTablePage {

	private PcaEditor pcaEditor;
	private PeakListNatTable peakListIntensityTable;

	public PeakListIntensityTablePage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	private void createButtonArea(Composite client) {

		Composite parent = new Composite(client, SWT.None);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, true));
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		createExportDataSection(parent);
	}

	private void createExportDataSection(Composite parent) {

		Group client = new Group(parent, SWT.None);
		client.setLayout(new FillLayout(SWT.VERTICAL));
		client.setText("Export");
		Button button = new Button(client, SWT.Selection);
		button.setText("Export");
		button.addListener(SWT.Selection, e -> peakListIntensityTable.exportTableDialog(Display.getDefault()));
	}

	private void createTable(Composite client) {

		peakListIntensityTable = new PeakListNatTable(client, new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Set the layout for the client.
		 */
		Composite client = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		createTable(client);
		createButtonArea(client);
		tabItem.setControl(composite);
	}

	public void update() {

		if(pcaEditor.getSamples().isPresent()) {
			peakListIntensityTable.update(pcaEditor.getSamples().get());
		}
	}
}
