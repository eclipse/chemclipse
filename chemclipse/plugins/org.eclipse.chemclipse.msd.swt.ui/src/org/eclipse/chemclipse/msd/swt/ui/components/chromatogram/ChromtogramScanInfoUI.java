/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.chromatogram;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.ChromatogramScanInfoLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.ChromtogramScanInfoContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.ChromtogramScanInfoTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.widgets.Composite;

public class ChromtogramScanInfoUI extends ExtendedTableViewer {

	private String[] titles = {"Scan#", "RT (Minutes)", "Number Of Ions", "SIM/SCAN", "m/z..."};
	private int bounds[] = {120, 120, 120, 120, 120};

	public ChromtogramScanInfoUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
		addCopyToClipboardListener(titles);
	}

	public void update(IChromatogramSelectionMSD chromatogramSelectionMSD, boolean forceReload) {

		if(chromatogramSelectionMSD != null) {
			setInput(chromatogramSelectionMSD);
		} else {
			setInput(null);
		}
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new ChromatogramScanInfoLabelProvider());
		setContentProvider(new ChromtogramScanInfoContentProvider());
		setComparator(new ChromtogramScanInfoTableComparator());
	}
}
