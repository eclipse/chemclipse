/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.support;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;

public interface ITrapezoidPeakIntegratorSupport {

	IPeakIntegrationResult calculatePeakIntegrationResult(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException;

	IPeakIntegrationResults calculatePeakIntegrationResults(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException;

	IPeakIntegrationResults calculatePeakIntegrationResults(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException;
}
