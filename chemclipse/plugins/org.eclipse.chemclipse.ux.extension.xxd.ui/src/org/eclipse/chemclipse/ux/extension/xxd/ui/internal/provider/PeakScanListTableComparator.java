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
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.jface.viewers.Viewer;

public class PeakScanListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private double chromatogramPeakArea = 0.0d;
	private final TargetExtendedComparator comparator = new TargetExtendedComparator(SortOrder.DESC);

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
		if(e1 instanceof IPeak) {
			object1 = e1;
		} else if(e1 instanceof IScan) {
			object1 = e1;
		}
		//
		if(e2 instanceof IPeak) {
			object2 = e2;
		} else if(e2 instanceof IScan) {
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
				if(object1 instanceof IChromatogramPeakMSD && object2 instanceof IChromatogramPeakMSD) {
					IChromatogramPeakMSD chromatogramPeak1 = (IChromatogramPeakMSD)object1;
					IChromatogramPeakMSD chromatogramPeak2 = (IChromatogramPeakMSD)object2;
					switch(getPropertyIndex()) {
						case 9:
							sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
							break;
						case 10:
							sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
							break;
					}
				} else if(object1 instanceof IChromatogramPeakCSD && object2 instanceof IChromatogramPeakCSD) {
					IChromatogramPeakCSD chromatogramPeak1 = (IChromatogramPeakCSD)object1;
					IChromatogramPeakCSD chromatogramPeak2 = (IChromatogramPeakCSD)object2;
					switch(getPropertyIndex()) {
						case 9:
							sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
							break;
						case 10:
							sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
							break;
					}
				} else if(object1 instanceof IChromatogramPeakWSD && object2 instanceof IChromatogramPeakWSD) {
					IChromatogramPeakWSD chromatogramPeak1 = (IChromatogramPeakWSD)object1;
					IChromatogramPeakWSD chromatogramPeak2 = (IChromatogramPeakWSD)object2;
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
				sortOrder = Integer.compare(getSuggestedNumberOfComponents(object2), getSuggestedNumberOfComponents(object1));
				break;
			case 15:
				String name1 = getName(object1);
				String name2 = getName(object2);
				//
				if(name1 != null && name2 != null) {
					sortOrder = name2.compareTo(name1);
				}
				break;
			case 16:
				if(chromatogramPeakArea > 0) {
					double factor = 100.0d / chromatogramPeakArea;
					double peakAreaPercent1 = factor * getIntegratedArea(object1);
					double peakAreaPercent2 = factor * getIntegratedArea(object2);
					return Double.compare(peakAreaPercent2, peakAreaPercent1);
				} else {
					sortOrder = 0;
				}
				break;
			case 17:
				sortOrder = Integer.compare(getInternalStandards(object2), getInternalStandards(object1));
				break;
			case 18:
				sortOrder = getClassifier(object2).size() - (getClassifier(object1)).size();
				break;
		}
		//
		return sortOrder;
	}

	private boolean isActiveForAnalysis(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).isActiveForAnalysis();
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

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getPeakMaximum().getRetentionTime();
		} else {
			return ((IScan)object).getRetentionTime();
		}
	}

	private int getRelativeRetentionTime(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getPeakMaximum().getRelativeRetentionTime();
		} else {
			return ((IScan)object).getRelativeRetentionTime();
		}
	}

	private float getRetentionIndex(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getPeakMaximum().getRetentionIndex();
		} else {
			return ((IScan)object).getRetentionIndex();
		}
	}

	private double getIntegratedArea(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getIntegratedArea();
		} else {
			return 0.0d;
		}
	}

	private int getStartRetentionTime(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getStartRetentionTime();
		} else {
			return 0;
		}
	}

	private int getStopRetentionTime(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getStopRetentionTime();
		} else {
			return 0;
		}
	}

	private int getWidth(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getWidthBaselineByInflectionPoints();
		} else {
			return 0;
		}
	}

	private float getLeading(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getLeading();
		} else {
			return 0;
		}
	}

	private float getTailing(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getPeakModel().getTailing();
		} else {
			return 0;
		}
	}

	private String getModelDescription(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getModelDescription();
		} else {
			return "";
		}
	}

	private int getSuggestedNumberOfComponents(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getSuggestedNumberOfComponents();
		} else {
			return 0;
		}
	}

	private int getInternalStandards(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getInternalStandards().size();
		} else {
			return 0;
		}
	}

	private Collection<String> getClassifier(Object object) {

		if(object instanceof IPeak) {
			return ((IPeak)object).getClassifier();
		} else {
			return Collections.emptyList();
		}
	}

	private String getName(Object object) {

		/*
		 * Is a peak name set?
		 */
		String name = null;
		if(object instanceof IPeak) {
			IPeak peak = (IPeak)object;
			name = peak.getName();
		}
		/*
		 * No peak name set.
		 * Then try to get the peak or scan best match.
		 */
		if(name == null) {
			if(object instanceof ITargetSupplier) {
				ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(((ITargetSupplier)object).getTargets(), comparator);
				if(libraryInformation != null) {
					name = libraryInformation.getName();
				}
			} else {
				name = "";
			}
		}
		/*
		 * No hit at all?
		 * Then return an empty String.
		 */
		return name != null ? name : "";
	}
}
