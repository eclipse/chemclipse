/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.RetentionTimeFilter;
import org.eclipse.jface.wizard.Wizard;

public class FiltersWizard extends Wizard {

	final protected static int FITER_EMPTY_DATA = 4;
	final protected static int FITER_TYPE_ABUNDANCE = 3;
	final protected static int FITER_TYPE_ANOVA = 1;
	final protected static int FITER_TYPE_CV = 2;
	final protected static int FITER_TYPE_RETENTION_TIME = 5;
	private IFilter filter;
	private FiltersWizardPage filtersWizardPage;

	public FiltersWizard() {

		super();
	}

	@Override
	public void addPages() {

		filtersWizardPage = new FiltersWizardPage("Select Filter");
		addPage(filtersWizardPage);
	}

	public IFilter getFilter() {

		return filter;
	}

	public IFilter getFilterType() {

		IFilter filter = null;
		switch(filtersWizardPage.getFilterType()) {
			case FiltersWizard.FITER_TYPE_ANOVA:
				filter = new AnovaFilter();
				break;
			case FiltersWizard.FITER_TYPE_CV:
				filter = new CVFilter();
				break;
			case FiltersWizard.FITER_TYPE_ABUNDANCE:
				filter = new AbundanceFilter();
				break;
			case FiltersWizard.FITER_EMPTY_DATA:
				filter = new EmptyDataFilter();
				break;
			case FiltersWizard.FITER_TYPE_RETENTION_TIME:
				filter = new RetentionTimeFilter();
		}
		return filter;
	}

	@Override
	public boolean performFinish() {

		return true;
	}

	public void setFilter(IFilter filter) {

		this.filter = filter;
	}
}
