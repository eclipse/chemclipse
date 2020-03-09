/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for name editing, improve classifier support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.AbstractPeak;
import org.eclipse.chemclipse.model.core.Classifiable;
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class PeakScanListLabelProvider extends AbstractChemClipseLabelProvider implements ITableFontProvider {

	public static final String NAME = "Name";
	public static final String ACTIVE_FOR_ANALYSIS = "Active for Analysis";
	public static final String TYPE = "Type";
	public static final String RETENTION_TIME = "RT [min]";
	public static final String RELATIVE_RETENTION_TIME = "RRT [min]";
	public static final String RETENTION_INDEX = "RI";
	public static final String AREA_TOTAL = "Area";
	public static final String START_RETENTION_TIME = "Start RT [min]";
	public static final String STOP_RETENTION_TIME = "Stop RT [min]";
	public static final String WIDTH = "Width";
	public static final String SCAN_NUMBER_AT_PEAK_MAX = "Scan# at Peak Maximum";
	public static final String SIGNAL_TO_NOISE = "S/N";
	public static final String LEADING = "Leading";
	public static final String TAILING = "Tailing";
	public static final String MODEL_DESCRIPTION = "Model Description";
	public static final String SUGGESTED_COMPONENTS = "Suggested Components";
	public static final String AREA_PERCENT = "Area [%]";
	public static final String QUANTIFIER = "Quantifier";
	public static final String CLASSIFIER = "Classifier";
	//
	public static final String PEAK = "PEAK";
	public static final String SCAN = "SCAN";
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
			SUGGESTED_COMPONENTS, //
			NAME, //
			AREA_PERCENT, //
			QUANTIFIER, //
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
	private Font italicFont;

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
	public Font getFont(Object element, int columnIndex) {

		if(columnIndex == 15 && element instanceof AbstractPeak) {
			AbstractPeak abstractPeak = (AbstractPeak)element;
			if(abstractPeak.isNameSet()) {
				return italicFont;
			}
		}
		return null;
	}

	@Override
	protected void initialize(ColumnViewer viewer, ViewerColumn column) {

		super.initialize(viewer, column);
		Font defaultFont = viewer.getControl().getFont();
		italicFont = FontDescriptor.createFrom(defaultFont).setStyle(SWT.ITALIC).createFont(defaultFont.getDevice());
	}

	@Override
	public void dispose(ColumnViewer viewer, ViewerColumn column) {

		if(italicFont != null) {
			italicFont.dispose();
		}
		super.dispose(viewer, column);
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
					DecimalFormat integerFormat = createIntegerDecimalFormatInstance();
					text = integerFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
				} else {
					text = decimalFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
				}
				break;
			case 5:
				boolean showAreaWithoutDecimals = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS);
				if(showAreaWithoutDecimals) {
					DecimalFormat integerFormat = createIntegerDecimalFormatInstance();
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
			case 15: {
				String peakName = peak.getName();
				if(peakName != null) {
					return peakName;
				}
				ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(peak.getTargets());
				if(libraryInformation != null) {
					return libraryInformation.getName();
				}
			}
				break;
			case 16:
				if(chromatogramPeakArea > 0) {
					double peakAreaPercent = (100.0d / chromatogramPeakArea) * peak.getIntegratedArea();
					text = decimalFormat.format(peakAreaPercent);
				} else {
					text = "-";
				}
				break;
			case 17:
				text = (peak.getInternalStandards().size() > 0) ? "ISTD" : "";
				break;
			case 18: {
				ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(peak.getTargets());
				if(libraryInformation != null) {
					Set<String> set = new LinkedHashSet<>();
					set.addAll(peak.getClassifier());
					set.addAll(libraryInformation.getClassifier());
					return Classifiable.asString(set);
				} else {
					return Classifiable.asString(peak);
				}
			}
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
				ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(scan.getTargets());
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
