/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakQuantitationContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakQuantitationLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.PeakQuantitationTableComparator;
import org.eclipse.swt.widgets.Composite;

public class PeakQuantitationListUI extends ExtendedTableViewer {

	private PeakQuantitationTableComparator peakQuantitationTableComparator;

	public PeakQuantitationListUI(Composite parent, int style) {
		super(parent, style);
		peakQuantitationTableComparator = new PeakQuantitationTableComparator();
		createColumns();
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

	}

	private void createColumns() {

		createColumns(PeakQuantitationLabelProvider.TITLES, PeakQuantitationLabelProvider.BOUNDS);
		setLabelProvider(new PeakQuantitationLabelProvider());
		setContentProvider(new PeakQuantitationContentProvider());
		setComparator(peakQuantitationTableComparator);
		setEditingSupport();
	}

	private void setEditingSupport() {

	}
}
