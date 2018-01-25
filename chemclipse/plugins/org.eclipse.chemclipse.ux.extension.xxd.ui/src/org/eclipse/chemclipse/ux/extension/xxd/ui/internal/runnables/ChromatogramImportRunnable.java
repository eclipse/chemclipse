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

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(ChromatogramImportRunnable.class);
	//
	private File file;
	private DataType dataType;
	private IChromatogramSelection chromatogramSelection;

	public ChromatogramImportRunnable(File file, DataType dataType) {
		this.file = file;
		this.dataType = dataType;
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Import Chromatogram", IProgressMonitor.UNKNOWN);
			switch(dataType) {
				case MSD_NOMINAL:
				case MSD_TANDEM:
				case MSD_HIGHRES:
				case MSD:
					chromatogramSelection = new ChromatogramSelectionMSD(ChromatogramConverterMSD.convert(file, monitor).getChromatogram());
					break;
				case CSD:
					chromatogramSelection = new ChromatogramSelectionCSD(ChromatogramConverterCSD.convert(file, monitor).getChromatogram());
					break;
				case WSD:
					chromatogramSelection = new ChromatogramSelectionCSD(ChromatogramConverterWSD.convert(file, monitor).getChromatogram());
					break;
				default:
					// No action
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			monitor.done();
		}
	}
}
