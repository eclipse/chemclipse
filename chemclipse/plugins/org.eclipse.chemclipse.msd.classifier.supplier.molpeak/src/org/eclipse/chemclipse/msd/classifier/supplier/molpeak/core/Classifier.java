/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.classifier.BasePeakClassifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.ILigninRatios;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.results.BasePeakClassifierResult;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.results.IBasePeakClassifierResult;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.results.IChromatogramResultBasePeak;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.ClassifierSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class Classifier extends AbstractChromatogramClassifier {

	@Override
	public IProcessingInfo<IChromatogramClassifierResult> applyClassifier(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		ClassifierSettings classifierSettings;
		if(chromatogramClassifierSettings instanceof ClassifierSettings) {
			classifierSettings = (ClassifierSettings)chromatogramClassifierSettings;
		} else {
			classifierSettings = PreferenceSupplier.getChromatogramClassifierSettings();
		}
		IProcessingInfo<IChromatogramClassifierResult> processingInfo = validate(chromatogramSelection, classifierSettings);
		if(!processingInfo.hasErrorMessages()) {
			ILigninRatios ligninRatios = BasePeakClassifier.calculateLigninRatios(chromatogramSelection);
			IBasePeakClassifierResult chromatogramClassifierResult = new BasePeakClassifierResult(ResultStatus.OK, "The chromatogram has been classified.", ligninRatios);
			IMeasurementResult measurementResult = new MeasurementResult(IChromatogramResultBasePeak.NAME, IChromatogramResultBasePeak.IDENTIFIER, "This is ratio of lignins calculated by the base peak.", ligninRatios);
			chromatogramSelection.getChromatogram().addMeasurementResult(measurementResult);
			processingInfo.setProcessingResult(chromatogramClassifierResult);
		}
		return processingInfo;
	}
}
