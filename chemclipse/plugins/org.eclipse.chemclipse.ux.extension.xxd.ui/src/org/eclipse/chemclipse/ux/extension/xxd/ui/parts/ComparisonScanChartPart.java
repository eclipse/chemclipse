/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedComparisonScanUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class ComparisonScanChartPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private ExtendedComparisonScanUI extendedComparisonScanUI;
	//
	private static final int NUMBER_PROPERTIES = 2;
	private static final int TARGET_MASS_SPECTRUM_UNKNOWN = 0;
	private static final int TARGET_ENTRY = 1;

	@Inject
	public ComparisonScanChartPart(Composite parent, MPart part) {
		super(part);
		extendedComparisonScanUI = new ExtendedComparisonScanUI(parent);
	}

	@Focus
	public void setFocus() {

		updateObjects(getObjects(), getTopic());
	}

	@Override
	public void registerEvents() {

		String[] properties = new String[NUMBER_PROPERTIES];
		properties[TARGET_MASS_SPECTRUM_UNKNOWN] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN;
		properties[TARGET_ENTRY] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY;
		registerEvent(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, properties);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		if(objects.size() == NUMBER_PROPERTIES) {
			IScanMSD unknownMassSpectrum = (IScanMSD)objects.get(TARGET_MASS_SPECTRUM_UNKNOWN);
			IIdentificationTarget identificationTarget = (IIdentificationTarget)objects.get(TARGET_ENTRY);
			extendedComparisonScanUI.update(unknownMassSpectrum, identificationTarget);
		}
	}
}
