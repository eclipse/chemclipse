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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable.PeakListNatTable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class PeakListIntensityTablePage {

	private PcaEditor pcaEditor;
	private PeakListNatTable peakListIntensityTable;
	private Section section;

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
		section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
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

	private void createTable(Composite client, FormToolkit formToolkit) {

		Composite composite = new Composite(client, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
		peakListIntensityTable = new PeakListNatTable(pcaEditor, composite, formToolkit);
		peakListIntensityTable.getNatTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
		tabItem.setControl(composite);
	}

	public void update() {

		peakListIntensityTable.update();
	}
}
