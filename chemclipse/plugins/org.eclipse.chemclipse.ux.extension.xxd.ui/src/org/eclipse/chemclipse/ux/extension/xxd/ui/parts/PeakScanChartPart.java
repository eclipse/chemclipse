/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.AbstractPeakUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.IPeakUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanChartUI;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class PeakScanChartPart extends AbstractPeakUpdateSupport implements IPeakUpdateSupport {

	private ExtendedScanChartUI extendedScanChartUI;

	@Inject
	public PeakScanChartPart(Composite parent, MPart part) {
		super(part);
		extendedScanChartUI = new ExtendedScanChartUI(parent, part);
	}

	@Focus
	public void setFocus() {

		updatePeak(getPeak());
	}

	@Override
	public void updatePeak(IPeak peak) {

		IScan scan = null;
		if(peak != null) {
			if(peak instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)peak;
				scan = peakMSD.getPeakModel().getPeakMaximum();
			} else if(peak instanceof IPeakCSD) {
				IPeakCSD peakCSD = (IPeakCSD)peak;
				scan = peakCSD.getPeakModel().getPeakMaximum();
			} else if(peak instanceof IPeakWSD) {
				IPeakWSD peakWSD = (IPeakWSD)peak;
				scan = peakWSD.getPeakModel().getPeakMaximum();
			}
		}
		extendedScanChartUI.update(scan);
	}
}
