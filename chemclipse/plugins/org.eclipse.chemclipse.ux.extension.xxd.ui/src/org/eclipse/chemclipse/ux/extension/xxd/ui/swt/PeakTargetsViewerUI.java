/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.comparator.PeakTargetsTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakTargetsLabelProvider;
import org.eclipse.swt.widgets.Composite;

public class PeakTargetsViewerUI extends ExtendedTableViewer {

	private String[] titles = {"Name", "Match Factor", "Reverse Factor", "Match Factor Direct", "Reverse Factor Direct"};
	private int bounds[] = {150, 100, 100, 100, 100};

	public PeakTargetsViewerUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public PeakTargetsViewerUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new PeakTargetsLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new PeakTargetsTableComparator());
	}
}
