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
import org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.wizards.InputEntriesWizard;

public class ChromatogramInputEntriesWizard2 extends InputEntriesWizard {

	public ChromatogramInputEntriesWizard2() {
		super();
		init("Chromatogram MSD Input Files", "This wizard lets you select several chormatogram MSD input files.", new SupplierFileExplorerLabelProvider(ChromatogramSupport.getInstanceIdentifier()), new SupplierFileExplorerContentProvider(ChromatogramSupport.getInstanceIdentifier()));
	}

	public ChromatogramInputEntriesWizard2(IChromatogramWizardElements chromatogramWizardElements) {
		super(chromatogramWizardElements);
		init("Chromatogram MSD Input Files", "This wizard lets you select several chormatogram MSD input files.", new SupplierFileExplorerLabelProvider(ChromatogramSupport.getInstanceIdentifier()), new SupplierFileExplorerContentProvider(ChromatogramSupport.getInstanceIdentifier()));
	}
}
