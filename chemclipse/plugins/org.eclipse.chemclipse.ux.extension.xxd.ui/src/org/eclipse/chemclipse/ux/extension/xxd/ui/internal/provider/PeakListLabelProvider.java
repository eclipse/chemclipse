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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class PeakListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String ACTIVE_FOR_ANALYSIS = "Active for Analysis";
	public static final String RT = "RT";
	public static final String CLASSIFIER = "Classifier";
	//
	private double chromatogramPeakArea = 0.0d;
	private PeakDataSupport peakDataSupport = new PeakDataSupport();
	//
	public static final String[] TITLES = { //
			ACTIVE_FOR_ANALYSIS, //
			"RT (min)", //
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
			List<IPeakTarget> peakTargets = peak.getTargets();
			for(IPeakTarget t : peakTargets) {
				IComparisonResult cp = t.getComparisonResult();
				if(cp instanceof IPeakComparisonResult) {
					IPeakComparisonResult pcp = (IPeakComparisonResult)cp;
					if(pcp.isMarkerPeak()) {
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
			}
		} else if(columnIndex == 1) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IPeak) {
			//
			IPeak peak = (IPeak)element;
			IPeakModel peakModel = peak.getPeakModel();
			ILibraryInformation libraryInformation = peakDataSupport.getBestLibraryInformation(new ArrayList<IPeakTarget>(peak.getTargets()));
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			//
			switch(columnIndex) {
				case 0:
					text = "";
					break;
				case 1:
					text = decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 2:
					boolean showRetentionIndexWithoutDecimals = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
					if(showRetentionIndexWithoutDecimals) {
						text = Integer.toString((int)peakModel.getPeakMaximum().getRetentionIndex());
					} else {
						text = decimalFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
					}
					break;
				case 3:
					boolean showAreaWithoutDecimals = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS);
					if(showAreaWithoutDecimals) {
						text = Integer.toString((int)peak.getIntegratedArea());
					} else {
						text = decimalFormat.format(peak.getIntegratedArea());
					}
					break;
				case 4:
					text = decimalFormat.format(peakModel.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 5:
					text = decimalFormat.format(peakModel.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 6:
					text = decimalFormat.format(peakModel.getWidthByInflectionPoints() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 7:
				case 8:
					if(element instanceof IChromatogramPeakMSD) {
						IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)element;
						switch(columnIndex) {
							case 7:
								text = Integer.toString(chromatogramPeak.getScanMax());
								break;
							case 8:
								text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
								break;
						}
					} else if(element instanceof IChromatogramPeakCSD) {
						IChromatogramPeakCSD chromatogramPeak = (IChromatogramPeakCSD)element;
						switch(columnIndex) {
							case 7:
								text = Integer.toString(chromatogramPeak.getScanMax());
								break;
							case 8:
								text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
								break;
						}
					}
					break;
				case 9:
					text = decimalFormat.format(peakModel.getLeading());
					break;
				case 10:
					text = decimalFormat.format(peakModel.getTailing());
					break;
				case 11:
					text = peak.getModelDescription();
					break;
				case 12:
					text = Integer.toString(peak.getSuggestedNumberOfComponents());
					break;
				case 13:
					if(libraryInformation != null) {
						text = libraryInformation.getName();
					}
					break;
				case 14:
					if(chromatogramPeakArea > 0) {
						double peakAreaPercent = (100.0d / chromatogramPeakArea) * peak.getIntegratedArea();
						text = decimalFormat.format(peakAreaPercent);
					} else {
						text = decimalFormat.format(0.0d);
					}
					break;
				case 15:
					text = (peak.getInternalStandards().size() > 0) ? "ISTD" : "";
					break;
				case 16:
					text = peak.getClassifier();
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
