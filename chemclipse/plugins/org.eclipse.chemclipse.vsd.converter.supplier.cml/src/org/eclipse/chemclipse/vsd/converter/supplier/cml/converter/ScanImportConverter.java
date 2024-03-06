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
package org.eclipse.chemclipse.vsd.converter.supplier.cml.converter;

import java.io.File;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.vsd.converter.core.AbstractScanImportConverter;
import org.eclipse.chemclipse.vsd.converter.core.IScanImportConverter;
import org.eclipse.chemclipse.vsd.converter.supplier.cml.io.ScanReader;
import org.eclipse.chemclipse.vsd.converter.supplier.cml.model.IVendorSpectrumVSD;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class ScanImportConverter extends AbstractScanImportConverter implements IScanImportConverter {

	@Override
	public IProcessingInfo<IVendorSpectrumVSD> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IVendorSpectrumVSD> processingInfo = new ProcessingInfo<>();
		IVendorSpectrumVSD vendorScan = null;
		ScanReader scanReader = new ScanReader();
		vendorScan = scanReader.read(file, monitor);
		processingInfo.setProcessingResult(vendorScan);
		return processingInfo;
	}
}