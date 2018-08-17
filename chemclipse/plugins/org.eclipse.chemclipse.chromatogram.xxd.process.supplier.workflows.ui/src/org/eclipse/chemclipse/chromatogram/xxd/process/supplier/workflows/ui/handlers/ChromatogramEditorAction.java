/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.runnables.EvaluationSelectionRunnable;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.AbstractChromatogramEditorAction;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.IChromatogramEditorAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ChromatogramEditorAction extends AbstractChromatogramEditorAction implements IChromatogramEditorAction {

	private static final Logger logger = Logger.getLogger(ChromatogramEditorAction.class);

	@Override
	public IProcessingInfo applyAction(IChromatogramSelection<? extends IPeak> chromatogramSelection) {

		IProcessingInfo processingInfo = validate(chromatogramSelection);
		if(!processingInfo.hasErrorMessages()) {
			IRunnableWithProgress runnable = null;
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				runnable = new EvaluationSelectionRunnable((IChromatogramSelectionMSD)chromatogramSelection);
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(DisplayUtils.getShell());
				try {
					monitor.run(true, true, runnable);
				} catch(InvocationTargetException e) {
					logger.warn(e);
				} catch(InterruptedException e) {
					logger.warn(e);
				}
			}
		}
		return processingInfo;
	}
}
