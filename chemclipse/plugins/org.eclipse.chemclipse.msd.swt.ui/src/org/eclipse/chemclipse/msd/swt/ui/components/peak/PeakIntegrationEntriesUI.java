/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakIntegrationEntriesContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakIntegrationEntriesLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.PeakIntegrationEntriesTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PeakIntegrationEntriesUI {

	private ExtendedTableViewer tableViewer;
	private PeakIntegrationEntriesTableComparator peakIntegrationEntriesTableComparator;
	private String[] titles = {"Ion (0 = TIC)", "Area"};
	private int bounds[] = {100, 100};

	public PeakIntegrationEntriesUI(Composite parent, int style) {
		parent.setLayout(new FillLayout());
		/*
		 * E.g. Scan
		 */
		// SWT.VIRTUAL | SWT.FULL_SELECTION
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new PeakIntegrationEntriesContentProvider());
		tableViewer.setLabelProvider(new PeakIntegrationEntriesLabelProvider());
		/*
		 * Sorting the table.
		 */
		peakIntegrationEntriesTableComparator = new PeakIntegrationEntriesTableComparator();
		tableViewer.setComparator(peakIntegrationEntriesTableComparator);
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	public void update(IPeakMSD peak, boolean forceReload) {

		if(peak != null) {
			tableViewer.setInput(peak);
		}
	}

	public void clear() {

		tableViewer.setInput(null);
	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}
}