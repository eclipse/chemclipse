/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.handlers;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.evaluation.WizardSingleChromatogramEvaluation;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

public class ChromatogramEvaluationWizardHandler {

	@Execute
	public void execute() {

		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), new WizardSingleChromatogramEvaluation());
		wizardDialog.open();
	}
}
