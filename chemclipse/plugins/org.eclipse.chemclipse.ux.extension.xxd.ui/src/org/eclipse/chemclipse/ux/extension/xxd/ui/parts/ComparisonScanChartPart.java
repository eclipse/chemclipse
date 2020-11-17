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
 * Christoph Läubrich - execute updates in own eventqueue, optimize display of target spectrum
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedComparisonScanUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ComparisonScanChartPart extends AbstractPart<ExtendedComparisonScanUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION;
	//
	private static final int TARGET_MASS_SPECTRUM_UNKNOWN = 0;
	private static final int TARGET_ENTRY = 1;

	@Inject
	public ComparisonScanChartPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedComparisonScanUI createControl(Composite parent) {

		return new ExtendedComparisonScanUI(parent, SWT.NONE);
	}

	@Override
	protected void subscribeAdditionalTopics() {

		String[] properties = new String[2];
		properties[TARGET_MASS_SPECTRUM_UNKNOWN] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN;
		properties[TARGET_ENTRY] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY;
		subscribeAdditionalTopic(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, properties);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(isUnloadEvent(topic)) {
			getControl().update(null);
			return false;
		} else if(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE.equals(topic)) {
			IScanMSD unknownMassSpectrum = (IScanMSD)objects.get(TARGET_MASS_SPECTRUM_UNKNOWN);
			IIdentificationTarget identificationTarget = (IIdentificationTarget)objects.get(TARGET_ENTRY);
			getControl().update(unknownMassSpectrum, identificationTarget);
			return true;
		} else if(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION.equals(topic) || IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION.equals(topic)) {
			Object object = objects.get(0);
			IScanMSD scan;
			IIdentificationTarget target = null;
			if(object instanceof IScanMSD) {
				scan = (IScanMSD)object;
				target = IIdentificationTarget.getBestIdentificationTarget(scan.getTargets());
			} else if(object instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)object;
				scan = peakMSD.getExtractedMassSpectrum();
				target = IIdentificationTarget.getBestIdentificationTarget(peakMSD.getTargets());
			} else {
				return false;
			}
			//
			if(target != null) {
				getControl().update(scan, target);
				return true;
			} else {
				getControl().update(scan);
				return true;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic) || isScanUpdateEvent(topic) || isUnloadEvent(topic);
	}

	private boolean isScanUpdateEvent(String topic) {

		return IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION.equals(topic);
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
