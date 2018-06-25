/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ChromatogramSelectionException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ClassifierSettingsException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.exceptions.ClassifierException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support.Calculator;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.WncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result.IChromatogramResultWNC;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result.IWncClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result.WncClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings.IWncClassifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class Classifier extends AbstractChromatogramClassifier {

	private static final Logger logger = Logger.getLogger(Classifier.class);
	private IWncIons wncIons;

	@Override
	public IProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		try {
			validate(chromatogramSelection, chromatogramClassifierSettings);
			setClassifierSettings(chromatogramClassifierSettings);
			/*
			 * Try to classify the chromatogram selection.
			 */
			try {
				Calculator calculator = new Calculator();
				IWncIons resultWncIons = calculator.calculateIonPercentages(chromatogramSelection, wncIons);
				IWncClassifierResult chromatogramClassifierResult = new WncClassifierResult(ResultStatus.OK, "The chromatogram has been classified.", resultWncIons);
				//
				IMeasurementResult measurementResult = new MeasurementResult(IChromatogramResultWNC.NAME, IChromatogramResultWNC.IDENTIFIER, "This is percentage area list of selected ions.", wncIons);
				chromatogramSelection.getChromatogram().addMeasurementResult(measurementResult);
				//
				processingInfo.setProcessingResult(chromatogramClassifierResult);
			} catch(ClassifierException e) {
				IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, IChromatogramResultWNC.NAME, "The chromatogram couldn't be classified.");
				processingInfo.addMessage(processingMessage);
			}
		} catch(ChromatogramSelectionException e1) {
			logger.warn(e1);
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, IChromatogramResultWNC.NAME, "The chromatogram selection is invalid.");
			processingInfo.addMessage(processingMessage);
		} catch(ClassifierSettingsException e1) {
			logger.warn(e1);
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, IChromatogramResultWNC.NAME, "The settings are invalid.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IChromatogramClassifierSettings chromatogramClassifierSettings = PreferenceSupplier.getChromatogramClassifierSettings();
		return applyClassifier(chromatogramSelection, chromatogramClassifierSettings, monitor);
	}

	// ----------------------------private methods
	private void setClassifierSettings(IChromatogramClassifierSettings chromatogramClassifierSettings) {

		if(chromatogramClassifierSettings instanceof IWncClassifierSettings) {
			IWncClassifierSettings settings = (IWncClassifierSettings)chromatogramClassifierSettings;
			wncIons = settings.getWNCIons();
		} else {
			wncIons = new WncIons();
		}
	}
}
