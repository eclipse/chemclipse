/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.peak;

import org.eclipse.chemclipse.converter.core.AbstractExportConverter;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public abstract class AbstractPeakExportConverter extends AbstractExportConverter implements IPeakExportConverter {

	public IProcessingInfo validate(IPeakMSD peak) {

		if(peak == null) {
			return getProcessingInfo("The peak couldn't be found.");
		} else {
			return new ProcessingInfo();
		}
	}

	public IProcessingInfo validate(IPeaks peaks) {

		if(peaks == null) {
			return getProcessingInfo("The peaks couldn't be found.");
		} else {
			return new ProcessingInfo();
		}
	}

	private IProcessingInfo getProcessingInfo(String message) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Export Converter", message);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
