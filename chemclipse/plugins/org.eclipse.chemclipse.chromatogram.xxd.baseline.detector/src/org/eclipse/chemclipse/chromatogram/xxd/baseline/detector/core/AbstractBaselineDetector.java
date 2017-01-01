/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.processing.BaselineDetectorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.processing.IBaselineDetectorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public abstract class AbstractBaselineDetector implements IBaselineDetector {

	private static final String ERROR_DESCRIPTION = "Baseline Detector";

	public IBaselineDetectorProcessingInfo validate(IChromatogramSelection chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor) {

		IProcessingMessage processingMessage;
		IBaselineDetectorProcessingInfo processingInfo = new BaselineDetectorProcessingInfo();
		if(chromatogramSelection == null) {
			processingMessage = new ProcessingMessage(MessageType.ERROR, ERROR_DESCRIPTION, "The chromatogram selection is invalid.");
			processingInfo.addMessage(processingMessage);
		} else {
			if(chromatogramSelection.getChromatogram() == null) {
				processingMessage = new ProcessingMessage(MessageType.ERROR, ERROR_DESCRIPTION, "The chromatogram is invalid.");
				processingInfo.addMessage(processingMessage);
			}
		}
		/*
		 * Settings
		 */
		if(baselineDetectorSettings == null) {
			processingMessage = new ProcessingMessage(MessageType.ERROR, ERROR_DESCRIPTION, "The baseline detector settings are invalid.");
			processingInfo.addMessage(processingMessage);
		}
		return processingInfo;
	}
}
