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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.ui.internal.runnable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.core.PrincipleComponentProcessor;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.model.IPeakInputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.model.PcaResults;

public class PcaRunnable implements IRunnableWithProgress {

	private List<IPeakInputEntry> inputEntries;
	private int retentionTimeWindow;
	private int numberOfPrincipleComponents;
	private PcaResults pcaResults;

	public PcaRunnable(List<IPeakInputEntry> inputEntries, int retentionTimeWindow, int numberOfPrincipleComponents) {

		this.inputEntries = inputEntries;
		this.retentionTimeWindow = retentionTimeWindow;
		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		PrincipleComponentProcessor principleComponentProcessor = new PrincipleComponentProcessor();
		List<File> inputFiles = new ArrayList<File>();
		for(IPeakInputEntry inputEntry : inputEntries) {
			inputFiles.add(new File(inputEntry.getInputFile()));
		}
		pcaResults = principleComponentProcessor.process(inputFiles, retentionTimeWindow, numberOfPrincipleComponents, monitor);
	}

	public PcaResults getPcaResults() {

		return pcaResults;
	}
}
