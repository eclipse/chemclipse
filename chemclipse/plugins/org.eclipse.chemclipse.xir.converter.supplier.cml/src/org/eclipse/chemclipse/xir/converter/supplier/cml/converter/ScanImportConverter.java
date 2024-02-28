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
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter.supplier.cml.converter;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xir.converter.core.AbstractScanImportConverter;
import org.eclipse.chemclipse.xir.converter.core.IScanImportConverter;
import org.eclipse.chemclipse.xir.converter.supplier.cml.io.ScanReader;
import org.eclipse.chemclipse.xir.converter.supplier.cml.model.IVendorSpectrumXIR;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ScanImportConverter extends AbstractScanImportConverter implements IScanImportConverter {

	@Override
	public IProcessingInfo<IVendorSpectrumXIR> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IVendorSpectrumXIR> processingInfo = new ProcessingInfo<>();
		IVendorSpectrumXIR vendorScan = null;
		ScanReader scanReader = new ScanReader();
		vendorScan = scanReader.read(file, monitor);
		processingInfo.setProcessingResult(vendorScan);
		return processingInfo;
	}
}
