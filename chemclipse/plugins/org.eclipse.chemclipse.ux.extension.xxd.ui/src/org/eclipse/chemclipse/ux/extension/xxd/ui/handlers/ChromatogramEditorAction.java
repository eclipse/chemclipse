/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics, Logging
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
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

public class ChromatogramEditorAction<T> extends AbstractChromatogramEditorAction<T> implements IChromatogramEditorAction<T> {

	private static final Logger logger = Logger.getLogger(ChromatogramEditorAction.class);
	//
	private static final String DESCRIPTION = "Method Editor Action";
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();

	@Override
	public IProcessingInfo<T> applyAction(IChromatogramSelection<?, ?> chromatogramSelection) {

		//
		String selectedMethod = preferenceStore.getString(PreferenceConstants.P_SELECTED_METHOD_NAME);
		Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
		for(IProcessMethod method : userMethods) {
			if(method.getName().equals(selectedMethod)) {
				IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
				try {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
					dialog.run(false, false, new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

							try {
								IProcessingInfo<IProcessMethod> processorInfo = processTypeSupport.applyProcessor(chromatogramSelection, method, monitor);
								if(processorInfo.hasErrorMessages()) {
									processingInfo.addMessages(processorInfo);
								} else {
									processingInfo.addInfoMessage(DESCRIPTION, "Success processing file: " + method.getName());
								}
							} catch(Exception e) {
								logger.error(e.getLocalizedMessage(), e);
							}
						}
					});
				} catch(InvocationTargetException e) {
					logger.error(e.getLocalizedMessage(), e);
				} catch(InterruptedException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
				return processingInfo;
			}
		}
		ProcessingInfo<T> errorInfo = new ProcessingInfo<>();
		errorInfo.addErrorMessage(DESCRIPTION, "It seems that no method has been selected yet.");
		return errorInfo;
	}
}
