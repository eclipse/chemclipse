/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PrincipleComponentProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PcaRunnable implements IRunnableWithProgress {

	private List<IDataInputEntry> dataInputEntries;
	private int retentionTimeWindow;
	private int numberOfPrincipleComponents;
	private PcaResults pcaResults;
	private int extractionType;

	public PcaRunnable(List<IDataInputEntry> dataInputEntries, int retentionTimeWindow, int numberOfPrincipleComponents, int exType) {
		this.dataInputEntries = dataInputEntries;
		this.retentionTimeWindow = retentionTimeWindow;
		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
		this.extractionType = exType;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		PrincipleComponentProcessor principleComponentProcessor = new PrincipleComponentProcessor();
		// Extraction type argument 0 for peaks, 1 for scans
		pcaResults = principleComponentProcessor.process(dataInputEntries, retentionTimeWindow, numberOfPrincipleComponents, monitor, extractionType);
	}

	public PcaResults getPcaResults() {

		return pcaResults;
	}
}
