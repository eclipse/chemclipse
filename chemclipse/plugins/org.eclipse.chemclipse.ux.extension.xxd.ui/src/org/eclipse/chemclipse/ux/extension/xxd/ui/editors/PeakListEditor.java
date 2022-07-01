/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PeakScanListUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PeakListEditor {

	@PostConstruct
	public void construct(Composite parent, ISupplier supplier, File file) {

		PeakScanListUI scanListUI = new PeakScanListUI(parent, SWT.NONE);
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(parent.getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					IProcessingInfo<IPeaks<?>> convert = PeakConverterMSD.convert(file, supplier.getId(), monitor);
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							IPeaks<?> result = convert.getProcessingResult();
							if(convert.hasErrorMessages() || result == null) {
								ProcessingInfoPartSupport.getInstance().update(convert);
							} else {
								scanListUI.setInput(result);
							}
						}
					});
				}
			});
		} catch(InvocationTargetException e) {
			IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage("PeakListEditor", "Open file " + file.getAbsolutePath() + " failed", e);
			StatusLineLogger.setInfo(InfoType.ERROR_MESSAGE, "Failed to open " + file.getName() + ". See Feedback for details.");
			ProcessingInfoPartSupport.getInstance().update(processingInfo);
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
