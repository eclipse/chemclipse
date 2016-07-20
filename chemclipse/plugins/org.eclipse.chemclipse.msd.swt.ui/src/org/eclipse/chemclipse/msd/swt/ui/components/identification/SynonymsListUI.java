/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.identification;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.SynonymsListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.SynonymsListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.SynonymsListTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.widgets.Composite;

public class SynonymsListUI extends ExtendedTableViewer {

	private String[] titles = {"Synonym"};
	private int bounds[] = {300};

	public SynonymsListUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public SynonymsListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new SynonymsListLabelProvider());
		setContentProvider(new SynonymsListContentProvider());
		setComparator(new SynonymsListTableComparator());
	}

	public void update(ILibraryInformation libraryInformation, boolean forceReload) {

		if(libraryInformation != null) {
			setInput(libraryInformation.getSynonyms());
		}
	}
}
