/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.StandardsReader;
import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;

public class RetentionIndexWizardElements extends ChromatogramWizardElements implements IRetentionIndexWizardElements {

	private String[] availableStandards;

	public RetentionIndexWizardElements() {
		StandardsReader standardsReader = new StandardsReader();
		availableStandards = standardsReader.getAvailableStandards();
	}

	@Override
	public String[] getAvailableStandards() {

		return availableStandards;
	}
}