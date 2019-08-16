/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramExportRunnable implements IRunnableWithProgress {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ChromatogramExportRunnable.class);
	private File data;
	private File file;
	private IChromatogramMSD chromatogram;
	private ISupplier supplier;

	public ChromatogramExportRunnable(File file, IChromatogramMSD chromatogram, ISupplier supplier) {

		this.file = file;
		this.chromatogram = chromatogram;
		this.supplier = supplier;
	}

	/**
	 * Returns the written chromatogram file or null.
	 */
	public File getData() {

		return data;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			IProcessingInfo<File> processingInfo = ChromatogramConverterMSD.getInstance().convert(file, chromatogram, supplier.getId(), monitor);
			data = processingInfo.getProcessingResult();
		} finally {
			monitor.done();
		}
	}
}
