/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.msd.ui.provider.PeakFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.provider.PeakFileExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.wizards.InputEntriesWizard;

public class PeakInputEntriesWizard extends InputEntriesWizard {

	public PeakInputEntriesWizard() {
		super();
		init("Peak Input Files", "This wizard lets you select several peak input files.", new PeakFileExplorerLabelProvider(), new PeakFileExplorerContentProvider());
	}

	public PeakInputEntriesWizard(IChromatogramWizardElements chromatogramWizardElements) {
		super(chromatogramWizardElements);
		init("Peak Input Files", "This wizard lets you select several peak input files.", new PeakFileExplorerLabelProvider(), new PeakFileExplorerContentProvider());
	}
}
