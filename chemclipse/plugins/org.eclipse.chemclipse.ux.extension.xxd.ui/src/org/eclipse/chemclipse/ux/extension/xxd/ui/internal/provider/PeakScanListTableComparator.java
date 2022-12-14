/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.targets.TargetSupport;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.jface.viewers.Viewer;

public class PeakScanListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private double chromatogramPeakArea = 0.0d;

	public void setChromatogramPeakArea(double chromatogramPeakArea) {

		this.chromatogramPeakArea = chromatogramPeakArea;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		//
		Object object1 = null;
		Object object2 = null;
		//
		if(e1 instanceof IPeak || e1 instanceof IScan) {
			object1 = e1;
		}
		//
		if(e2 instanceof IPeak || e2 instanceof IScan) {
			object2 = e2;
		}
		//
		if(object1 != null && object2 != null) {
			sortOrder = getSortOrder(object1, object2);
		}
		//
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private int getSortOrder(Object object1, Object object2) {

		int sortOrder = 0;
		//
		switch(getPropertyIndex()) {
			case 0:
				sortOrder = Boolean.compare(isActiveForAnalysis(object2), isActiveForAnalysis(object1));
				break;
			case 1:
				sortOrder = getType(object2).compareTo(getType(object1));
				break;
			case 2:
				sortOrder = Integer.compare(getRetentionTime(object2), getRetentionTime(object1));
				break;
			case 3:
				sortOrder = Integer.compare(getRelativeRetentionTime(object2), getRelativeRetentionTime(object1));
				break;
			case 4:
				sortOrder = Float.compare(getRetentionIndex(object2), getRetentionIndex(object1));
				break;
			case 5:
				sortOrder = Double.compare(getIntegratedArea(object2), getIntegratedArea(object1));
				break;
			case 6:
				sortOrder = Integer.compare(getStartRetentionTime(object2), getStartRetentionTime(object1));
				break;
			case 7:
				sortOrder = Integer.compare(getStopRetentionTime(object2), getStopRetentionTime(object1));
				break;
			case 8:
				sortOrder = Integer.compare(getWidth(object2), getWidth(object1));
				break;
			case 9:
			case 10:
				if(object1 instanceof IChromatogramPeakMSD chromatogramPeak1 && object2 instanceof IChromatogramPeakMSD chromatogramPeak2) {
					switch(getPropertyIndex()) {
						case 9:
							sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
							break;
						case 10:
							sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
							break;
					}
				} else if(object1 instanceof IChromatogramPeakCSD chromatogramPeak1 && object2 instanceof IChromatogramPeakCSD chromatogramPeak2) {
					switch(getPropertyIndex()) {
						case 9:
							sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
							break;
						case 10:
							sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
							break;
					}
				} else if(object1 instanceof IChromatogramPeakWSD chromatogramPeak1 && object2 instanceof IChromatogramPeakWSD chromatogramPeak2) {
					switch(getPropertyIndex()) {
						case 9:
							sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
							break;
						case 10:
							sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
							break;
					}
				}
				break;
			case 11:
				sortOrder = Float.compare(getLeading(object2), getLeading(object1));
				break;
			case 12:
				sortOrder = Float.compare(getTailing(object2), getTailing(object1));
				break;
			case 13:
				sortOrder = getModelDescription(object2).compareTo(getModelDescription(object1));
				break;
			case 14:
				sortOrder = getDetectorDescription(object2).compareTo(getDetectorDescription(object1));
				break;
			case 15:
				sortOrder = getIntegratorDescription(object2).compareTo(getIntegratorDescription(object1));
				break;
			case 16:
				sortOrder = Integer.compare(getSuggestedNumberOfComponents(object2), getSuggestedNumberOfComponents(object1));
				break;
			case 17:
				String name1 = TargetSupport.getBestTargetLibraryField(object1);
				String name2 = TargetSupport.getBestTargetLibraryField(object2);
				sortOrder = PreferenceSupplier.isSortCaseSensitive() ? name2.compareTo(name1) : name2.compareToIgnoreCase(name1);
				break;
			case 18:
				if(chromatogramPeakArea > 0) {
					double factor = 100.0d / chromatogramPeakArea;
					double peakAreaPercent1 = factor * getIntegratedArea(object1);
					double peakAreaPercent2 = factor * getIntegratedArea(object2);
					return Double.compare(peakAreaPercent2, peakAreaPercent1);
				} else {
					sortOrder = 0;
				}
				break;
			case 19:
				sortOrder = Integer.compare(getInternalStandards(object2), getInternalStandards(object1));
				break;
			case 20:
				String classifier1 = PeakScanListSupport.getClassifier(object1);
				String classifier2 = PeakScanListSupport.getClassifier(object2);
				sortOrder = PreferenceSupplier.isSortCaseSensitive() ? classifier2.compareTo(classifier1) : classifier2.compareToIgnoreCase(classifier1);
				break;
		}
		//
		return sortOrder;
	}

	private boolean isActiveForAnalysis(Object object) {

		if(object instanceof IPeak peak) {
			return peak.isActiveForAnalysis();
		} else {
			return false;
		}
	}

	private String getType(Object object) {

		if(object instanceof IPeak) {
			return PeakScanListLabelProvider.PEAK;
		} else {
			return PeakScanListLabelProvider.SCAN;
		}
	}

	private int getRetentionTime(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getPeakMaximum().getRetentionTime();
		} else if(object instanceof IScan scan) {
			return scan.getRetentionTime();
		}
		return 0;
	}

	private int getRelativeRetentionTime(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getPeakMaximum().getRelativeRetentionTime();
		} else if(object instanceof IScan scan) {
			return scan.getRelativeRetentionTime();
		}
		return 0;
	}

	private float getRetentionIndex(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getPeakMaximum().getRetentionIndex();
		} else if(object instanceof IScan scan) {
			return scan.getRetentionIndex();
		}
		return 0;
	}

	private double getIntegratedArea(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getIntegratedArea();
		} else {
			return 0.0d;
		}
	}

	private int getStartRetentionTime(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getStartRetentionTime();
		} else {
			return 0;
		}
	}

	private int getStopRetentionTime(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getStopRetentionTime();
		} else {
			return 0;
		}
	}

	private int getWidth(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getWidthBaselineByInflectionPoints();
		} else {
			return 0;
		}
	}

	private float getLeading(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getLeading();
		} else {
			return 0;
		}
	}

	private float getTailing(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getPeakModel().getTailing();
		} else {
			return 0;
		}
	}

	private String getModelDescription(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getModelDescription();
		} else {
			return "";
		}
	}

	private String getDetectorDescription(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getDetectorDescription();
		} else {
			return "";
		}
	}

	private String getIntegratorDescription(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getIntegratorDescription();
		} else {
			return "";
		}
	}

	private int getSuggestedNumberOfComponents(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getSuggestedNumberOfComponents();
		} else {
			return 0;
		}
	}

	private int getInternalStandards(Object object) {

		if(object instanceof IPeak peak) {
			return peak.getInternalStandards().size();
		} else {
			return 0;
		}
	}
}
