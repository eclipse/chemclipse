/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ScanLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

	public static final String NO_VALUE = "n.a.";
	//
	public static final String INTENSITY = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_Y_AXIS_INTENSITY);
	public static final String RELATIVE_INTENSITY = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_Y_AXIS_RELATIVE_INTENSITY);
	public static final String ION = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_MZ);
	public static final String WAVELENGTH = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_WAVELENGTH);
	public static final String MINUTES = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_MINUTES);
	public static final String PARENT_ION = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_PARENT_MZ);
	public static final String PARENT_RESOLUTION = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_PARENT_RESOLUTION);
	public static final String DAUGHTER_ION = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_DAUGHTER_MZ);
	public static final String DAUGHTER_RESOLUTION = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_DAUGHTER_RESOLUTION);
	public static final String COLLISION_ENERGY = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_TITLE_X_AXIS_COLLISION_ENERGY);
	//
	public static final String[] TITLES_MSD_NOMINAL = {ION, INTENSITY, RELATIVE_INTENSITY};
	public static final int[] BOUNDS_MSD_NOMINAL = {150, 150, 150};
	public static final String[] TITLES_MSD_TANDEM = {ION, INTENSITY, RELATIVE_INTENSITY, PARENT_ION, PARENT_RESOLUTION, DAUGHTER_ION, DAUGHTER_RESOLUTION, COLLISION_ENERGY};
	public static final int[] BOUNDS_MSD_TANDEM = {100, 100, 100, 120, 120, 120, 120, 120};
	public static final String[] TITLES_MSD_HIGHRES = {ION, INTENSITY, RELATIVE_INTENSITY};
	public static final int[] BOUNDS_MSD_HIGHRES = {150, 150, 150};
	public static final String[] TITLES_CSD = {MINUTES, INTENSITY, RELATIVE_INTENSITY};
	public static final int[] BOUNDS_CSD = {150, 150, 150};
	public static final String[] TITLES_WSD = {WAVELENGTH, INTENSITY, RELATIVE_INTENSITY};
	public static final int[] BOUNDS_WSD = {150, 150, 150};
	public static final String[] TITLES_EMPTY = {NO_VALUE};
	public static final int[] BOUNDS_EMPTY = {150};
	//
	private DataType dataType;
	//
	private DecimalFormat dfNominalMSD;
	private DecimalFormat dfTandemMSD;
	private DecimalFormat dfHighResMSD;
	private DecimalFormat dfCSD;
	private DecimalFormat dfWSD;
	//
	private DecimalFormat dfIntensity;
	private DecimalFormat dfRelativeIntensity;
	//
	private double relativeIntensityFactor = 0.0d;

	public ScanLabelProvider(DataType dataType) {
		this.dataType = dataType;
		dfNominalMSD = ValueFormat.getDecimalFormatEnglish("0.0");
		dfTandemMSD = ValueFormat.getDecimalFormatEnglish("0.0");
		dfHighResMSD = ValueFormat.getDecimalFormatEnglish("0.000###");
		dfCSD = ValueFormat.getDecimalFormatEnglish("0.0000");
		dfWSD = ValueFormat.getDecimalFormatEnglish("0.0");
		dfIntensity = ValueFormat.getDecimalFormatEnglish("0.0###");
		dfRelativeIntensity = ValueFormat.getDecimalFormatEnglish("0.000");
	}

	public void setTotalIntensity(float totalIntensity) {

		if(totalIntensity != 0) {
			relativeIntensityFactor = 100.0d / totalIntensity;
		} else {
			relativeIntensityFactor = 0.0d;
		}
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
				case 0:
					text = dfNominalMSD.format(ion.getIon());
					break;
				case 1:
					text = dfIntensity.format(ion.getAbundance());
					break;
				case 2:
					text = dfRelativeIntensity.format(relativeIntensityFactor * ion.getAbundance());
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
					String mz = dfTandemMSD.format(ion.getIon());
					text = (ionTransition == null) ? mz : Integer.toString((int)ionTransition.getQ1StartIon()) + " > " + mz;
					break;
				case 1:
					text = dfIntensity.format(ion.getAbundance());
					break;
				case 2:
					text = dfRelativeIntensity.format(relativeIntensityFactor * ion.getAbundance());
					break;
				case 3: // parent m/z
					text = (ionTransition == null) ? "" : dfTandemMSD.format(ionTransition.getQ1Ion());
					break;
				case 4: // parent resolution
					text = (ionTransition == null) ? "" : dfTandemMSD.format(ionTransition.getQ1Resolution());
					break;
				case 5: // daughter m/z
					text = (ionTransition == null) ? "" : dfTandemMSD.format(ionTransition.getQ3Ion());
					break;
				case 6: // daughter resolution
					text = (ionTransition == null) ? "" : dfTandemMSD.format(ionTransition.getQ3Resolution());
					break;
				case 7: // collision energy
					text = (ionTransition == null) ? "" : dfTandemMSD.format(ionTransition.getCollisionEnergy());
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
				case 0:
					text = dfHighResMSD.format(ion.getIon());
					break;
				case 1:
					text = dfIntensity.format(ion.getAbundance());
					break;
				case 2:
					text = dfRelativeIntensity.format(relativeIntensityFactor * ion.getAbundance());
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
				case 0:
					text = dfCSD.format(scanCSD.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 1:
					text = dfIntensity.format(scanCSD.getTotalSignal());
					break;
				case 2:
					text = dfRelativeIntensity.format(relativeIntensityFactor * scanCSD.getTotalSignal());
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
				case 0:
					text = dfWSD.format(scanSignal.getWavelength());
					break;
				case 1:
					text = dfIntensity.format(scanSignal.getAbundance());
					break;
				case 2:
					text = dfRelativeIntensity.format(relativeIntensityFactor * scanSignal.getAbundance());
					break;
				default:
					text = NO_VALUE;
			}
		}
		return text;
	}
}
