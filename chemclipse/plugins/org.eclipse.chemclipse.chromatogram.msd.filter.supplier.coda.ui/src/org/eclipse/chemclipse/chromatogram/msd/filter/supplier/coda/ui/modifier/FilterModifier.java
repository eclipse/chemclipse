/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.ui.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class FilterModifier extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String DESCRIPTION = "CODA Filter";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda";

	public FilterModifier(IChromatogramSelectionMSD chromatogramSelection) {
		super(chromatogramSelection);
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		if(getChromatogramSelection() instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)getChromatogramSelection();
			final IProcessingInfo processingInfo = ChromatogramFilterMSD.applyFilter(chromatogramSelection, FILTER_ID, monitor);
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					chromatogramSelection.reset(true);
				}
			});
		}
	}

	@Override
	public String getDescription() {

		return DESCRIPTION;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * The doOperation calls the execute of the chromatogram modifier
		 * method.<br/> Why not doing it directly? Model and GUI should be
		 * handled separately.
		 */
		try {
			monitor.beginTask("CODA Filter", IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
}
