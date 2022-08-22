/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics, Logging
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramImportRunnable.class);
	//
	private List<File> files = new ArrayList<>();
	private DataType dataType;
	private List<IChromatogramSelection<?, ?>> chromatogramSelections = new ArrayList<>();

	public ChromatogramImportRunnable(File file, DataType dataType) {

		this(Arrays.asList(file), dataType);
	}

	public ChromatogramImportRunnable(List<File> files, DataType dataType) {

		this.files.addAll(files);
		this.dataType = dataType;
	}

	public IChromatogramSelection<?, ?> getChromatogramSelection() {

		if(!chromatogramSelections.isEmpty()) {
			return chromatogramSelections.get(0);
		} else {
			return null;
		}
	}

	public List<IChromatogramSelection<?, ?>> getChromatogramSelections() {

		return chromatogramSelections;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			/*
			 * Don't fire an update.
			 */
			monitor.beginTask("Import Chromatogram(s)", IProgressMonitor.UNKNOWN);
			for(File file : files) {
				boolean fireUpdate = false;
				switch(dataType) {
					case MSD_NOMINAL:
					case MSD_TANDEM:
					case MSD_HIGHRES:
					case MSD:
						IProcessingInfo<IChromatogramMSD> processingInfoMSD = ChromatogramConverterMSD.getInstance().convert(file, monitor);
						IChromatogramMSD chromatogramMSD = processingInfoMSD.getProcessingResult();
						chromatogramSelections.add(new ChromatogramSelectionMSD(chromatogramMSD, fireUpdate));
						break;
					case CSD:
						IProcessingInfo<IChromatogramCSD> processingInfoCSD = ChromatogramConverterCSD.getInstance().convert(file, monitor);
						IChromatogramCSD chromatogramCSD = processingInfoCSD.getProcessingResult();
						chromatogramSelections.add(new ChromatogramSelectionCSD(chromatogramCSD, fireUpdate));
						break;
					case WSD:
						IProcessingInfo<IChromatogramWSD> processingInfoWSD = ChromatogramConverterWSD.getInstance().convert(file, monitor);
						IChromatogramWSD chromatogramWSD = processingInfoWSD.getProcessingResult();
						chromatogramSelections.add(new ChromatogramSelectionWSD(chromatogramWSD, fireUpdate));
						break;
					default:
						// No action
				}
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			monitor.done();
		}
	}
}
