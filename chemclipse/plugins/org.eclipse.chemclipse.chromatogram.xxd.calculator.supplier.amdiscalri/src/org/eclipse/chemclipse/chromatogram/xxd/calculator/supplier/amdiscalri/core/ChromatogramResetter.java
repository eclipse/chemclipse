/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.AbstractChromatogramCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.IChromatogramCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ResetterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramResetter extends AbstractChromatogramCalculator {

	@Override
	public IProcessingInfo<?> applyCalculator(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramCalculatorSettings chromatogramCalculatorSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		//
		if(chromatogramCalculatorSettings instanceof ResetterSettings resetterSettings) {
			RetentionIndexCalculator calculator = new RetentionIndexCalculator();
			IProcessingInfo<?> calculatorInfo = calculator.resetIndices(chromatogramSelection, resetterSettings);
			chromatogramSelection.getChromatogram().setDirty(true);
			processingInfo.addMessages(calculatorInfo);
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> applyCalculator(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		ResetterSettings resetterSettings = PreferenceSupplier.getChromatogramResetterSettings();
		return applyCalculator(chromatogramSelection, resetterSettings, monitor);
	}
}
