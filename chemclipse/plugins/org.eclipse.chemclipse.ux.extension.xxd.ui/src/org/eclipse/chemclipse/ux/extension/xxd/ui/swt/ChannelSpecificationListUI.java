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

import org.eclipse.chemclipse.pcr.model.core.IChannelSpecification;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ChannelSpecificationLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ChannelSpecificationListFilter;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ChannelSpecificationTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.HeaderDataTableComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

public class ChannelSpecificationListUI extends ExtendedTableViewer {

	private ChannelSpecificationTableComparator tableComparator = new ChannelSpecificationTableComparator();
	private ChannelSpecificationListFilter listFilter = new ChannelSpecificationListFilter();
	private ChannelSpecificationLabelProvider labelProvider = new ChannelSpecificationLabelProvider();
	private String[] titles = ChannelSpecificationLabelProvider.TITLES;
	private int[] bounds = ChannelSpecificationLabelProvider.BOUNDS;
	//
	private IChannelSpecification channelSpecification = null;

	public ChannelSpecificationListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void setInput(IChannelSpecification channelSpecification) {

		this.channelSpecification = channelSpecification;
		if(channelSpecification != null) {
			super.setInput(channelSpecification.getData());
		} else {
			clear();
		}
	}

	public void clear() {

		super.setInput(null);
	}

	public IChannelSpecification getChannelSpecification() {

		return channelSpecification;
	}

	public void sortTable() {

		int column = 0;
		int sortOrder = HeaderDataTableComparator.DESCENDING;
		//
		tableComparator.setColumn(column);
		tableComparator.setDirection(sortOrder);
		refresh();
	}

	private void createColumns() {

		createColumns(titles, bounds);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(tableComparator);
		setFilters(new ViewerFilter[]{listFilter});
	}
}
