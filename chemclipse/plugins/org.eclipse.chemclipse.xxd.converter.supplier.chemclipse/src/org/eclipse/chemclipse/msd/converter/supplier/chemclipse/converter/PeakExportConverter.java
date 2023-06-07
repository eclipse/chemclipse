/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
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
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.converter;

import java.io.File;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.AbstractPeakExportConverter;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakExportConverter extends AbstractPeakExportConverter {

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public IProcessingInfo convert(File file, IPeaks peaks, boolean append, IProgressMonitor monitor) {

		return getErrorMessage();
	}

	private IProcessingInfo<?> getErrorMessage() {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage("OCB Peak Writer", "There is no capability to write peaks in *.ocb format.");
		return processingInfo;
	}
}
