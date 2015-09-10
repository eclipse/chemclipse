/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.swt;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.FilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CombinedMassSpectrumUI extends Composite {

	private SimpleMassSpectrumUI simpleMassSpectrumUI;

	public CombinedMassSpectrumUI(Composite parent, int style) {

		super(parent, style);
		initialize(parent);
	}

	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout;
		GridData gridData;
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		// composite.setBackground(new Color(SWT.COLOR_RED));
		// ------------------------------------------------------------------------------------------
		// Mass Spectrum
		simpleMassSpectrumUI = new SimpleMassSpectrumUI(composite, SWT.NONE, MassValueDisplayPrecision.NOMINAL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		simpleMassSpectrumUI.setLayoutData(gridData);
	}

	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		if(chromatogramSelection != null) {
			boolean useNormalize = PreferenceSupplier.isUseNormalize();
			IScanMSD massSpectrum = FilterSupport.getCombinedMassSpectrum(chromatogramSelection, null, useNormalize);
			simpleMassSpectrumUI.update(massSpectrum, forceReload);
		}
	}
}
