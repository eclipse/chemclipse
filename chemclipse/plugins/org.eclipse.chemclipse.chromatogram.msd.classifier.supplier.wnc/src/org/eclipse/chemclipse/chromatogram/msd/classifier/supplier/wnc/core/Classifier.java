/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ChromatogramSelectionException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ClassifierSettingsException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.ChromatogramClassifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.IChromatogramClassifierProcessingInfo;
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
import org.eclipse.chemclipse.model.core.IChromatogramResult;
import org.eclipse.chemclipse.model.implementation.ChromatogramResult;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public class Classifier extends AbstractChromatogramClassifier {

	private static final Logger logger = Logger.getLogger(Classifier.class);
	private IWncIons wncIons;

	@Override
	public IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		IChromatogramClassifierProcessingInfo processingInfo = new ChromatogramClassifierProcessingInfo();
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
				IChromatogramResult chromatogramResult = new ChromatogramResult(IChromatogramResultWNC.IDENTIFIER, "This is percentage area list of selected ions.", wncIons);
				chromatogramSelection.getChromatogram().addChromatogramResult(chromatogramResult);
				//
				processingInfo.setChromatogramClassifierResult(chromatogramClassifierResult);
			} catch(ClassifierException e) {
				IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "WNC Classifier", "The chromatogram couldn't be classified.");
				processingInfo.addMessage(processingMessage);
			}
		} catch(ChromatogramSelectionException e1) {
			logger.warn(e1);
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "WNC Classifier", "The chromatogram selection is invalid.");
			processingInfo.addMessage(processingMessage);
		} catch(ClassifierSettingsException e1) {
			logger.warn(e1);
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "WNC Classifier", "The settings are invalid.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}

	@Override
	public IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

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
