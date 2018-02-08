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

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
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
		if(element instanceof IScan) {
			IScan scan = (IScan)element;
			ILibraryInformation libraryInformation = scanDataSupport.getLibraryInformation(scan);
			//
			switch(columnIndex) {
				case 0:
					text = (libraryInformation != null) ? libraryInformation.getName() : "";
					break;
				case 1:
					text = decimalFormat.format(scan.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 2:
					text = decimalFormat.format(scan.getRelativeRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 3:
					text = decimalFormat.format(scan.getRetentionIndex());
					break;
				case 4:
					text = (libraryInformation != null) ? libraryInformation.getCasNumber() : "";
					break;
				case 5:
					text = (libraryInformation != null) ? decimalFormat.format(libraryInformation.getMolWeight()) : "";
					break;
				case 6:
					text = (libraryInformation != null) ? libraryInformation.getFormula() : "";
					break;
				case 7:
					text = (libraryInformation != null) ? libraryInformation.getSmiles() : "";
					break;
				case 8:
					text = (libraryInformation != null) ? libraryInformation.getInChI() : "";
					break;
				case 9:
					text = (libraryInformation != null) ? libraryInformation.getReferenceIdentifier() : "";
					break;
				case 10:
					text = (libraryInformation != null) ? libraryInformation.getComments() : "";
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
		return image;
	}
}
