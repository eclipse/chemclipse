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
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantitationListContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantitationListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantitationListTableComparator;
import org.eclipse.swt.widgets.Composite;

public class QuantitationListUI extends ExtendedTableViewer {

	private QuantitationListTableComparator quantitationListTableComparator;

	public QuantitationListUI(Composite parent, int style) {
		super(parent, style);
		quantitationListTableComparator = new QuantitationListTableComparator();
		createColumns();
	}

	public void clear() {

		setInput(null);
	}

	public void sortTable() {

	}

	private void createColumns() {

		createColumns(QuantitationListLabelProvider.TITLES, QuantitationListLabelProvider.BOUNDS);
		setLabelProvider(new QuantitationListLabelProvider());
		setContentProvider(new QuantitationListContentProvider());
		setComparator(quantitationListTableComparator);
		setEditingSupport();
	}

	private void setEditingSupport() {

	}
}
