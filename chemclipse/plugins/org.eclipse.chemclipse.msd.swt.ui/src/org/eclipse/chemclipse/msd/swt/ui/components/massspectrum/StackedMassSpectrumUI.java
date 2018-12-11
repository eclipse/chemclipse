/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.Range;

public class StackedMassSpectrumUI extends SimpleMassSpectrumUI {

	private StackedMassSpectrumUI otherStackedMassSpectrumUI;

	public StackedMassSpectrumUI(Composite parent, int style, MassValueDisplayPrecision massSpectrumType) {
		super(parent, style, massSpectrumType);
		setShowAxis(SWT.TOP, false);
		setShowAxis(SWT.RIGHT, false);
	}

	public void setOtherStackedMassSpectrumUI(StackedMassSpectrumUI otherStackedMassSpectrumUI) {

		this.otherStackedMassSpectrumUI = otherStackedMassSpectrumUI;
	}

	@Override
	public void mouseUp(MouseEvent e) {

		super.mouseUp(e);
		//
		if(otherStackedMassSpectrumUI != null) {
			otherStackedMassSpectrumUI.setRange(getRange());
		}
	}

	@Override
	public void mouseScrolled(MouseEvent e) {

		if(otherStackedMassSpectrumUI != null) {
			otherStackedMassSpectrumUI.setRange(getRange());
		}
	}

	@Override
	public void adjustRange() {

		Range fixedAxisRangeX = getFixedAxisRangeX();
		setRange(fixedAxisRangeX);
		if(otherStackedMassSpectrumUI != null) {
			otherStackedMassSpectrumUI.setRange(fixedAxisRangeX);
		}
	}

	@Override
	public void adjustXRange() {

		adjustRange();
	}

	@Override
	public void adjustYRange() {

		adjustRange();
	}

	@Override
	public void adjustPreviousRange() {

		adjustRange();
	}
}
