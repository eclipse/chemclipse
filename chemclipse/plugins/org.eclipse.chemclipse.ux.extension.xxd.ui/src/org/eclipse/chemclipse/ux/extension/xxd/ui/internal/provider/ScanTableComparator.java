/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.jface.viewers.Viewer;

public class ScanTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private DataType dataType;

	public ScanTableComparator(DataType dataType) {

		this.dataType = dataType;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder;
		switch(dataType) {
			case MSD_NOMINAL:
				sortOrder = getNominalMSD(viewer, e1, e2);
				break;
			case MSD_TANDEM:
				sortOrder = getTandemMSD(viewer, e1, e2);
				break;
			case MSD_HIGHRES:
				sortOrder = getHighResolutionMSD(viewer, e1, e2);
				break;
			case CSD:
				sortOrder = getCSD(viewer, e1, e2);
				break;
			case WSD:
				sortOrder = getWSD(viewer, e1, e2);
				break;
			default:
				sortOrder = 0;
		}
		return sortOrder;
	}

	private int getNominalMSD(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IIon ion1 && e2 instanceof IIon ion2) {
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Double.compare(ion2.getIon(), ion1.getIon());
					break;
				case 1:
					sortOrder = Float.compare(ion2.getAbundance(), ion1.getAbundance());
					break;
				case 2: // rel. abundance == abundance
					sortOrder = Float.compare(ion2.getAbundance(), ion1.getAbundance());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private int getTandemMSD(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IIon ion1 && e2 instanceof IIon ion2) {
			IIonTransition ionTransition1 = ion1.getIonTransition();
			IIonTransition ionTransition2 = ion2.getIonTransition();
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Double.compare(ion2.getIon(), ion1.getIon());
					break;
				case 1:
					sortOrder = Float.compare(ion2.getAbundance(), ion1.getAbundance());
					break;
				case 2: // rel. abundance == abundance
					sortOrder = Float.compare(ion2.getAbundance(), ion1.getAbundance());
					break;
				case 3: // parent m/z
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Integer.compare(ionTransition2.getQ1Ion(), ionTransition1.getQ1Ion());
					break;
				case 4: // parent resolution
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getQ1Resolution(), ionTransition1.getQ1Resolution());
					break;
				case 5: // daughter m/z
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getQ3Ion(), ionTransition1.getQ3Ion());
					break;
				case 6: // daughter resolution
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getQ3Resolution(), ionTransition1.getQ3Resolution());
					break;
				case 7: // collision energy
					sortOrder = (ionTransition1 == null || ionTransition2 == null) ? 0 : Double.compare(ionTransition2.getCollisionEnergy(), ionTransition1.getCollisionEnergy());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private int getHighResolutionMSD(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IIon ion1 && e2 instanceof IIon ion2) {
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Double.compare(ion2.getIon(), ion1.getIon());
					break;
				case 1:
					sortOrder = Float.compare(ion2.getAbundance(), ion1.getAbundance());
					break;
				case 2: // rel. abundance == abundance
					sortOrder = Float.compare(ion2.getAbundance(), ion1.getAbundance());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private int getCSD(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IScanCSD scanCSD1 && e2 instanceof IScanCSD scanCSD2) {
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Integer.compare(scanCSD2.getRetentionTime(), scanCSD1.getRetentionTime());
					break;
				case 1:
					sortOrder = Float.compare(scanCSD2.getTotalSignal(), scanCSD1.getTotalSignal());
					break;
				case 2: // rel. abundance == abundance
					sortOrder = Float.compare(scanCSD2.getTotalSignal(), scanCSD1.getTotalSignal());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private int getWSD(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IScanSignalWSD scanSignalWSD1 && e2 instanceof IScanSignalWSD scanSignalWSD2) {
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Double.compare(scanSignalWSD2.getWavelength(), scanSignalWSD1.getWavelength());
					break;
				case 1:
					sortOrder = Float.compare(scanSignalWSD2.getAbundance(), scanSignalWSD1.getAbundance());
					break;
				case 2: // rel. abundance == abundance
					sortOrder = Float.compare(scanSignalWSD2.getAbundance(), scanSignalWSD1.getAbundance());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
