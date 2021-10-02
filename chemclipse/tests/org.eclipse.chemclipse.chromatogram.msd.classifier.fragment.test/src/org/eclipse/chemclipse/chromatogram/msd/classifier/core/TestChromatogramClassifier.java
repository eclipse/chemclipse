/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * THIS IS A TEST CLASS. DO NOT USE IT BUT ONLY FOR TESTING PURPOSE.
 * 
 * @author eselmeister
 */
public class TestChromatogramClassifier extends AbstractChromatogramClassifier {

	@Override
	public IProcessingInfo<IChromatogramClassifierResult> applyClassifier(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramClassifierResult> processingInfo = new ProcessingInfo<IChromatogramClassifierResult>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Classifier", "The chromatogram selection or the settings are invalid.");
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
