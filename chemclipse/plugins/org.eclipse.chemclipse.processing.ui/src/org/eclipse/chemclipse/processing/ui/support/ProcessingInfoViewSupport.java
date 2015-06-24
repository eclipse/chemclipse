/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.support;

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

import org.eclipse.chemclipse.processing.core.IProcessingInfo;

@SuppressWarnings("restriction")
public class ProcessingInfoViewSupport {

	private static DynamicProcessingInfoUpdateNotifier dynamicUpdateNotifier;

	private ProcessingInfoViewSupport() {

	}

	/**
	 * Shows an error message reminder, if the processing info
	 * instance contains error messages.
	 * 
	 * @param processingInfo
	 */
	public static void showErrorInfoReminder(IProcessingInfo processingInfo) {

		if(processingInfo != null && processingInfo.hasErrorMessages()) {
			Shell shell = Display.getCurrent().getActiveShell();
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
			messageBox.setText("An error/some errors occured.");
			messageBox.setMessage("Please check the 'Processing Info' view.");
			messageBox.open();
		}
	}

	/**
	 * Updates the processing info view.
	 * Call this method inside a Display.asyncExec run method.
	 * 
	 * @param processingInfo
	 */
	public static void updateProcessingInfoView(IProcessingInfo processingInfo) {

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
