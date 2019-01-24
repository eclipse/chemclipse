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
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedComparisonScanUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ComparisonScanChartPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private ExtendedComparisonScanUI extendedComparisonScanUI;
	//
	private static final int NUMBER_PROPERTIES_1 = 1;
	private static final int NUMBER_PROPERTIES_2 = 2;
	private static final int TARGET_MASS_SPECTRUM_UNKNOWN = 0;
	private static final int TARGET_ENTRY = 1;

	@Inject
	public ComparisonScanChartPart(Composite parent, MPart part) {
		super(part);
		extendedComparisonScanUI = new ExtendedComparisonScanUI(parent, SWT.BORDER);
	}

	@Focus
	public void setFocus() {

		/*
		 * Don't update to not load the reference twice.
		 */
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		registerEvent(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
		registerEvent(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
		//
		String[] properties = new String[NUMBER_PROPERTIES_2];
		properties[TARGET_MASS_SPECTRUM_UNKNOWN] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN;
		properties[TARGET_ENTRY] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY;
		registerEvent(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, properties);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		if(objects.size() == NUMBER_PROPERTIES_1) {
			/*
			 * MSD (Scan1, Scan2)
			 */
			if(isUnloadEvent(topic)) {
				extendedComparisonScanUI.update(null);
			} else {
				Object object = objects.get(0);
				if(object instanceof IScanMSD) {
					extendedComparisonScanUI.update((IScanMSD)object);
				} else if(object instanceof IPeakMSD) {
					extendedComparisonScanUI.update(((IPeakMSD)object).getExtractedMassSpectrum());
				}
			}
		} else if(objects.size() == NUMBER_PROPERTIES_2) {
			/*
			 * MSD + TARGET
			 */
			IScanMSD unknownMassSpectrum = (IScanMSD)objects.get(TARGET_MASS_SPECTRUM_UNKNOWN);
			IIdentificationTarget identificationTarget = (IIdentificationTarget)objects.get(TARGET_ENTRY);
			extendedComparisonScanUI.update(unknownMassSpectrum, identificationTarget);
		}
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}
}
