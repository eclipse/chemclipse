/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.processor.core;

import org.eclipse.chemclipse.nmr.model.core.IScanNMR;
import org.eclipse.chemclipse.nmr.processor.settings.IProcessorSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public abstract class AbstractScanProcessor implements IScanProcessor {

	private static final String DESCRIPTION = "NMR Processor";

	public IProcessingInfo validate(final IScanNMR scanNMR, final IProcessorSettings processorSettings) {

		final IProcessingInfo processingInfo = new ProcessingInfo();
		if(scanNMR == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The NMR scan is not available.");
		}
		return processingInfo;
	}
}
