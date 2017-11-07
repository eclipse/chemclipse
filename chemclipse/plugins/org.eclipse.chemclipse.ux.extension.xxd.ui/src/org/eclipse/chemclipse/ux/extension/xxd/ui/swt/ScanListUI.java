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

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.NominalMSDLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.NominalMSDTableComparator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class ScanListUI extends ExtendedTableViewer {

	private static final String[] TITLES_MSD = { //
			"m/z", //
			"abundance", //
			"parent m/z", //
			"parent resolution", //
			"daughter m/z", //
			"daughter resolution", //
			"collision energy" //
	};
	private static final int[] BOUNDS_MSD = { //
			100, //
			100, //
			120, //
			120, //
			120, //
			120, //
			120 //
	};
	private static final String[] TITLES_CSD = { //
			"retention time (Minutes)", //
			"abundance" //
	};
	private static final int[] BOUNDS_CSD = { //
			150, //
			150 //
	};
	private static final String[] TITLES_WSD = { //
			"wavelength", //
			"abundance" //
	};
	private static final int[] BOUNDS_WSD = { //
			150, //
			150 //
	};
	//
	private ITableLabelProvider labelProvider;
	private ViewerComparator viewerComparator;

	public ScanListUI(Composite parent, int style) {
		super(parent, style);
		labelProvider = new NominalMSDLabelProvider();
		viewerComparator = new NominalMSDTableComparator();
		setDataType(DataType.MSD);
	}

	public void clear() {

		setInput(null);
	}

	public void setDataType(DataType dataType) {

		switch(dataType) {
			case MSD:
				setDataTypeColumns(TITLES_MSD, BOUNDS_MSD, labelProvider, viewerComparator);
				break;
			case CSD:
				setDataTypeColumns(TITLES_CSD, BOUNDS_CSD, labelProvider, viewerComparator);
				break;
			case WSD:
				setDataTypeColumns(TITLES_WSD, BOUNDS_WSD, labelProvider, viewerComparator);
				break;
			default:
				setDataTypeColumns(TITLES_MSD, BOUNDS_MSD, labelProvider, viewerComparator);
				break;
		}
	}

	private void setDataTypeColumns(String[] titles, int[] bounds, ITableLabelProvider labelProvider, ViewerComparator viewerComparator) {

		createColumns(titles, bounds);
		//
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(viewerComparator);
	}
}
