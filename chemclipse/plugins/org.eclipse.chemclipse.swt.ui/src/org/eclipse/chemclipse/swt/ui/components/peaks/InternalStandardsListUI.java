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
package org.eclipse.chemclipse.swt.ui.components.peaks;

import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.internal.provider.InternalStandardListLabelProvider;
import org.eclipse.chemclipse.swt.ui.internal.provider.InternalStandardListTableComparator;
import org.eclipse.swt.widgets.Composite;

public class InternalStandardsListUI extends ExtendedTableViewer {

	private String[] titles = {"Content", "Response Factor", "Unit"};
	private int bounds[] = {120, 120, 120};

	public InternalStandardsListUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public InternalStandardsListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void update(List<IInternalStandard> internalStandards, boolean forceReload) {

		setInput(internalStandards);
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new InternalStandardListLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new InternalStandardListTableComparator());
		setEditingSupport();
	}

	private void setEditingSupport() {

		// TODO implement
	}
}
