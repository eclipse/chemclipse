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
package org.eclipse.chemclipse.swt.ui.components.peaks;

import java.util.List;

import org.eclipse.chemclipse.model.support.PeakQuantitations;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.internal.provider.PeakQuantitationListLabelProvider;
import org.eclipse.chemclipse.swt.ui.internal.provider.PeakQuantitationListTableComparator;
import org.eclipse.swt.widgets.Composite;

public class PeakQuantitationListUI extends ExtendedTableViewer {

	private static final int DEFAULT_COLUMN_WIDTH = 100;
	private String[] titles = {"RT (Minutes)", "Area"};
	private int bounds[] = {DEFAULT_COLUMN_WIDTH, DEFAULT_COLUMN_WIDTH};

	public PeakQuantitationListUI(Composite parent, int style) {
		super(parent, style);
		setColumns(titles, bounds);
	}

	public void update(PeakQuantitations peakQuantitations) {

		getTable().clearAll();
		if(peakQuantitations != null) {
			List<String> quantitationTitles = peakQuantitations.getTitles();
			int length = quantitationTitles.size();
			String[] titles;
			int[] bounds;
			if(length == 0) {
				titles = this.titles;
				bounds = this.bounds;
			} else {
				titles = new String[length];
				bounds = new int[length];
				//
				for(int i = 0; i < length; i++) {
					titles[i] = quantitationTitles.get(i);
					bounds[i] = DEFAULT_COLUMN_WIDTH;
				}
			}
			/*
			 * Set the columns
			 */
			setColumns(titles, bounds);
			setInput(peakQuantitations.getQuantitationEntries());
		} else {
			setInput(null);
		}
	}

	public void clear() {

		setInput(null);
	}

	private void setColumns(String[] titles, int bounds[]) {

		createColumns(titles, bounds);
		//
		setLabelProvider(new PeakQuantitationListLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new PeakQuantitationListTableComparator());
	}
}
