/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.AbundanceFilter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.AnovaFilter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.CVFilter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.EmptyDataFilter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.RetentionTime2Filter;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.RetentionTimeFilter;
import org.eclipse.jface.wizard.Wizard;

public class FilterWizard extends Wizard {

	private IFilter filter;
	private IFilterWizardPage filterWizardPage;

	public FilterWizard(IFilter filter) {

		super();
		this.filter = filter;
	}

	@Override
	public void addPages() {

		if(filter instanceof AnovaFilter anovaFilter) {
			addPage(filterWizardPage = new FilterAnovaWizardPage(anovaFilter));
		} else if(filter instanceof CVFilter cvFilter) {
			addPage(filterWizardPage = new FilterCVWizardPage(cvFilter));
		} else if(filter instanceof AbundanceFilter abundanceFilter) {
			addPage(filterWizardPage = new FilterAbundanceWizardPage(abundanceFilter));
		} else if(filter instanceof EmptyDataFilter emptyDataFilter) {
			addPage(filterWizardPage = new FilterEmptyDataWizardPage(emptyDataFilter));
		} else if(filter instanceof RetentionTimeFilter retentionTimeFilter) {
			addPage(filterWizardPage = new FilterRetentionTimeWizardPage(retentionTimeFilter));
		} else if(filter instanceof RetentionTime2Filter retentionTime2Filter) {
			addPage(filterWizardPage = new FilterRetentionTime2WizardPage(retentionTime2Filter));
		}
	}

	@Override
	public boolean performFinish() {

		filterWizardPage.update();
		return true;
	}
}
