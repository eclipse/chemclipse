/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.progress.ui.internal.core;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.progress.core.IStatusLineMessageListener;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.ui.exceptions.DisplayException;
import org.eclipse.chemclipse.progress.ui.exceptions.StatusLineManagerException;

/**
 * Prints messages to the status line.
 * 
 * @author eselmeister
 */
public class UIStatusLineLogger implements IStatusLineMessageListener {

	private static final Logger logger = Logger.getLogger(UIStatusLineLogger.class);
	private IStatusLineManager statusLineManager;
	private Display display;

	@Override
	public void setInfo(final InfoType infoType, final String message) {

		Display display;
		final IStatusLineManager statusLineManager;
		try {
			statusLineManager = getStatusLineManager();
			display = getDisplay();
			/*
			 * AsyncExec to update the display.
			 */
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					switch(infoType) {
						case MESSAGE:
							statusLineManager.setMessage(message);
							break;
						case ERROR_MESSAGE:
							statusLineManager.setErrorMessage(message);
							break;
					}
				}
			});
		} catch(StatusLineManagerException e) {
			/*
			 * There is no appropriate status line in Eclipse 4.3.
			 * There should be one, but this is a bug.
			 */
			// logger.warn(e);
		} catch(DisplayException e) {
			logger.warn(e);
		}
	}

	// ---------------------------------------------------private methods
	private Display getDisplay() throws DisplayException {

		if(display == null) {
			display = Display.getCurrent();
			if(display == null) {
				throw new DisplayException("The display instance is null.");
			}
		}
		return display;
	}

	/**
	 * Initializes the status line manager.
	 */
	private IStatusLineManager getStatusLineManager() throws StatusLineManagerException {

		/*
		 * If the status line manager is null, try to get a valid instance.
		 */
		if(statusLineManager == null) {
			IWorkbenchWindow workbenchWindow = getWorkbenchWindow();
			IWorkbenchPage workbenchPage = getWorkbenchPage(workbenchWindow);
			IWorkbenchPart workbenchPart = getWorkbenchPart(workbenchPage);
			IActionBars actionBars = getActionBars(workbenchPart.getSite());
			statusLineManager = getStatusLineManager(actionBars);
		}
		return statusLineManager;
	}

	/**
	 * Get the status line manager.
	 * 
	 * @return {@link StatusLineManager}
	 * @param actionBars
	 */
	private IStatusLineManager getStatusLineManager(IActionBars actionBars) throws StatusLineManagerException {

		if(actionBars != null) {
			return actionBars.getStatusLineManager();
		} else {
			throw new StatusLineManagerException("IActionBars instance is not available.");
		}
	}

	/**
	 * Returns a valid workbench window instance.
	 * 
	 * @return {@link IWorkbenchWindow}
	 * @throws StatusLineManagerException
	 */
	private IWorkbenchWindow getWorkbenchWindow() throws StatusLineManagerException {

		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(workbenchWindow == null) {
			throw new StatusLineManagerException("IWorkbenchWindow instance is not available.");
		}
		return workbenchWindow;
	}

	/**
	 * Returns a workbench page instance.
	 * 
	 * @param workbenchWindow
	 * @return {@link IWorkbenchPage}
	 * @throws StatusLineManagerException
	 */
	private IWorkbenchPage getWorkbenchPage(IWorkbenchWindow workbenchWindow) throws StatusLineManagerException {

		assert (workbenchWindow != null) : "The workbenchWindow instance must not be null.";
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		if(workbenchPage == null) {
			throw new StatusLineManagerException("IWorkbenchPage instance is not available.");
		}
		return workbenchPage;
	}

	private IWorkbenchPart getWorkbenchPart(IWorkbenchPage workbenchPage) throws StatusLineManagerException {

		assert (workbenchPage != null) : "The workbenchPage instance must not be null.";
		IWorkbenchPart workbenchPart = workbenchPage.getActivePart();
		if(workbenchPart == null) {
			throw new StatusLineManagerException("IWorkbenchPart instance is not available.");
		}
		return workbenchPart;
	}

	/**
	 * Returns the action bars.
	 * 
	 * @param workbenchPartSite
	 * @return {@link IActionBars}
	 * @throws StatusLineManagerException
	 */
	private IActionBars getActionBars(IWorkbenchPartSite workbenchPartSite) throws StatusLineManagerException {

		IActionBars actionBars;
		if(workbenchPartSite instanceof IViewSite) {
			IViewSite viewSite = (IViewSite)workbenchPartSite;
			actionBars = viewSite.getActionBars();
			return actionBars;
		} else if(workbenchPartSite instanceof IEditorSite) {
			IEditorSite editorSite = (IEditorSite)workbenchPartSite;
			actionBars = editorSite.getActionBars();
			return actionBars;
		} else {
			throw new StatusLineManagerException("IViewSite or IEditorSite instances are not available.");
		}
	}
	// ---------------------------------------------------private methods
}
