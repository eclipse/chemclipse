/*******************************************************************************
 * Copyright (c) 2014, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.handlers;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.wizards.WizardProcessor;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.process.supplier.batchprocess.ui.provider.BatchJobFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class BatchProcessHandler {

	private static final Logger logger = Logger.getLogger(BatchProcessHandler.class);

	@Execute
	public void execute(@Active Shell shell) {

		WizardProcessor wizard = new WizardProcessor();
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.open();
		/*
		 * The wizard dialog is closed at this point, so the E4 part service can resolve the active window.
		 */
		File batchProcessFile = wizard.getBatchProcessFile();
		if(batchProcessFile != null) {
			ISupplierFileEditorSupport supplierEditorSupport = new BatchJobFileEditorSupport();
			if(!supplierEditorSupport.openEditor(batchProcessFile)) {
				logger.warn("Failed to open editor.");
			}
		}
	}
}
