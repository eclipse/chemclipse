/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider.PeakIntegrationResultsContentProvider;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider.PeakIntegrationResultsLabelProvider;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider.PeakIntegrationResultsTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PeakIntegrationResultsUI extends Composite {

	public static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt.peakIntegrationResultsListUI.popup";
	private ExtendedTableViewer tableViewer;
	private PeakIntegrationResultsTableComparator integrationResultTableComparator;
	private String[] titles = {"Start RT (minutes)", "Stop RT", "Integrated Area", "ion (TIC = 0)"};
	private int bounds[] = {150, 100, 100, 100};

	public PeakIntegrationResultsUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void update(IPeakIntegrationResults peakIntegrationResults) {

		tableViewer.setInput(peakIntegrationResults);
	}

	// -----------------------------------------private methods
	/**
	 * Initializes the widget.
	 */
	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		GridData gridData;
		// ------------------------------------------------------------------------------------------TabelViewer
		tableViewer = new ExtendedTableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		tableViewer.getTable().setLayoutData(gridData);
		tableViewer.setContentProvider(new PeakIntegrationResultsContentProvider());
		tableViewer.setLabelProvider(new PeakIntegrationResultsLabelProvider());
		/*
		 * Sorting the table.
		 */
		integrationResultTableComparator = new PeakIntegrationResultsTableComparator();
		tableViewer.setComparator(integrationResultTableComparator);
	}
}
