/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.messages;

public interface IFirstDerivativeMessages {

	/*
	 * The strings need to be listed and translated in:
	 * OSGI-INF/l10n/bundle.properties ...
	 */
	String FIRST_DERIVATIVE = "FirstDerivative";
	String FIRST_DERIVATIVE_DESCRIPTON = "FirstDerivativeDescription";
	String THRESHOLD = "Threshold";
	String INCLUDE_BACKGROUND = "IncludeBackground";
	String BACKGROUND_DESCRIPTION = "IncludeBackgroundDescription";
	String MIN_SN_RATIO = "MinSignalToNoiseRatio";
	String WINDOW_SIZE = "WindowSize";
	String WINDOW_SIZE_DESCRIPTION = "WindowSizeDescription";
	String USE_NOISE_SEGMENTS = "UseNoiseSegments";
	String USE_NOISE_SEGMENTS_DESCRIPTION = "UseNoiseSegmentsDescription";
	String FILTER_MODE = "FilterMode";
	String FILTER_MASSES = "FilterMasses";
	String USE_INDIVIDUAL_TRACES = "UseIndividualTraces";
	String USE_INDIVIDUAL_TRACES_DESCRIPTION = "UseIndividualTracesDescription";
	String OPTIMIZE_BASELINE_VV = "OptimizeBaselineVV";
	String PEAKS_DETECTED = "PeaksDetected";
	String FILTER_WAVELENGTHS = "FilterWavelengths";
	String USE_INDIVIDUAL_WAVELENGTHS = "UseIndividualWavelengths";
	String USE_INDIVIDUAL_WAVELENGTHS_DESCRIPTION = "UseIndividualWavelengthsDescription";
}
