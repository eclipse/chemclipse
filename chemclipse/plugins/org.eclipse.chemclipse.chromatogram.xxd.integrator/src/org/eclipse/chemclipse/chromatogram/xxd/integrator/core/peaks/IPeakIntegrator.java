/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.IPeakIntegratorProcessingInfo;

public interface IPeakIntegrator {

	IPeakIntegratorProcessingInfo integrate(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor);

	IPeakIntegratorProcessingInfo integrate(IPeak peak, IProgressMonitor monitor);

	IPeakIntegratorProcessingInfo integrate(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor);

	IPeakIntegratorProcessingInfo integrate(List<? extends IPeak> peaks, IProgressMonitor monitor);

	IPeakIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor);

	IPeakIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor);
}
