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
package org.eclipse.chemclipse.ux.extension.xxd.ui.handlers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.AbstractChromatogramEditorAction;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.IChromatogramEditorAction;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;

public class ChromatogramEditorAction extends AbstractChromatogramEditorAction implements IChromatogramEditorAction {

	private static final Logger logger = Logger.getLogger(ChromatogramEditorAction.class);
	//
	private static final String DESCRIPTION = "Method Editor Action";
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();

	@Override
	public IProcessingInfo applyAction(IChromatogramSelection<? extends IPeak> chromatogramSelection) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		//
		String directoryPath = preferenceStore.getString(PreferenceConstants.P_METHOD_EXPLORER_PATH_ROOT_FOLDER);
		String selectedMethod = preferenceStore.getString(PreferenceConstants.P_SELECTED_METHOD_NAME);
		File file = new File(directoryPath + File.separator + selectedMethod);
		if(file.exists()) {
			try {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
				dialog.run(false, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						IProcessingInfo processingInfo = MethodConverter.convert(file, monitor);
						if(!processingInfo.hasErrorMessages()) {
							try {
								IProcessMethod processMethod = processingInfo.getProcessingResult(ProcessMethod.class);
								IProcessingInfo processorInfo = processTypeSupport.applyProcessor(chromatogramSelection, processMethod, monitor);
								if(processorInfo.hasErrorMessages()) {
									processingInfo.addMessages(processorInfo);
								} else {
									processingInfo.addInfoMessage(DESCRIPTION, "Success processing file: " + file);
								}
							} catch(Exception e) {
								logger.warn(e);
							}
						}
					}
				});
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
			}
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "It seems that no method has been selected yet.");
		}
		//
		return processingInfo;
	}
}
