/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ChromatogramSelectionException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.ClassifierSettingsException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.ChromatogramClassifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.classifier.processing.IChromatogramClassifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.processor.DurbinWatsonProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.DurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IChromatogramResultDurbinWatson;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IDurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.settings.IDurbinWatsonClassifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramResult;
import org.eclipse.chemclipse.model.implementation.ChromatogramResult;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public class Classifier extends AbstractChromatogramClassifier {

	private static final Logger logger = Logger.getLogger(Classifier.class);

	@Override
	public IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		IChromatogramClassifierProcessingInfo processingInfo = new ChromatogramClassifierProcessingInfo();
		try {
			validate(chromatogramSelection, chromatogramClassifierSettings);
			IDurbinWatsonClassifierSettings classifierSettings;
			if(chromatogramClassifierSettings instanceof IDurbinWatsonClassifierSettings) {
				classifierSettings = (IDurbinWatsonClassifierSettings)chromatogramClassifierSettings;
			} else {
				classifierSettings = PreferenceSupplier.getSettings();
			}
			//
			DurbinWatsonProcessor durbinWatsonProcessor = new DurbinWatsonProcessor();
			IDurbinWatsonClassifierResult durbinWatsonClassifierResult = new DurbinWatsonClassifierResult(ResultStatus.OK, "The chromatogram has been classified.");
			durbinWatsonProcessor.run(chromatogramSelection, classifierSettings, durbinWatsonClassifierResult, monitor);
			//
			IChromatogramResult chromatogramResult = new ChromatogramResult(IChromatogramResultDurbinWatson.IDENTIFIER, "This is the Durbin-Watson classifier result.", durbinWatsonClassifierResult);
			chromatogramSelection.getChromatogram().addChromatogramResult(chromatogramResult);
			//
			processingInfo.setChromatogramClassifierResult(durbinWatsonClassifierResult);
		} catch(ChromatogramSelectionException e1) {
			logger.warn(e1);
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Durbin-Watson Classifier", "The chromatogram selection is invalid.");
			processingInfo.addMessage(processingMessage);
		} catch(ClassifierSettingsException e1) {
			logger.warn(e1);
			IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Durbin-Watson Classifier", "The settings are invalid.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}

	@Override
	public IChromatogramClassifierProcessingInfo applyClassifier(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		IDurbinWatsonClassifierSettings chromatogramClassifierSettings = PreferenceSupplier.getSettings();
		return applyClassifier(chromatogramSelection, chromatogramClassifierSettings, monitor);
	}
}
