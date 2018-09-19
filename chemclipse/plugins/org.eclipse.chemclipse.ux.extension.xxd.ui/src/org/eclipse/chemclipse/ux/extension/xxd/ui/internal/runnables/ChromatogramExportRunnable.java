/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

@SuppressWarnings("rawtypes")
public class ChromatogramExportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramExportRunnable.class);
	private File data;
	private File file;
	private IChromatogram chromatogram;
	private ISupplier supplier;
	private DataType dataType;

	public ChromatogramExportRunnable(File file, IChromatogram chromatogram, ISupplier supplier, DataType dataType) {
		this.file = file;
		this.chromatogram = chromatogram;
		this.supplier = supplier;
		this.dataType = dataType;
	}

	public File getData() {

		return data;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Export Chromatogram", IProgressMonitor.UNKNOWN);
			IProcessingInfo processingInfo = null;
			switch(dataType) {
				case MSD_NOMINAL:
				case MSD_TANDEM:
				case MSD_HIGHRES:
				case MSD:
					if(chromatogram instanceof IChromatogramMSD) {
						IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
						processingInfo = ChromatogramConverterMSD.convert(file, chromatogramMSD, supplier.getId(), monitor);
					}
					break;
				case CSD:
					if(chromatogram instanceof IChromatogramCSD) {
						IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
						processingInfo = ChromatogramConverterCSD.convert(file, chromatogramCSD, supplier.getId(), monitor);
					}
					break;
				case WSD:
					if(chromatogram instanceof IChromatogramWSD) {
						IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
						processingInfo = ChromatogramConverterWSD.convert(file, chromatogramWSD, supplier.getId(), monitor);
					}
					break;
				default:
					// Do nothing
			}
			//
			if(processingInfo != null) {
				try {
					data = processingInfo.getProcessingResult(File.class);
				} catch(TypeCastException e) {
					logger.warn(e);
				}
			}
		} finally {
			monitor.done();
		}
	}
}
