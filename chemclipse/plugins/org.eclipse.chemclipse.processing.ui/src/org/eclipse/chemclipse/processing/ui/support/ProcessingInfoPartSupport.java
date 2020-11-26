/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.ui.Activator;
import org.eclipse.chemclipse.processing.ui.parts.ProcessingInfoPart;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

@Creatable
public class ProcessingInfoPartSupport {

	private static final Logger logger = Logger.getLogger(ProcessingInfoPartSupport.class);
	//
	private static final String TITLE = "An error/some errors occured.";
	private static final String MESSAGE = "Please check the 'Feedback' part.";
	//
	private UISynchronize uiSynchronize = null;
	private ProcessingInfoUpdateNotifier processingInfoUpdateNotifier = null;

	/**
	 * Use getInstance() instead or create this support via:
	 * <code>ContextInjectionFactory.make(ProcessingInfoViewSupport.class, eclipseContext);</code>
	 */
	public ProcessingInfoPartSupport() {

	}

	public static ProcessingInfoPartSupport getInstance() {

		return Activator.getDefault().getProcessingInfoPartSupport();
	}

	/**
	 * Update the message provider and show the processing info part on demand.
	 * 
	 * @param messageProvider
	 * @param focusProcessingInfoPart
	 */
	public void update(final MessageProvider messageProvider, final boolean focusProcessingInfoPart) {

		if(messageProvider == null) {
			return;
		}
		/*
		 * Log the errors.
		 */
		if(messageProvider.hasErrorMessages()) {
			for(IProcessingMessage message : messageProvider.getMessages()) {
				if(message.getMessageType() == MessageType.ERROR) {
					logErrorMessage(message.getDescription(), message.getMessage(), message.getException());
				}
			}
		}
		/*
		 * Update the message.
		 */
		try {
			getProcessingInfoUpdateNotifier().update(messageProvider);
		} catch(RuntimeException e) {
			logErrorMessage(ProcessingInfoPartSupport.class.getName(), "Calling the info update notifier failed.", e);
		}
		/*
		 * Display a message if an error occurred.
		 */
		if(messageProvider.hasErrorMessages()) {
			getUISynchronize().asyncExec(new Runnable() {

				@Override
				public void run() {

					Shell shell = DisplayUtils.getShell();
					if(shell != null) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
						messageBox.setText(TITLE);
						messageBox.setMessage(MESSAGE);
						messageBox.open();
					}
					/*
					 * Focus the view if requested, this will open the feedback view if required.
					 */
					if(focusProcessingInfoPart) {
						try {
							EModelService modelService = Activator.getDefault().getModelService();
							MApplication application = Activator.getDefault().getApplication();
							EPartService partService = Activator.getDefault().getPartService();
							//
							if(modelService != null && application != null && partService != null) {
								MUIElement element = modelService.find(ProcessingInfoPart.ID, application);
								if(element instanceof MPart) {
									MPart part = (MPart)element;
									/*
									 * Prevent the error by using asyncExcec
									 * "Application does not have an active window"
									 */
									Display.getDefault().asyncExec(new Runnable() {

										@Override
										public void run() {

											partService.showPart(part, PartState.ACTIVATE);
										}
									});
								}
							}
						} catch(RuntimeException e) {
							logErrorMessage(ProcessingInfoPartSupport.class.getName(), "Failed to focus on part.", e);
						}
					}
				}
			});
		}
	}

	/**
	 * Updates the messages and focus on the processing error part automatically.
	 * 
	 * @param messageProvider
	 */
	public void update(final MessageProvider messageProvider) {

		if(messageProvider == null) {
			return;
		}
		//
		update(messageProvider, messageProvider.hasErrorMessages());
	}

	private UISynchronize getUISynchronize() {

		if(uiSynchronize == null) {
			uiSynchronize = new UISynchronize() {

				@Override
				public void syncExec(Runnable runnable) {

					Display.getDefault().syncExec(runnable);
				}

				@Override
				public void asyncExec(Runnable runnable) {

					Display.getDefault().asyncExec(runnable);
				}
			};
		}
		//
		return uiSynchronize;
	}

	private ProcessingInfoUpdateNotifier getProcessingInfoUpdateNotifier() {

		if(processingInfoUpdateNotifier == null) {
			processingInfoUpdateNotifier = new ProcessingInfoUpdateNotifier();
		}
		//
		return processingInfoUpdateNotifier;
	}

	private static void logErrorMessage(String description, String message, Throwable e) {

		logger.warn(description);
		logger.warn(message);
		logger.warn(e);
	}
}
