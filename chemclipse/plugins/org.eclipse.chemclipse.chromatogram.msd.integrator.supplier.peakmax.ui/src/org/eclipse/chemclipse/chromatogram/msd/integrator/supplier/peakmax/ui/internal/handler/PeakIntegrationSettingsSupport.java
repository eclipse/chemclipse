/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.ui.internal.handler;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IAreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IIntegrationSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.support.util.IonSettingUtil;

public class PeakIntegrationSettingsSupport {

	public IPeakIntegrationSettings getPeakIntegrationSettings() {

		PeakIntegrationSettings peakIntegrationSettings = new PeakIntegrationSettings();
		/*
		 * Area Support
		 */
		IAreaSupport areaSupport = peakIntegrationSettings.getAreaSupport();
		areaSupport.setMinimumArea(PreferenceSupplier.getMinimumPeakArea()); // int but double should be used.
		/*
		 * Integration Support
		 */
		IIntegrationSupport integrationSupport = peakIntegrationSettings.getIntegrationSupport();
		integrationSupport.setMinimumPeakWidth(PreferenceSupplier.getMinimumPeakWidth());
		integrationSupport.setMinimumSignalToNoiseRatio(PreferenceSupplier.getMinimumSignalToNoiseRatio()); // int but float should be used.
		IMarkedIons selectedIons = peakIntegrationSettings.getSelectedIons();
		String ions = PreferenceSupplier.getIons(PreferenceSupplier.P_SELECTED_IONS, PreferenceSupplier.DEF_SELECTED_IONS);
		IonSettingUtil settingIon = new IonSettingUtil();
		selectedIons.add(settingIon.extractIons(settingIon.deserialize(ions)));
		return peakIntegrationSettings;
	}
}
