/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.exceptions.ClassifierException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support.Calculator;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result.IWncClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result.WncClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings.ClassifierSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class Classifier extends AbstractChromatogramClassifier {

	public static final String IDENTIFIER = "org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc";

	public Classifier() {

		super(DataType.MSD);
	}

	@Override
	public IProcessingInfo<IChromatogramClassifierResult> applyClassifier(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		ClassifierSettings classifierSettings;
		if(chromatogramClassifierSettings instanceof ClassifierSettings settings) {
			classifierSettings = settings;
		} else {
			classifierSettings = PreferenceSupplier.getClassifierSettings();
		}
		IProcessingInfo<IChromatogramClassifierResult> processingInfo = validate(chromatogramSelection, chromatogramClassifierSettings);
		if(!processingInfo.hasErrorMessages()) {
			try {
				IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
				if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
					IWncIons resultWncIons = Calculator.calculateIonPercentages(chromatogramMSD, chromatogramSelection, classifierSettings);
					IWncClassifierResult chromatogramClassifierResult = new WncClassifierResult(ResultStatus.OK, Messages.chromatogramClassified, resultWncIons);
					//
					IMeasurementResult<?> measurementResult = new MeasurementResult(Messages.wncClassifier, IDENTIFIER, Messages.percentageAreaOfSelectedIons, chromatogramClassifierResult);
					chromatogramSelection.getChromatogram().addMeasurementResult(measurementResult);
					//
					processingInfo.setProcessingResult(chromatogramClassifierResult);
				} else {
					processingInfo.addWarnMessage(Messages.wncClassifier, Messages.msdChromatogramsOnly);
				}
			} catch(ClassifierException e) {
				IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, Messages.wncClassifier, Messages.chromatogramNotClassified);
				processingInfo.addMessage(processingMessage);
			}
		}
		return processingInfo;
	}
}
