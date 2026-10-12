/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.process.supplier.batchprocess.ui.wizards;

import java.io.File;

import org.eclipse.chemclipse.support.ui.wizards.WizardElements;

public class BatchProcessWizardElements extends WizardElements {

	private File batchProcessFile;

	public File getBatchProcessFile() {

		return batchProcessFile;
	}

	public void setBatchProcessFile(File batchProcessFile) {

		this.batchProcessFile = batchProcessFile;
	}
}
