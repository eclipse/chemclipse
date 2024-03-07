/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - refactoring vibrational spectroscopy
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.cml.converter;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.core.AbstractScanExportConverter;
import org.eclipse.chemclipse.wsd.converter.core.IScanExportConverter;
import org.eclipse.chemclipse.wsd.converter.supplier.cml.io.ScanWriter;
import org.eclipse.chemclipse.wsd.model.core.ISpectrumWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ScanExportConverter extends AbstractScanExportConverter implements IScanExportConverter {

	@Override
	public IProcessingInfo<File> convert(File file, ISpectrumWSD scan, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = validate(file);
		if(!processingInfo.hasErrorMessages()) {
			ScanWriter scanWriter = new ScanWriter();
			scanWriter.write(file, scan, monitor);
			processingInfo.setProcessingResult(file);
		}
		return processingInfo;
	}
}