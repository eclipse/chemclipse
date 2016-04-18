/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.chromatogram.xxd.calculator.processing.ICalculatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.IChromatogramCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ISupplierCalculatorSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramCalculator extends AbstractChromatogramCalculator {

	@Override
	public ICalculatorProcessingInfo applyCalculator(IChromatogramSelection chromatogramSelection, IChromatogramCalculatorSettings chromatogramCalculatorSettings, IProgressMonitor monitor) {

		ISupplierCalculatorSettings supplierSettings;
		if(chromatogramCalculatorSettings instanceof IChromatogramCalculatorSettings) {
			supplierSettings = (ISupplierCalculatorSettings)chromatogramCalculatorSettings;
		} else {
			supplierSettings = PreferenceSupplier.getChromatogramCalculatorSettings();
		}
		//
		RetentionIndexCalculator calculator = new RetentionIndexCalculator();
		return calculator.apply(chromatogramSelection, supplierSettings, monitor);
	}

	@Override
	public ICalculatorProcessingInfo applyCalculator(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		/*
		 * The settings are fetched dynamically.
		 */
		ISupplierCalculatorSettings chromatogramCalculatorSettings = PreferenceSupplier.getChromatogramCalculatorSettings();
		return applyCalculator(chromatogramSelection, chromatogramCalculatorSettings, monitor);
	}
}
