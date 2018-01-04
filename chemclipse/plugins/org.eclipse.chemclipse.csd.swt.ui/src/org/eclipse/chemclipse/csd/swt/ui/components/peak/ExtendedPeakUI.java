/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.swt.ui.components.peak;

import org.eclipse.swt.widgets.Composite;

/**
 * Shows the peak without the background.
 * 
 * @author eselmeister
 */
public class ExtendedPeakUI extends AbstractViewPeakUI {

	public ExtendedPeakUI(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void setViewSeries() {

		if(peak != null) {
			super.setViewSeries();
		}
	}
}
