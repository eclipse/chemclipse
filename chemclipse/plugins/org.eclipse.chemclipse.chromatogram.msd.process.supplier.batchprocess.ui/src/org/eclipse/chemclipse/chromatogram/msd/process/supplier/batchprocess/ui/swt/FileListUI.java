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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.provider.FileListLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.widgets.Composite;

public class FileListUI extends ExtendedTableViewer {

	public FileListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(FileListLabelProvider.TITLES, FileListLabelProvider.BOUNDS);
		setLabelProvider(new FileListLabelProvider());
		setContentProvider(new ListContentProvider());
	}
}
