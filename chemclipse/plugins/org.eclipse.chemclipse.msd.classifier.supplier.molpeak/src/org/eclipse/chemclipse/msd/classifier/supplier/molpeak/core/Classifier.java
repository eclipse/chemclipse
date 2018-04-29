/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ChromatogramSelectionException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ClassifierSettingsException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.ChromatogramClassifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.IChromatogramClassifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.classifier.BasePeakClassifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.ILigninRatios;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.results.BasePeakClassifierResult;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.results.IBasePeakClassifierResult;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.results.IChromatogramResultBasePeak;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class Classifier extends AbstractChromatogramClassifier {

	private static final Logger logger = Logger.getLogger(Classifier.class);

	@Override
	public IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		IChromatogramClassifierProcessingInfo processingInfo = new ChromatogramClassifierProcessingInfo();
		try {
			validate(chromatogramSelection, chromatogramClassifierSettings);
			BasePeakClassifier basePeakClassifier = new BasePeakClassifier();
			ILigninRatios ligninRatios = basePeakClassifier.calculateLigninRatios(chromatogramSelection);
			IBasePeakClassifierResult chromatogramClassifierResult = new BasePeakClassifierResult(ResultStatus.OK, "The chromatogram has been classified.", ligninRatios);
			IMeasurementResult measurementResult = new MeasurementResult(IChromatogramResultBasePeak.NAME, IChromatogramResultBasePeak.IDENTIFIER, "This is ratio of lignins calculated by the base peak.", ligninRatios);
			chromatogramSelection.getChromatogram().addMeasurementResult(measurementResult);
			processingInfo.setChromatogramClassifierResult(chromatogramClassifierResult);
		} catch(ChromatogramSelectionException e) {
			logger.warn(e);
			processingInfo.addErrorMessage(IChromatogramResultBasePeak.NAME, "The chromatogram selection was wrong.");
		} catch(ClassifierSettingsException e) {
			logger.warn(e);
			processingInfo.addErrorMessage(IChromatogramResultBasePeak.NAME, "Settings were wrong.");
		}
		return processingInfo;
	}

	@Override
	public IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramClassifierSettings chromatogramClassifierSettings = PreferenceSupplier.getChromatogramClassifierSettings();
		return applyClassifier(chromatogramSelection, chromatogramClassifierSettings, monitor);
	}
}
