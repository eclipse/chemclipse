/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.TargetsTableComparator;
import org.eclipse.swt.widgets.Composite;

public class TargetsListUI extends ExtendedTableViewer {

	private static final String VERIFIED_MANUALLY = "Verified (manually)";
	//
	private static final String[] TITLES = { //
			VERIFIED_MANUALLY, //
			"Rating", //
			"Name", //
			"CAS", //
			"Match Factor", //
			"Reverse Factor", //
			"Match Factor Direct", //
			"Reverse Factor Direct", //
			"Probability", //
			"Formula", //
			"SMILES", //
			"InChI", //
			"Mol Weight", //
			"Advise", //
			"Identifier", //
			"Miscellaneous", //
			"Comments", //
			"Database", //
			"Contributor", //
			"Reference ID"//
	};
	private static final int[] BOUNDS = { //
			30, //
			30, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100 //
	};

	public TargetsListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		//
		setLabelProvider(new TargetsLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new TargetsTableComparator());
	}
}
