/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.AbundanceFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.AnovaFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.CVFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.jface.wizard.Wizard;

public class FilterWizard extends Wizard {

	private IFilter filter;

	public FilterWizard(IFilter filter) {
		super();
		this.filter = filter;
	}

	@Override
	public void addPages() {

		if(filter instanceof AnovaFilter) {
			addPage(new FilterAnovaWizardPage((AnovaFilter)filter));
		} else if(filter instanceof CVFilter) {
			addPage(new FilterCVWizardPage((CVFilter)filter));
		} else if(filter instanceof AbundanceFilter) {
			addPage(new FilterAbundanceWizardPage((AbundanceFilter)filter));
		}
	}

	@Override
	public boolean performFinish() {

		return true;
	}
}
