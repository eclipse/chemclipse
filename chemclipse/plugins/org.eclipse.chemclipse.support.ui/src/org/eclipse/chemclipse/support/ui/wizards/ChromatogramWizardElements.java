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
package org.eclipse.chemclipse.support.ui.wizards;

import java.util.ArrayList;
import java.util.List;

public class ChromatogramWizardElements extends WizardElements implements IChromatogramWizardElements {

	private List<String> selectedChromatograms;

	public ChromatogramWizardElements() {
		selectedChromatograms = new ArrayList<String>();
	}

	@Override
	public void addElements(IChromatogramWizardElements chromatogramWizardElements) {

		if(chromatogramWizardElements != null) {
			selectedChromatograms.addAll(chromatogramWizardElements.getSelectedChromatograms());
		}
	}

	@Override
	public List<String> getSelectedChromatograms() {

		return selectedChromatograms;
	}

	@Override
	public void clearSelectedChromatograms() {

		selectedChromatograms.clear();
	}

	@Override
	public void addSelectedChromatogram(String selectedChromatogram) {

		selectedChromatograms.add(selectedChromatogram);
	}
}
