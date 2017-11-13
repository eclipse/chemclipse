/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ScanTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class ScanListUI extends ExtendedTableViewer {

	private Map<DataType, ITableLabelProvider> labelProviderMap;
	private Map<DataType, ViewerComparator> viewerComparatorMap;

	public ScanListUI(Composite parent, int style) {
		super(parent, style);
		labelProviderMap = new HashMap<DataType, ITableLabelProvider>();
		viewerComparatorMap = new HashMap<DataType, ViewerComparator>();
		setColumns(DataType.MSD_NOMINAL);
	}

	public void setInput(IScan scan) {

		if(scan instanceof IScanMSD) {
			/*
			 * MSD
			 */
			IScanMSD scanMSD = (IScanMSD)scan;
			if(scanMSD.isTandemMS()) {
				setColumns(DataType.MSD_TANDEM);
			} else {
				if(scanMSD.isHighResolutionMS()) {
					setColumns(DataType.MSD_HIGHRES);
				} else {
					setColumns(DataType.MSD_NOMINAL);
				}
			}
			setInput(scanMSD.getIons());
		} else if(scan instanceof IScanCSD) {
			/*
			 * CSD
			 */
			setColumns(DataType.CSD);
			IScanCSD scanCSD = (IScanCSD)scan;
			List<IScanCSD> list = new ArrayList<IScanCSD>();
			list.add(scanCSD);
			setInput(list);
		} else if(scan instanceof IScanWSD) {
			/*
			 * WSD
			 */
			setColumns(DataType.WSD);
			IScanWSD scanWSD = (IScanWSD)scan;
			setInput(scanWSD.getScanSignals());
		} else {
			super.setInput(null);
		}
	}

	private void setColumns(DataType dataType) {

		String[] titles = getTitles(dataType);
		int[] bounds = getBounds(dataType);
		ITableLabelProvider labelProvider = getTableLabelProvider(dataType);
		ViewerComparator viewerComparator = getViewerComparator(dataType);
		//
		createColumns(titles, bounds);
		//
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(viewerComparator);
	}

	private String[] getTitles(DataType dataType) {

		String[] titles;
		switch(dataType) {
			case MSD_NOMINAL:
				titles = ScanLabelProvider.TITLES_MSD_NOMINAL;
				break;
			case MSD_TANDEM:
				titles = ScanLabelProvider.TITLES_MSD_TANDEM;
				break;
			case MSD_HIGHRES:
				titles = ScanLabelProvider.TITLES_MSD_HIGHRES;
				break;
			case CSD:
				titles = ScanLabelProvider.TITLES_CSD;
				break;
			case WSD:
				titles = ScanLabelProvider.TITLES_WSD;
				break;
			default:
				titles = ScanLabelProvider.TITLES_EMPTY;
		}
		return titles;
	}

	private int[] getBounds(DataType dataType) {

		int[] bounds;
		switch(dataType) {
			case MSD_NOMINAL:
				bounds = ScanLabelProvider.BOUNDS_MSD_NOMINAL;
				break;
			case MSD_TANDEM:
				bounds = ScanLabelProvider.BOUNDS_MSD_TANDEM;
				break;
			case MSD_HIGHRES:
				bounds = ScanLabelProvider.BOUNDS_MSD_HIGHRES;
				break;
			case CSD:
				bounds = ScanLabelProvider.BOUNDS_CSD;
				break;
			case WSD:
				bounds = ScanLabelProvider.BOUNDS_WSD;
				break;
			default:
				bounds = ScanLabelProvider.BOUNDS_EMPTY;
		}
		return bounds;
	}

	private ITableLabelProvider getTableLabelProvider(DataType dataType) {

		ITableLabelProvider tableLableProvider = labelProviderMap.get(dataType);
		if(tableLableProvider == null) {
			tableLableProvider = new ScanLabelProvider(dataType);
			labelProviderMap.put(dataType, tableLableProvider);
		}
		return tableLableProvider;
	}

	private ViewerComparator getViewerComparator(DataType dataType) {

		ViewerComparator viewerComparator = viewerComparatorMap.get(dataType);
		if(viewerComparator == null) {
			viewerComparator = new ScanTableComparator(dataType);
			viewerComparatorMap.put(dataType, viewerComparator);
		}
		return viewerComparator;
	}
}
