/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AbstractPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.ISettingStatus;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

/**
 * THIS IS A TEST CLASS! DO NOT USE EXCEPT FOR TESTS!
 */
public class PeakIntegrationSettings extends AbstractPeakIntegrationSettings implements IIntegrationSettings {

	@Override
	public ISettingStatus getSettingStatus(IPeak peak) {

		int startRetentionTime = 0;
		if(peak instanceof IPeakMSD) {
			startRetentionTime = ((IPeakMSD)peak).getPeakModel().getStartRetentionTime();
		} else if(peak instanceof IPeakCSD) {
			startRetentionTime = ((IPeakCSD)peak).getPeakModel().getStartRetentionTime();
		}
		boolean report = report(peak);
		boolean sumOn = getAreaSupport().isAreaSumOn(startRetentionTime);
		ISettingStatus firstDerivativeSettingStatus = new SettingStatus(report, sumOn);
		return firstDerivativeSettingStatus;
	}
}