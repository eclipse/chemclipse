/*******************************************************************************
 * Copyright (c) 2008, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - reimplemented using Eclipse API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.handlers;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.progress.core.InfoType;
import org.eclipse.chemclipse.progress.core.StatusLineLogger;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;

import jakarta.inject.Named;

public class RedoOperationHandler {

	private static final Logger logger = Logger.getLogger(RedoOperationHandler.class);

	@CanExecute
	boolean canExecute() {

		return OperationHistoryFactory.getOperationHistory().canRedo(UndoContextFactory.getUndoContext());
	}

	@Execute
	public void execute(UISynchronize uiSynchronize, final @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		uiSynchronize.syncExec(() -> {

			Cursor cursor = shell.getCursor();
			try {
				Cursor cursorNew = shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
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
			}
		});
	}
}
