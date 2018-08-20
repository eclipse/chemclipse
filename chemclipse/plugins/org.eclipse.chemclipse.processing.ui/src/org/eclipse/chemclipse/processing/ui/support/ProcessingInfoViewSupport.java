/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.support;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.parts.ProcessingInfoPart;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public class ProcessingInfoViewSupport {

	private final static String TITLE = "An error/some errors occured.";
	private final static String MESSAGE = "Please check the 'Feedback' view.";
	private static DynamicProcessingInfoUpdateNotifier dynamicUpdateNotifier;

	private ProcessingInfoViewSupport() {
	}

	/**
	 * Shows an error message reminder, if the processing info
	 * instance contains error messages.
	 * 
	 * @param processingInfo
	 */
	public static void updateProcessingInfo(final IProcessingInfo processingInfo, final boolean focusProcessingInfoView) {

		/*
		 * Info error message.
		 */
		Display display = DisplayUtils.getDisplay();
		display.asyncExec(new Runnable() {

			@Override
			public void run() {

				/*
				 * Show the message box.
				 */
				if(processingInfo != null && processingInfo.hasErrorMessages()) {
					Shell shell = DisplayUtils.getShell();
					if(shell != null) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
						messageBox.setText(TITLE);
						messageBox.setMessage(MESSAGE);
						messageBox.open();
					}
				}
				/*
				 * Update the info view.
				 * Focus the view.
				 */
				updateProcessingInfoView(processingInfo);
				if(focusProcessingInfoView) {
					/*
					 * Focus this part.
					 * Use the ProcessingInfoPart.ID in the Application.e4xmi
					 */
					ModelSupportAddon.focusPart(ProcessingInfoPart.ID);
				}
			}
		});
	}

	/**
	 * Updates the processing info view.
	 * Call this method inside a Display.asyncExec run method.
	 * 
	 * @param processingInfo
	 */
	private static void updateProcessingInfoView(IProcessingInfo processingInfo) {

		/*
		 * Create the dynamic update notifier if it has been not created yet.
		 */
		if(dynamicUpdateNotifier == null) {
			Bundle bundle = FrameworkUtil.getBundle(ProcessingInfoViewSupport.class);
			BundleContext bundleContext = bundle.getBundleContext();
			IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
			eclipseContext.set(Logger.class, null);
			dynamicUpdateNotifier = ContextInjectionFactory.make(DynamicProcessingInfoUpdateNotifier.class, eclipseContext);
		}
		/*
		 * Send an event using the event broker mechanism of e4.
		 */
		dynamicUpdateNotifier.update(processingInfo);
	}
}
