/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.swt;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class ManualExtendedPeakUI extends AbstractChromatogramViewPeakUI {

	public ManualExtendedPeakUI(Composite parent, int style) {
		super(parent, style);
	}

	public void setPeakAndSelection(IPeak peak, IChromatogramSelection chromatogramSelection) {

		super.setPeak(peak);
		updateSelection(chromatogramSelection, true);
	}
}
