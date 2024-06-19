/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for name editing, improve classifier support
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
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.support.PeakClassifierSupport;
import org.eclipse.chemclipse.model.targets.TargetSupport;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.preferences.PreferenceSupplierModel;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class PeakScanListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String BEST_TARGET = ExtensionMessages.bestTarget;
	public static final String ACTIVE_FOR_ANALYSIS = ExtensionMessages.activeForAnalysis;
	public static final String TYPE = ExtensionMessages.type;
	public static final String RETENTION_TIME = ExtensionMessages.rtmin;
	public static final String RELATIVE_RETENTION_TIME = ExtensionMessages.rrtmin;
	public static final String RETENTION_INDEX = ExtensionMessages.ri;
	public static final String AREA_TOTAL = ExtensionMessages.areaTotal;
	public static final String START_RETENTION_TIME = ExtensionMessages.startRetentionTime;
	public static final String STOP_RETENTION_TIME = ExtensionMessages.stopRetentionTime;
	public static final String WIDTH = ExtensionMessages.widthInflectionPoints;
	public static final String SCAN_NUMBER_AT_PEAK_MAX = ExtensionMessages.scanNumberAtPeakMax;
	public static final String SIGNAL_TO_NOISE = ExtensionMessages.signalToNoise;
	public static final String LEADING = ExtensionMessages.leading;
	public static final String TAILING = ExtensionMessages.tailing;
	public static final String MODEL_DESCRIPTION = ExtensionMessages.modelDescription;
	public static final String DETECTOR = ExtensionMessages.modelDescription;
	public static final String INTEGRATOR = ExtensionMessages.integrator;
	public static final String SUGGESTED_COMPONENTS = ExtensionMessages.suggestedComponents;
	public static final String AREA_PERCENT = ExtensionMessages.areaPercent;
	public static final String QUANTIFIER = ExtensionMessages.quantifier;
	public static final String CLASSIFIER = ExtensionMessages.classifier;
	public static final String PEAK_MODEL = ExtensionMessages.peakModel;
	//
	public static final String PEAK = "PEAK";
	public static final String SCAN = "SCAN";
	//
	private static final String BLANK = "";
	private static final String NO_VALUE = "--";
	private static final String ISTD = "ISTD";
	//
	private double chromatogramPeakArea = 0.0d;
	//
	public static final String[] TITLES = { //
			ACTIVE_FOR_ANALYSIS, //
			TYPE, //
			RETENTION_TIME, //
			RELATIVE_RETENTION_TIME, //
			RETENTION_INDEX, //
			AREA_TOTAL, //
			START_RETENTION_TIME, //
			STOP_RETENTION_TIME, //
			WIDTH, //
			SCAN_NUMBER_AT_PEAK_MAX, //
			SIGNAL_TO_NOISE, //
			LEADING, //
			TAILING, //
			MODEL_DESCRIPTION, //
			DETECTOR, //
			INTEGRATOR, //
			SUGGESTED_COMPONENTS, //
			BEST_TARGET, //
			AREA_PERCENT, //
			QUANTIFIER, //
			CLASSIFIER, //
			PEAK_MODEL //
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
			100, //
			100, //
			100, //
			30 //
	};

	public void setChromatogramPeakArea(double chromatogramPeakArea) {

		this.chromatogramPeakArea = chromatogramPeakArea;
	}

	@Override
	public Color getBackground(final Object element) {

		if(element instanceof IPeak peak) {
			Set<IIdentificationTarget> peakTargets = peak.getTargets();
			for(IIdentificationTarget peakTarget : peakTargets) {
				IComparisonResult comparisonResult = peakTarget.getComparisonResult();
				if(comparisonResult instanceof IPeakComparisonResult peakComparisonResult) {
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
			if(element instanceof IPeak peak) {
				if(peak.isActiveForAnalysis()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImageProvider.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImageProvider.SIZE_16x16);
				}
			} else if(element instanceof IScan) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED_INACTIVE, IApplicationImageProvider.SIZE_16x16);
			}
		} else if(columnIndex == 1) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = BLANK;
		if(element instanceof IPeak || element instanceof IScan) {
			/*
			 * Show peak and scan data.
			 */
			if(element instanceof IPeak peak) {
				text = getPeakText(peak, columnIndex);
			} else if(element instanceof IScan scan) {
				text = getScanText(scan, columnIndex);
			}
		}
		return text;
	}

	private String getPeakText(IPeak peak, int columnIndex) {

		IPeakModel peakModel = peak.getPeakModel();
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = BLANK;
		//
		switch(columnIndex) {
			case 0:
				text = BLANK;
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
				if(PreferenceSupplierModel.showRetentionIndexWithoutDecimals()) {
					DecimalFormat integerFormat = getIntegerDecimalFormatInstance();
					text = integerFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
				} else {
					text = decimalFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
				}
				break;
			case 5:
				if(PreferenceSupplierModel.showAreaWithoutDecimals()) {
					DecimalFormat integerFormat = getIntegerDecimalFormatInstance();
					text = integerFormat.format(peak.getIntegratedArea());
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
				if(peak instanceof IChromatogramPeakMSD chromatogramPeak) {
					switch(columnIndex) {
						case 9:
							text = Integer.toString(chromatogramPeak.getScanMax());
							break;
						case 10:
							text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
							break;
					}
				} else if(peak instanceof IChromatogramPeakCSD chromatogramPeak) {
					switch(columnIndex) {
						case 9:
							text = Integer.toString(chromatogramPeak.getScanMax());
							break;
						case 10:
							text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
							break;
					}
				} else if(peak instanceof IChromatogramPeakWSD chromatogramPeak) {
					switch(columnIndex) {
						case 9:
							text = Integer.toString(chromatogramPeak.getScanMax());
							break;
						case 10:
							float sn = chromatogramPeak.getSignalToNoiseRatio();
							text = Float.isNaN(sn) ? NO_VALUE : decimalFormat.format(sn);
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
				text = peak.getDetectorDescription();
				break;
			case 15:
				text = peak.getIntegratorDescription();
				break;
			case 16:
				text = Integer.toString(peak.getSuggestedNumberOfComponents());
				break;
			case 17:
				text = TargetSupport.getBestTargetLibraryField(peak);
				break;
			case 18:
				if(chromatogramPeakArea > 0) {
					DecimalFormat decimalFormatPercent = ValueFormat.getDecimalFormatEnglish("0.000");
					double peakAreaPercent = (100.0d / chromatogramPeakArea) * peak.getIntegratedArea();
					text = decimalFormatPercent.format(peakAreaPercent);
				} else {
					text = NO_VALUE;
				}
				break;
			case 19:
				text = (!peak.getInternalStandards().isEmpty()) ? ISTD : BLANK;
				break;
			case 20:
				text = PeakClassifierSupport.getClassifier(peak);
				break;
			case 21:
				text = peakModel.isStrictModel() ? "S" : "";
				break;
		}
		//
		return text;
	}

	private String getScanText(IScan scan, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = BLANK;
		//
		switch(columnIndex) {
			case 0:
				text = BLANK;
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
				if(PreferenceSupplierModel.showRetentionIndexWithoutDecimals()) {
					DecimalFormat integerFormat = getIntegerDecimalFormatInstance();
					text = integerFormat.format(scan.getRetentionIndex());
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
				text = NO_VALUE;
				break;
			case 13:
				text = BLANK;
				break;
			case 14:
				text = NO_VALUE;
				break;
			case 15:
				text = NO_VALUE;
				break;
			case 16:
				text = NO_VALUE;
				break;
			case 17:
				text = TargetSupport.getBestTargetLibraryField(scan);
				break;
			case 18:
				text = NO_VALUE;
				break;
			case 19:
			case 20:
			case 21:
				text = BLANK;
				break;
		}
		//
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImageProvider.SIZE_16x16);
	}
}