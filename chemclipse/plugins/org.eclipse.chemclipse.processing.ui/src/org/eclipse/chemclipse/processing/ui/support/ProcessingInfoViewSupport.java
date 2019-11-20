/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use {@link MessageProvider} interface, add support for E4 DI
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.support;

import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.ui.Activator;
import org.eclipse.chemclipse.processing.ui.parts.ProcessingInfoPart;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ProcessingInfoViewSupport {

	private final static String TITLE = "An error/some errors occured.";
	private final static String MESSAGE = "Please check the 'Feedback' view.";

	public ProcessingInfoViewSupport() {
	}

	public static void updateProcessingInfoError(String description, String message, Throwable e) {

		DefaultProcessingResult<?> errorResult = new DefaultProcessingResult<>();
		errorResult.addErrorMessage(description, message, e);
		updateProcessingInfo(errorResult, true);
	}

	/**
	 * Shows an error message reminder and tries to focus the part, if the processing info
	 * instance contains error messages.
	 * 
	 * @param processingInfo
	 */
	public static void updateProcessingInfo(final MessageProvider processingInfo) {

		if(processingInfo == null) {
			return;
		}
		updateProcessingInfo(processingInfo, processingInfo.hasErrorMessages());
	}

	/**
	 * Shows an error message reminder, if the processing info
	 * instance contains error messages.
	 * 
	 * @param processingInfo
	 */
	public static void updateProcessingInfo(final MessageProvider processingInfo, final boolean focusProcessingInfoView) {

		if(processingInfo == null) {
			return;
		}
		Display display = DisplayUtils.getDisplay();
		updateProcessingInfo(display, processingInfo, focusProcessingInfoView);
	}

	public static void updateProcessingInfo(final Display display, final MessageProvider processingInfo, final boolean focusProcessingInfoView) {

		if(processingInfo == null) {
			return;
		}
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(Activator.getDefault().getBundle().getBundleContext());
		DynamicProcessingInfoUpdateNotifier notifier = ContextInjectionFactory.make(DynamicProcessingInfoUpdateNotifier.class, serviceContext);
		notifier.update(processingInfo);
		// show popup if error occurred
		if(processingInfo != null && processingInfo.hasErrorMessages()) {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					Shell shell = DisplayUtils.getShell();
					if(shell != null) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
						messageBox.setText(TITLE);
						messageBox.setMessage(MESSAGE);
						messageBox.open();
					}
					// focus the view if requested, this will open the feedback view if required
					if(focusProcessingInfoView) {
						try {
							ModelSupportAddon.focusPart(ProcessingInfoPart.ID);
						} catch(IllegalStateException e) {
							// ignore then...
						}
					}
				}
			});
		}
	}
}
