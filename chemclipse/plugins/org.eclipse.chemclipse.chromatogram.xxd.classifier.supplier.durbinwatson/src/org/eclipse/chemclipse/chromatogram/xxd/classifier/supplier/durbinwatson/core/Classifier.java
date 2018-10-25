/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.processor.DurbinWatsonProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.DurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IChromatogramResultDurbinWatson;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IDurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.settings.ClassifierSettings;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class Classifier extends AbstractChromatogramClassifier {

	@Override
	public IProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = validate(chromatogramSelection, chromatogramClassifierSettings);
		if(!processingInfo.hasErrorMessages()) {
			if(chromatogramClassifierSettings instanceof ClassifierSettings) {
				DurbinWatsonProcessor durbinWatsonProcessor = new DurbinWatsonProcessor();
				IDurbinWatsonClassifierResult durbinWatsonClassifierResult = new DurbinWatsonClassifierResult(ResultStatus.OK, "The chromatogram has been classified.");
				durbinWatsonProcessor.run(chromatogramSelection, (ClassifierSettings)chromatogramClassifierSettings, durbinWatsonClassifierResult, monitor);
				IMeasurementResult measurementResult = new MeasurementResult(IChromatogramResultDurbinWatson.NAME, IChromatogramResultDurbinWatson.IDENTIFIER, "This is the Durbin-Watson classifier result.", durbinWatsonClassifierResult);
				chromatogramSelection.getChromatogram().addMeasurementResult(measurementResult);
				processingInfo.setProcessingResult(durbinWatsonClassifierResult);
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		ClassifierSettings classifierSettings = PreferenceSupplier.getSettings();
		return applyClassifier(chromatogramSelection, classifierSettings, monitor);
	}
}
