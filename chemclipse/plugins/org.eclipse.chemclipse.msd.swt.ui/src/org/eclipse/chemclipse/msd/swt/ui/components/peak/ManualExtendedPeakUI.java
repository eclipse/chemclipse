/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public class ManualExtendedPeakUI extends AbstractChromatogramViewPeakUI {

	/**
	 * @param parent
	 * @param style
	 */
	public ManualExtendedPeakUI(Composite parent, int style) {

		super(parent, style);
	}

	/**
	 * Sets the peak to show.
	 * 
	 * @param IChromatogramPeakMSD
	 */
	public void setPeakAndSelection(IChromatogramPeakMSD peak, IChromatogramSelectionMSD chromatogramSelection) {

		super.setPeak(peak);
		updateSelection(chromatogramSelection, true);
	}
}
