/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter.supplier.jcampdx.converter;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xir.converter.core.AbstractScanImportConverter;
import org.eclipse.chemclipse.xir.converter.core.IScanImportConverter;
import org.eclipse.chemclipse.xir.converter.supplier.jcampdx.io.ScanReader;
import org.eclipse.chemclipse.xir.converter.supplier.jcampdx.model.IVendorScanXIR;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ScanImportConverter extends AbstractScanImportConverter implements IScanImportConverter {

	private static final Logger logger = Logger.getLogger(ScanImportConverter.class);

	@Override
	public IProcessingInfo<IVendorScanXIR> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IVendorScanXIR> processingInfo = new ProcessingInfo<>();
		try {
			ScanReader scanReader = new ScanReader();
			IVendorScanXIR vendorScan = scanReader.read(file, monitor);
			processingInfo.setProcessingResult(vendorScan);
		} catch(IOException e) {
			processingInfo.addErrorMessage("JCAMP-DX", "There was a problem to import the FT-IR file.");
			logger.warn(e);
		}
		return processingInfo;
	}
}
