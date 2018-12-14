/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.swt;

import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.internal.provider.IdentifierListLabelProvider;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.ui.internal.provider.IdentifierListTableComparator;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.widgets.Composite;

public class IdentifierFileListUI extends ExtendedTableViewer {

	public IdentifierFileListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(IdentifierListLabelProvider.TITLES, IdentifierListLabelProvider.BOUNDS);
		setLabelProvider(new IdentifierListLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new IdentifierListTableComparator());
	}
}
