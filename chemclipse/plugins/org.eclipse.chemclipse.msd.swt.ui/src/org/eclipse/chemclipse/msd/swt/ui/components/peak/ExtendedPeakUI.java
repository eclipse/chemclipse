/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import org.eclipse.chemclipse.swt.ui.support.IAxisTitles;
import org.eclipse.swt.widgets.Composite;

/**
 * Shows the peak without the background.
 * 
 * @author eselmeister
 */
public class ExtendedPeakUI extends AbstractViewPeakUI {

	public ExtendedPeakUI(Composite parent, int style, IAxisTitles axisTitles) {
		super(parent, style, axisTitles);
		setSuppressHairlineDivider(true);
	}

	@Override
	public void setViewSeries() {

		if(peak != null) {
			super.setViewSeries();
		}
	}
}
