/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.IChromatogramCalculatorSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * A chromatogram calculator is used for example to calculate retention indices (RI) for
 * scans and/or peaks. It extends the chromatographic data. We first thought
 * to put this functionality under the category of filters. But people hesitate to use it,
 * cause filtering normally means to remove things. That's why an own category has been created.
 *
 */
public interface IChromatogramCalculator {

	/**
	 * Apply the calculator in the given chromatogram selection and take care of the
	 * calculator settings.<br/>
	 * Return an {@link IChromatogramCalculatorProcessingInfo} instance.<br/>
	 * If there is no monitor instance, use a {@link NullProgressMonitor}.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramCalculatorSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	@SuppressWarnings("rawtypes")
	IProcessingInfo applyCalculator(IChromatogramSelection chromatogramSelection, IChromatogramCalculatorSettings chromatogramCalculatorSettings, IProgressMonitor monitor);

	/**
	 * Apply the calculator in the given chromatogram selection.
	 * Return an {@link IProcessingInfo} instance.<br/>
	 * If there is no monitor instance, use a {@link NullProgressMonitor}.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	@SuppressWarnings("rawtypes")
	IProcessingInfo applyCalculator(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor);

	/**
	 * Validates the selection and settings and returns a process info instance.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramCalculatorSettings
	 * @return {@link IProcessingInfo}
	 */
	@SuppressWarnings("rawtypes")
	IProcessingInfo validate(IChromatogramSelection chromatogramSelection, IChromatogramCalculatorSettings chromatogramCalculatorSettings);
}
