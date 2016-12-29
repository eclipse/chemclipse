/*******************************************************************************
 * Copyright (c) 2014, 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public class PeakIntegrationSettings extends AbstractPeakIntegrationSettings implements IPeakIntegrationSettings {

	@Override
	public ISettingStatus getSettingStatus(IPeak peak) {

		boolean report = super.report(peak);
		int startRetentionTime = 0;
		if(peak instanceof IPeakMSD) {
			startRetentionTime = ((IPeakMSD)peak).getPeakModel().getStartRetentionTime();
		} else if(peak instanceof IPeakCSD) {
			startRetentionTime = ((IPeakCSD)peak).getPeakModel().getStartRetentionTime();
		}
		//
		boolean sumOn = super.getAreaSupport().isAreaSumOn(startRetentionTime);
		ISettingStatus status = new SettingStatus(report, sumOn);
		return status;
	}
}
