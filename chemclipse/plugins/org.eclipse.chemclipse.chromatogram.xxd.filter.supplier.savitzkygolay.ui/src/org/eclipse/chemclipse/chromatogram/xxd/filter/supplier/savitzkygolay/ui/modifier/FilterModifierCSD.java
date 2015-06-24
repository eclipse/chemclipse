/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;

public class FilterModifierCSD extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String DESCRIPTION = "Savitzky-Golay Filter";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.csd.filter.supplier.savitzkygolay";

	public FilterModifierCSD(IChromatogramSelectionCSD chromatogramSelection) {

		super(chromatogramSelection);
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		if(getChromatogramSelection() instanceof IChromatogramSelectionCSD) {
			IChromatogramSelectionCSD chromatogramSelection = (IChromatogramSelectionCSD)getChromatogramSelection();
			/*
			 * Apply the filter.
			 */
			final IChromatogramFilterProcessingInfo processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, FILTER_ID, monitor);
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					/*
					 * Show the processing view if error messages occurred.
					 */
					ProcessingInfoViewSupport.showErrorInfoReminder(processingInfo);
					ProcessingInfoViewSupport.updateProcessingInfoView(processingInfo);
				}
			});
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
