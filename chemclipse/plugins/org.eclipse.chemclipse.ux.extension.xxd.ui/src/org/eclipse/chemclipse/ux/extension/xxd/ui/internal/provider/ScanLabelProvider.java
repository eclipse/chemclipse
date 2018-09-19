/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ScanLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

	public static final String NO_VALUE = "n.a.";
	public static final String ABUNDANCE = "abundance";
	//
	public static final String[] TITLES_MSD_NOMINAL = {"m/z", ABUNDANCE};
	public static final int[] BOUNDS_MSD_NOMINAL = {150, 150};
	public static final String[] TITLES_MSD_TANDEM = {"m/z", ABUNDANCE, "parent m/z", "parent resolution", "daughter m/z", "daughter resolution", "collision energy"};
	public static final int[] BOUNDS_MSD_TANDEM = {100, 100, 120, 120, 120, 120, 120};
	public static final String[] TITLES_MSD_HIGHRES = {"m/z", ABUNDANCE};
	public static final int[] BOUNDS_MSD_HIGHRES = {150, 150};
	public static final String[] TITLES_CSD = {"retention time (minutes)", ABUNDANCE};
	public static final int[] BOUNDS_CSD = {150, 150};
	public static final String[] TITLES_WSD = {"wavelength", ABUNDANCE};
	public static final int[] BOUNDS_WSD = {150, 150};
	public static final String[] TITLES_EMPTY = {NO_VALUE};
	public static final int[] BOUNDS_EMPTY = {150};
	//
	private DataType dataType;
	//
	private DecimalFormat decimalFormatNominalMSD;
	private DecimalFormat decimalFormatTandemMSD;
	private DecimalFormat decimalFormatHighResMSD;
	private DecimalFormat decimalFormatCSD;
	private DecimalFormat decimalFormatWSD;
	//
	private DecimalFormat decimalFormatIntensity;

	public ScanLabelProvider(DataType dataType) {
		this.dataType = dataType;
		decimalFormatNominalMSD = ValueFormat.getDecimalFormatEnglish("0.0");
		decimalFormatTandemMSD = ValueFormat.getDecimalFormatEnglish("0.0");
		decimalFormatHighResMSD = ValueFormat.getDecimalFormatEnglish("0.000###");
		decimalFormatCSD = ValueFormat.getDecimalFormatEnglish("0.0000");
		decimalFormatWSD = ValueFormat.getDecimalFormatEnglish("0.0");
		decimalFormatIntensity = ValueFormat.getDecimalFormatEnglish("0.0###");
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ION, IApplicationImage.SIZE_16x16);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		switch(dataType) {
			case MSD_NOMINAL:
				text = getNominalMSD(element, columnIndex);
				break;
			case MSD_TANDEM:
				text = getTandemMSD(element, columnIndex);
				break;
			case MSD_HIGHRES:
				text = getHighResolutionMSD(element, columnIndex);
				break;
			case CSD:
				text = getCSD(element, columnIndex);
				break;
			case WSD:
				text = getWSD(element, columnIndex);
				break;
			default:
				text = NO_VALUE;
		}
		return text;
	}

	private String getNominalMSD(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IIon) {
			IIon ion = (IIon)element;
			switch(columnIndex) {
				case 0: // m/z
					text = decimalFormatNominalMSD.format(ion.getIon());
					break;
				case 1: // abundance
					text = decimalFormatIntensity.format(ion.getAbundance());
					break;
				default:
					text = NO_VALUE;
			}
		}
		return text;
	}

	private String getTandemMSD(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IIon) {
			IIon ion = (IIon)element;
			IIonTransition ionTransition = ion.getIonTransition();
			switch(columnIndex) {
				case 0: // m/z (normal 28.3 or with Transition 128 > 78.4)
					String mz = decimalFormatTandemMSD.format(ion.getIon());
					text = (ionTransition == null) ? mz : Integer.toString((int)ionTransition.getQ1StartIon()) + " > " + mz;
					break;
				case 1: // abundance
					text = decimalFormatIntensity.format(ion.getAbundance());
					break;
				case 2: // parent m/z
					text = (ionTransition == null) ? "" : decimalFormatTandemMSD.format(ionTransition.getQ1Ion());
					break;
				case 3: // parent resolution
					text = (ionTransition == null) ? "" : decimalFormatTandemMSD.format(ionTransition.getQ1Resolution());
					break;
				case 4: // daughter m/z
					text = (ionTransition == null) ? "" : decimalFormatTandemMSD.format(ionTransition.getQ3Ion());
					break;
				case 5: // daughter resolution
					text = (ionTransition == null) ? "" : decimalFormatTandemMSD.format(ionTransition.getQ3Resolution());
					break;
				case 6: // collision energy
					text = (ionTransition == null) ? "" : decimalFormatTandemMSD.format(ionTransition.getCollisionEnergy());
					break;
				default:
					text = NO_VALUE;
			}
		}
		return text;
	}

	private String getHighResolutionMSD(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IIon) {
			IIon ion = (IIon)element;
			switch(columnIndex) {
				case 0: // m/z
					text = decimalFormatHighResMSD.format(ion.getIon());
					break;
				case 1: // abundance
					text = decimalFormatIntensity.format(ion.getAbundance());
					break;
				default:
					text = NO_VALUE;
			}
		}
		return text;
	}

	private String getCSD(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)element;
			switch(columnIndex) {
				case 0: // retention time
					text = decimalFormatCSD.format(scanCSD.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 1: // abundance
					text = decimalFormatIntensity.format(scanCSD.getTotalSignal());
					break;
				default:
					text = NO_VALUE;
			}
		}
		return text;
	}

	private String getWSD(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IScanSignalWSD) {
			IScanSignalWSD scanSignal = (IScanSignalWSD)element;
			switch(columnIndex) {
				case 0: // m/z
					text = decimalFormatWSD.format(scanSignal.getWavelength());
					break;
				case 1: // abundance
					text = decimalFormatIntensity.format(scanSignal.getAbundance());
					break;
				default:
					text = NO_VALUE;
			}
		}
		return text;
	}
}
