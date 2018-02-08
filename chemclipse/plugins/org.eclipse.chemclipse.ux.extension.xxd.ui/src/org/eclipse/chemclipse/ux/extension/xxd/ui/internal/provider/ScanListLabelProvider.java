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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.swt.graphics.Image;

public class ScanListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = "Name";
	public static final String RETENTION_TIME = "Retention Time";
	public static final String RELATIVE_RETENTION_TIME = "Relative Retention Time";
	public static final String RETENTION_INDEX = "Retention Index";
	public static final String BASE_PEAK = "Base Peak";
	public static final String BASE_PEAK_ABUNDANCE = "Base Peak Abundance";
	public static final String NUMBER_OF_IONS = "Number of Ions";
	public static final String CAS = "CAS";
	public static final String MW = "MW";
	public static final String FORMULA = "Formula";
	public static final String SMILES = "SMILES";
	public static final String INCHI = "InChI";
	public static final String REFERENCE_IDENTIFIER = "Reference Identifier";
	public static final String COMMENTS = "Comments";
	//
	private ScanDataSupport scanDataSupport = new ScanDataSupport();
	//
	public static String[] TITLES = {//
			NAME, //
			RETENTION_TIME, //
			RELATIVE_RETENTION_TIME, //
			RETENTION_INDEX, //
			BASE_PEAK, //
			BASE_PEAK_ABUNDANCE, //
			NUMBER_OF_IONS, //
			CAS, //
			MW, //
			FORMULA, //
			SMILES, //
			INCHI, //
			REFERENCE_IDENTIFIER, //
			COMMENTS//
	};
	//
	public static int[] BOUNDS = {//
			300, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		return text;
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
		return image;
	}
}
