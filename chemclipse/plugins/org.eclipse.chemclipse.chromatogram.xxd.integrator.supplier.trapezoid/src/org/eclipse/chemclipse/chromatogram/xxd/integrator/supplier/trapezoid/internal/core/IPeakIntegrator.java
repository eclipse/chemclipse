/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.ValueMustNotBeNullException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakIntegrator {

	/*
	 * The FirstDerivative seems to use a correction factor.
	 * Otherwise, the peak areas are too high.
	 */
	double CORRECTION_FACTOR_TRAPEZOID = 100.0d; // ChemStation Factor
	//
	String DESCRIPTION = "Integrator Trapezoid";
	String XIC = "XIC =";
	String TIC = "TIC";

	/**
	 * Returns a integration result object.<br/>
	 * May also return null.
	 * 
	 * @param peak
	 * @param integrationSettings
	 * @return {@link TrapezoidPeakIntegrationResult}
	 */
	IPeakIntegrationResult integrate(IPeak peak, PeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException;

	/**
	 * Integrates a list of peaks and returns a results object.
	 * 
	 * @param peaks
	 * @param integrationSettings
	 * @return {@link TrapezoidPeakIntegrationResults}
	 */
	IPeakIntegrationResults integrate(List<? extends IPeak> peaks, PeakIntegrationSettings peakIntegrationSettings, IProgressMonitor monitor) throws ValueMustNotBeNullException;
}
