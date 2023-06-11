/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xir.converter.core.AbstractScanExportConverter;
import org.eclipse.chemclipse.xir.converter.core.IScanExportConverter;
import org.eclipse.chemclipse.xir.model.core.ISpectrumXIR;
import org.eclipse.core.runtime.IProgressMonitor;

public class ScanExportConverter extends AbstractScanExportConverter implements IScanExportConverter {

	@Override
	public IProcessingInfo<File> convert(File file, ISpectrumXIR scan, IProgressMonitor monitor) {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		processingInfo.addInfoMessage("FTIR", "Export is not available");
		return processingInfo;
	}
}
