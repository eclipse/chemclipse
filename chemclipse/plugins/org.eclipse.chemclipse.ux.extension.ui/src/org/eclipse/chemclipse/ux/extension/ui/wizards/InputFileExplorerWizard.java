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
package org.eclipse.chemclipse.ux.extension.ui.wizards;

import java.util.List;
import java.util.StringJoiner;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;

public class InputFileExplorerWizard extends InputEntriesWizard {

	public InputFileExplorerWizard(IChromatogramWizardElements chromatogramWizardElements, List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {
		super(chromatogramWizardElements);
		init(supplierFileIdentifierList);
	}

	public InputFileExplorerWizard(String title, String description, List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {
		super();
		init(supplierFileIdentifierList);
	}

	private void init(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		init(getTitle(supplierFileIdentifierList), getDesctiption(supplierFileIdentifierList), getBaseLabelProvider(supplierFileIdentifierList), getContentProvider(supplierFileIdentifierList));
	}

	private String getTitle(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		StringJoiner sj = new StringJoiner(", ", "", " Input Files");
		for(ISupplierFileIdentifier iSupplierFileIdentifier : supplierFileIdentifierList) {
			sj.add(iSupplierFileIdentifier.getType());
		}
		return sj.toString();
	}

	private String getDesctiption(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		StringJoiner sj = new StringJoiner(", ", "This wizard lets you select several ", " input files.");
		for(ISupplierFileIdentifier iSupplierFileIdentifier : supplierFileIdentifierList) {
			sj.add(iSupplierFileIdentifier.getType());
		}
		return sj.toString();
	}

	private IBaseLabelProvider getBaseLabelProvider(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		return new SupplierFileExplorerLabelProvider(supplierFileIdentifierList);
	}

	private IContentProvider getContentProvider(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		return new SupplierFileExplorerContentProvider(supplierFileIdentifierList);
	}
}
