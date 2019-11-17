/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractChromatogramClassifier implements IChromatogramClassifier {

	private static final String CLASSIFIER = "Classifier";

	public IProcessingInfo<IChromatogramClassifierResult> validate(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings) {

		IProcessingInfo<IChromatogramClassifierResult> processingInfo = new ProcessingInfo<>();
		validateChromatogramSelection(chromatogramSelection, processingInfo);
		return processingInfo;
	}

	private void validateChromatogramSelection(IChromatogramSelection<?, ?> chromatogramSelection, IProcessingInfo<?> processingInfo) {

		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(CLASSIFIER, "The chromatogram selection must not be null.");
		}
		if(chromatogramSelection.getChromatogram() == null) {
			processingInfo.addErrorMessage(CLASSIFIER, "The chromatogram must not be null.");
		}
	}
}
