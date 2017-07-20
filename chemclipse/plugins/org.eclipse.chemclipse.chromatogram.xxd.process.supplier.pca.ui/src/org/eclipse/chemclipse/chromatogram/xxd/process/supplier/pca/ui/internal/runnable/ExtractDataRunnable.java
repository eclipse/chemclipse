/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ExtractDataRunnable implements IRunnableWithProgress {

	private List<IDataInputEntry> dataInputEntries;
	private int extractionType;
	private IPcaResults pcaResults;
	private int retentionTimeWindow;

	public ExtractDataRunnable(List<IDataInputEntry> dataInputEntries, int retentionTimeWindow, int exType) {
		this.dataInputEntries = dataInputEntries;
		this.retentionTimeWindow = retentionTimeWindow;
		this.extractionType = exType;
	}

	public IPcaResults getPcaResults() {

		return pcaResults;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * Extraction type argument 0 for peaks, 1 for scans
		 */
		PcaExtractionData pcaExtractData = new PcaExtractionData();
		pcaResults = pcaExtractData.proccess(dataInputEntries, retentionTimeWindow, monitor, extractionType);
	}
}
