/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ExtendedMassSpectrumIonsListUI extends Composite {

	private MassSpectrumIonsListUI massSpectrumIonsListUI;

	public ExtendedMassSpectrumIonsListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IScanMSD scanMSD) {

		massSpectrumIonsListUI.update(scanMSD, true);
	}

	private void createControl() {

		setLayout(new FillLayout());
		massSpectrumIonsListUI = new MassSpectrumIonsListUI(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}
}
