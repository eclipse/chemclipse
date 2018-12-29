/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantPeaksLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.QuantPeaksTableComparator;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class QuantPeakListUI extends ExtendedTableViewer {

	private String[] titles = QuantPeaksLabelProvider.TITLES;
	private int[] bounds = QuantPeaksLabelProvider.BOUNDS;
	private IBaseLabelProvider labelProvider = new QuantPeaksLabelProvider();
	private ViewerComparator tableComparator = new QuantPeaksTableComparator();

	public QuantPeakListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void clear() {

		setInput(null);
	}

	private void createColumns() {

		createColumns(titles, bounds);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
	}
}
