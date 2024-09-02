/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IVendorStandaloneMassSpectrum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ExtendedMassSpectrumPeakListUI extends Composite {

	private MassSpectrumPeakListUI massSpectrumPeaksListUI;

	public ExtendedMassSpectrumPeakListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IVendorStandaloneMassSpectrum standaloneMassSpectrum) {

		massSpectrumPeaksListUI.update(standaloneMassSpectrum);
	}

	private void createControl() {

		setLayout(new FillLayout());
		massSpectrumPeaksListUI = new MassSpectrumPeakListUI(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);
	}
}
