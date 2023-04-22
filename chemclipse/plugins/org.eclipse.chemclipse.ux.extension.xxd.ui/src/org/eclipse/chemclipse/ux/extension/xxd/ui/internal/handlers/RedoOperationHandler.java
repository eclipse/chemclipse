/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/eplv10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - reimplemented using Eclipse API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import javax.inject.Named;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.rcp.app.ui.console.MessageConsoleAppender;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;

public class RedoOperationHandler {

	private static final Logger logger = Logger.getLogger(RedoOperationHandler.class);

	@CanExecute
	boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		return OperationHistoryFactory.getOperationHistory().canRedo(UndoContextFactory.getUndoContext());
	}

	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, @Named(IServiceConstants.ACTIVE_PART) MPart part) {

		uiSynchronize.syncExec(new Runnable() {

			@Override
			public void run() {

				Cursor cursor = shell.getCursor();
				try {
					Cursor cursorNew = DisplayUtils.getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
					shell.setCursor(cursorNew);
					/*
					 * Undo the operation.
					 */
					IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
					IUndoContext undoContext = UndoContextFactory.getUndoContext();
					operationHistory.redo(undoContext, null, null);
				} catch(ExecutionException e) {
					logger.warn(e);
					StatusLineLogger.setInfo(InfoType.ERROR_MESSAGE, ExtensionMessages.redoOperationFailed);
				} finally {
					shell.setCursor(cursor);
					StatusLineLogger.setInfo(InfoType.MESSAGE, ExtensionMessages.redoOperationFinished);
					MessageConsoleAppender.printDone(ExtensionMessages.redoOperationFinished);
				}
			}
		});
	}
}
