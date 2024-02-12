/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ListProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class MethodSupport {

	/**
	 * 
	 * @param object
	 * @return the {@link ListProcessEntryContainer} for this object if available and not read-only, <code>null</code> otherwise
	 */
	public static final ListProcessEntryContainer getContainer(Object object) {

		if(object instanceof IProcessEntry processEntry) {
			ProcessEntryContainer parent = processEntry.getParent();
			if(parent instanceof ListProcessEntryContainer container) {
				if(!container.isReadOnly()) {
					return container;
				}
			}
		}
		return null;
	}

	public static void runMethod(IMethodListener methodListener, IProcessMethod processMethod, Shell shell) {

		if(methodListener != null && processMethod != null) {
			try {
				/*
				 * Resume at a given position?
				 */
				int resumeIndex = ResumeMethodSupport.selectResumeIndex(shell, processMethod);
				processMethod.setResumeIndex(resumeIndex);
				//
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				dialog.run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						methodListener.execute(processMethod, monitor);
					}
				});
			} catch(InvocationTargetException e) {
				IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
				processingInfo.addErrorMessage(processMethod.getName(), "Execution failed", e.getCause());
				StatusLineLogger.setInfo(InfoType.ERROR_MESSAGE, ExtensionMessages.processMethodFailedSeeFeedback);
				ProcessingInfoPartSupport.getInstance().update(processingInfo);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch(MethodCancelException e) {
				IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
				processingInfo.addWarnMessage(processMethod.getName(), ExtensionMessages.processMethodExecutionCanceled);
				ProcessingInfoPartSupport.getInstance().update(processingInfo);
			}
		}
	}
}
