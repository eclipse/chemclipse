/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.supplier.zeroset.ui.modifier;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.model.processor.AbstractChromatogramProcessor;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class FilterModifier extends AbstractChromatogramProcessor implements IRunnableWithProgress {

	private static final String DESCRIPTION = "ZeroSet";
	private static final String FILTER_ID = "org.eclipse.chemclipse.chromatogram.csd.filter.supplier.zeroset";

	public FilterModifier(IChromatogramSelection chromatogramSelection) {
		super(chromatogramSelection);
	}

	@Override
	public void execute(IProgressMonitor monitor) {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			IProcessingInfo processingInfo = ChromatogramFilter.applyFilter(chromatogramSelection, FILTER_ID, monitor);
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

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			getChromatogramSelection().getChromatogram().doOperation(this, monitor);
		} finally {
			monitor.done();
		}
	}
}
