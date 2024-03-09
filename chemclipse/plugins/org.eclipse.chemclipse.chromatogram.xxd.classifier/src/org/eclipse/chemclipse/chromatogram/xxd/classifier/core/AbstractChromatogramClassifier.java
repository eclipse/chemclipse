/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
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
package org.eclipse.chemclipse.chromatogram.xxd.classifier.core;

import org.eclipse.chemclipse.chromatogram.xxd.classifier.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractChromatogramClassifier implements IChromatogramClassifier {

	private static final String CLASSIFIER = Messages.classifier;
	private final DataType[] dataTypes;

	protected AbstractChromatogramClassifier(DataType... dataTypes) {

		this.dataTypes = dataTypes;
	}

	public IProcessingInfo<IChromatogramClassifierResult> validate(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings) {

		IProcessingInfo<IChromatogramClassifierResult> processingInfo = new ProcessingInfo<>();
		validateChromatogramSelection(chromatogramSelection, processingInfo);
		return processingInfo;
	}

	private void validateChromatogramSelection(IChromatogramSelection<?, ?> chromatogramSelection, IProcessingInfo<?> processingInfo) {

		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(CLASSIFIER, Messages.chromatogramSelectionNull);
		} else if(chromatogramSelection.getChromatogram() == null) {
			processingInfo.addErrorMessage(CLASSIFIER, Messages.chromatogramNull);
		}
	}

	@Override
	public DataType[] getDataTypes() {

		return dataTypes.clone();
	}
}
