/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.IonListTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumIonsListUI extends ExtendedTableViewer {

	private String[] titles = {"m/z", "abundance", "parent m/z", "parent resolution", "daughter m/z", "daughter resolution", "collision energy"};
	private int bounds[] = {100, 100, 120, 120, 120, 120, 120};

	public MassSpectrumIonsListUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public MassSpectrumIonsListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void update(IScanMSD massSpectrum, boolean forceReload) {

		if(massSpectrum != null) {
			setInput(massSpectrum);
		}
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new IonListLabelProvider());
		setContentProvider(new IonListContentProvider());
		setComparator(new IonListTableComparator());
		// TODO add a filter to display a subset of m/z values only
		setEditingSupport();
	}

	private void setEditingSupport() {

		// TODO implement
	}
}
