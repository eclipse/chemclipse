/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.cml.converter;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.database.AbstractDatabaseExportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.cml.l10n.Messages;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class DatabaseExportConverter extends AbstractDatabaseExportConverter {

	@Override
	public IProcessingInfo<File> convert(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	@Override
	public IProcessingInfo<File> convert(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) {

		return getProcessingInfo();
	}

	private IProcessingInfo<File> getProcessingInfo() {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		processingInfo.addErrorMessage(Messages.description, "The CML converter has no capabilities to export a library.");
		return processingInfo;
	}
}
