/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IBaselineDetector {

	/**
	 * All classes which implement this interface are responsible to set a
	 * baseline to the chromatogram.<br/>
	 * How they will calculate the baseline (automatically, manually) is their
	 * on scope.<br/>
	 * Use {@link IChromatogramMSD} and {@link IBaselineModel} which can be
	 * retrieved from the chromatogram instance.
	 * 
	 * @param chromatogram
	 * @param baselineDetectorSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<?> setBaseline(IChromatogramSelection<?, ?> chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor);

	/**
	 * This class does the same as the other setBaseline method but does not require settings.<br/>
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<?> setBaseline(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor);

	/**
	 * Validates the parameters.
	 * 
	 * @param chromatogramSelection
	 * @param baselineDetectorSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<?> validate(IChromatogramSelection<?, ?> chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor);

	/**
	 * Validates the parameters.
	 * 
	 * @param baselineDetectorSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<?> validate(IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor);

	/**
	 * Validates the parameters.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<?> validate(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor);
}
