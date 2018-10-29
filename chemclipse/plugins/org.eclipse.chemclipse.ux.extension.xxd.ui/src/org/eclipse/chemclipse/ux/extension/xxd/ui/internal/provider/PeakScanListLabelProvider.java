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
import java.util.Set;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class PeakScanListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String ACTIVE_FOR_ANALYSIS = "Active for Analysis";
	public static final String RT = "RT";
	public static final String CLASSIFIER = "Classifier";
	//
	public static final String PEAK = "PEAK";
	public static final String SCAN = "SCAN";
	//
	private double chromatogramPeakArea = 0.0d;
	private PeakDataSupport peakDataSupport = new PeakDataSupport();
	private ScanDataSupport scanDataSupport = new ScanDataSupport();
	//
	public static final String[] TITLES = { //
			ACTIVE_FOR_ANALYSIS, //
			"Type", //
			"RT (min)", //
			"RRT (min)", //
			"RI", //
			"Area", //
			"Start RT", //
			"Stop RT", //
			"Width", //
			"Scan# at Peak Maximum", //
			"S/N", //
			"Leading", //
			"Tailing", //
			"Model Description", //
			"Suggested Components", //
			"Name", //
			"Area Percent", //
			"Quantifier", //
			CLASSIFIER //
	};
	//
	public static final int[] BOUNDS = { //
			30, //
			80, //
			100, //
			100, //
			60, //
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
			100, //
			100 //
	};

	public void setChromatogramPeakArea(double chromatogramPeakArea) {

		this.chromatogramPeakArea = chromatogramPeakArea;
	}

	@Override
	public Color getBackground(final Object element) {

		if(element instanceof IPeak) {
			IPeak peak = (IPeak)element;
			Set<IIdentificationTarget> peakTargets = peak.getTargets();
			for(IIdentificationTarget peakTarget : peakTargets) {
				IComparisonResult comparisonResult = peakTarget.getComparisonResult();
				if(comparisonResult instanceof IPeakComparisonResult) {
					IPeakComparisonResult peakComparisonResult = (IPeakComparisonResult)comparisonResult;
					if(peakComparisonResult.isMarkerPeak()) {
						return new Color(DisplayUtils.getDisplay(), 255, 140, 0);
					}
				}
			}
		}
		return super.getBackground(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			if(element instanceof IPeak) {
				IPeak peak = (IPeak)element;
				if(peak.isActiveForAnalysis()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
				}
			} else if(element instanceof IScan) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED_INACTIVE, IApplicationImage.SIZE_16x16);
			}
		} else if(columnIndex == 1) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IPeak || element instanceof IScan) {
			/*
			 * Show peak and scan data.
			 */
			if(element instanceof IPeak) {
				IPeak peak = (IPeak)element;
				text = getPeakText(peak, columnIndex);
			} else if(element instanceof IScan) {
				IScan scan = (IScan)element;
				text = getScanText(scan, columnIndex);
			}
		}
		return text;
	}

	private String getPeakText(IPeak peak, int columnIndex) {

		IPeakModel peakModel = peak.getPeakModel();
		DecimalFormat decimalFormat = getDecimalFormat();
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String text = "";
		//
		switch(columnIndex) {
			case 0:
				text = "";
				break;
			case 1:
				text = PEAK;
				break;
			case 2:
				text = decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				break;
			case 3:
				text = decimalFormat.format(peakModel.getPeakMaximum().getRelativeRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				break;
			case 4:
				boolean showRetentionIndexWithoutDecimals = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
				if(showRetentionIndexWithoutDecimals) {
					text = Integer.toString((int)peakModel.getPeakMaximum().getRetentionIndex());
				} else {
					text = decimalFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
				}
				break;
			case 5:
				boolean showAreaWithoutDecimals = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS);
				if(showAreaWithoutDecimals) {
					text = Integer.toString((int)peak.getIntegratedArea());
				} else {
					text = decimalFormat.format(peak.getIntegratedArea());
				}
				break;
			case 6:
				text = decimalFormat.format(peakModel.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				break;
			case 7:
				text = decimalFormat.format(peakModel.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				break;
			case 8:
				text = decimalFormat.format(peakModel.getWidthByInflectionPoints() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				break;
			case 9:
			case 10:
				if(peak instanceof IChromatogramPeakMSD) {
					IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)peak;
					switch(columnIndex) {
						case 9:
							text = Integer.toString(chromatogramPeak.getScanMax());
							break;
						case 10:
							text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
							break;
					}
				} else if(peak instanceof IChromatogramPeakCSD) {
					IChromatogramPeakCSD chromatogramPeak = (IChromatogramPeakCSD)peak;
					switch(columnIndex) {
						case 9:
							text = Integer.toString(chromatogramPeak.getScanMax());
							break;
						case 10:
							text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
							break;
					}
				} else if(peak instanceof IChromatogramPeakWSD) {
					IChromatogramPeakWSD chromatogramPeak = (IChromatogramPeakWSD)peak;
					switch(columnIndex) {
						case 9:
							text = Integer.toString(chromatogramPeak.getScanMax());
							break;
						case 10:
							text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
							break;
					}
				}
				break;
			case 11:
				text = decimalFormat.format(peakModel.getLeading());
				break;
			case 12:
				text = decimalFormat.format(peakModel.getTailing());
				break;
			case 13:
				text = peak.getModelDescription();
				break;
			case 14:
				text = Integer.toString(peak.getSuggestedNumberOfComponents());
				break;
			case 15:
				ILibraryInformation libraryInformation = peakDataSupport.getBestLibraryInformation(peak.getTargets());
				if(libraryInformation != null) {
					text = libraryInformation.getName();
				}
				break;
			case 16:
				if(chromatogramPeakArea > 0) {
					double peakAreaPercent = (100.0d / chromatogramPeakArea) * peak.getIntegratedArea();
					text = decimalFormat.format(peakAreaPercent);
				} else {
					text = decimalFormat.format(0.0d);
				}
				break;
			case 17:
				text = (peak.getInternalStandards().size() > 0) ? "ISTD" : "";
				break;
			case 18:
				text = peak.getClassifier();
				break;
		}
		//
		return text;
	}

	private String getScanText(IScan scan, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String text = "";
		//
		switch(columnIndex) {
			case 0:
				text = "";
				break;
			case 1:
				text = SCAN;
				break;
			case 2:
				text = decimalFormat.format(scan.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				break;
			case 3:
				text = decimalFormat.format(scan.getRelativeRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				break;
			case 4:
				boolean showRetentionIndexWithoutDecimals = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
				if(showRetentionIndexWithoutDecimals) {
					text = Integer.toString((int)scan.getRetentionIndex());
				} else {
					text = decimalFormat.format(scan.getRetentionIndex());
				}
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				text = "--";
				break;
			case 13:
				text = "";
				break;
			case 14:
				text = "--";
				break;
			case 15:
				ILibraryInformation libraryInformation = scanDataSupport.getBestLibraryInformation(scan);
				if(libraryInformation != null) {
					text = libraryInformation.getName();
				}
				break;
			case 16:
				text = "--";
				break;
			case 17:
			case 18:
				text = "";
				break;
		}
		//
		return text;
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
		return image;
	}
}
