/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.ui.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FilterModifier extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Savitzky-Golay Smoothing";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay";
	private static final String FILTER_MSD_ID = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.savitzkygoaly";
	private static final String FILTER_CSD_ID = "org.eclipse.chemclipse.chromatogram.csd.filter.supplier.savitzkygolay";

	@SuppressWarnings("rawtypes")
	public FilterModifier(IChromatogramSelection chromatogramSelection) {
		super(chromatogramSelection);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(IProgressMonitor monitor) {

		if(getChromatogramSelection() != null) {
			IChromatogramSelection chromatogramSelection = getChromatogramSelection();
			IProcessingInfo processingInfo;
			if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
				processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, FILTER_MSD_ID, monitor);
			} else if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
				processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, FILTER_CSD_ID, monitor);
			} else {
				processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, FILTER_ID, monitor);
			}
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			
		}
	}

	@Override
	public String getDescription() {

		return DESCRIPTION;
	}

	// ----------------------------------------------------------------------IRunnableWithProgress
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram modifier
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
	// ----------------------------------------------------------------------IRunnableWithProgress
}
