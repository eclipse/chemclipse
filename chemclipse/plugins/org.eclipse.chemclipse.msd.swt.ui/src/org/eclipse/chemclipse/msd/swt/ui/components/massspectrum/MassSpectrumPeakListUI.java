/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - increase performance for large spectra
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumPeakLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumPeakListUI extends ExtendedTableViewer {

	private String[] titles = {"m/z", "abundance", "s/n"};
	private int[] bounds = {160, 200, 10};

	public MassSpectrumPeakListUI(Composite parent) {

		super(parent);
		createColumns();
	}

	public MassSpectrumPeakListUI(Composite parent, int style) {

		super(parent, style);
		createColumns();
	}

	public void update(IStandaloneMassSpectrum standaloneMassSpectrum) {

		if(standaloneMassSpectrum != null) {
			setContentProviders();
			super.setInput(standaloneMassSpectrum.getPeaks());
			setItemCount(standaloneMassSpectrum.getPeaks().size());
		} else {
			super.setInput(null);
		}
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		setLabelProvider();
		setContentProviders();
	}

	private void setLabelProvider() {

		setLabelProvider(new MassSpectrumPeakLabelProvider());
	}

	private void setContentProviders() {

		setContentProvider(new ListContentProvider());
	}
}
