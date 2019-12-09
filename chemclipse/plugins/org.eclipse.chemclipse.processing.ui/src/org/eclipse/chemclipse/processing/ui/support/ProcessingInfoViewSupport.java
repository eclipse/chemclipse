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

import javax.inject.Inject;

import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.ui.Activator;
import org.eclipse.chemclipse.processing.ui.parts.ProcessingInfoPart;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

@Creatable
public class ProcessingInfoViewSupport {

	private final static String TITLE = "An error/some errors occured.";
	private final static String MESSAGE = "Please check the 'Feedback' view.";
	@Inject
	IEclipseContext eclipseContext;
	@Inject
	DynamicProcessingInfoUpdateNotifier infoUpdateNotifier;
	@Inject
	UISynchronize uiSynchronize;
	@Inject
	PartSupport partSupport;
	private static final ProcessingInfoViewSupport INSTANCE = new ProcessingInfoViewSupport();
	static {
		INSTANCE.eclipseContext = EclipseContextFactory.getServiceContext(Activator.getDefault().getBundle().getBundleContext());
		INSTANCE.infoUpdateNotifier = ContextInjectionFactory.make(DynamicProcessingInfoUpdateNotifier.class, INSTANCE.eclipseContext);
		INSTANCE.uiSynchronize = new UISynchronize() {

			@Override
			public void syncExec(Runnable runnable) {

				Display.getDefault().syncExec(runnable);
			}

			@Override
			public void asyncExec(Runnable runnable) {

				Display.getDefault().asyncExec(runnable);
			}
		};
		INSTANCE.partSupport = ModelSupportAddon.getPartSupport();
	}

	public ProcessingInfoViewSupport() {
	}

	public void update(final MessageProvider processingInfo, final boolean focusProcessingInfoView) {

		if(processingInfo == null) {
			return;
		}
		if(processingInfo.hasErrorMessages()) {
			for(IProcessingMessage message : processingInfo.getMessages()) {
				if(message.getMessageType() == MessageType.ERROR) {
					logError(message.getDescription(), message.getMessage(), message.getException());
				}
			}
		}
		try {
			infoUpdateNotifier.update(processingInfo);
		} catch(RuntimeException e) {
			logError(ProcessingInfoViewSupport.class.getName(), "calling infoUpdateNotifier failed", e);
		}
		// show popup if error occurred
		if(processingInfo != null && processingInfo.hasErrorMessages()) {
			uiSynchronize.asyncExec(new Runnable() {

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
							partSupport.focusPart(ProcessingInfoPart.ID);
						} catch(RuntimeException e) {
							logError(ProcessingInfoViewSupport.class.getName(), "focus part failed", e);
						}
					}
				}
			});
		}
	}

	public void update(final MessageProvider processingInfo) {

		if(processingInfo == null) {
			return;
		}
		update(processingInfo, processingInfo.hasErrorMessages());
	}

	public void showError(String description, String message, Throwable e) {

		DefaultProcessingResult<?> errorResult = new DefaultProcessingResult<>();
		errorResult.addErrorMessage(description, message, e);
		update(errorResult);
	}

	@Deprecated
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
	@Deprecated
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
	@Deprecated
	public static void updateProcessingInfo(final MessageProvider processingInfo, final boolean focusProcessingInfoView) {

		if(processingInfo == null) {
			return;
		}
		Display display = DisplayUtils.getDisplay();
		updateProcessingInfo(display, processingInfo, focusProcessingInfoView);
	}

	@Deprecated
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

	private static void logError(String description, String message, Throwable e) {

		Activator.getDefault().getLog().log(new Status(IStatus.ERROR, description, message, e));
	}
}
