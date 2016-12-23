/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
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

import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IAreaSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IIntegrationSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.PeakIntegrationSettings;

public class PeakIntegrationSettingsSupport {

	public IPeakIntegrationSettings getPeakIntegrationSettings() {

		IPeakIntegrationSettings peakIntegrationSettings = new PeakIntegrationSettings();
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
		Set<Integer> ions = PreferenceSupplier.getIons(PreferenceSupplier.P_SELECTED_IONS, PreferenceSupplier.DEF_SELECTED_IONS);
		PreferenceSupplier.setMarkedIons(selectedIons, ions);
		return peakIntegrationSettings;
	}
}
