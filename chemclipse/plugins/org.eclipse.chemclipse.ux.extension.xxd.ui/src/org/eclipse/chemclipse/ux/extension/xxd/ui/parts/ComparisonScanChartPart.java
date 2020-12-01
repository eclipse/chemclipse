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

	@Inject
	public ComparisonScanChartPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedComparisonScanUI createControl(Composite parent) {

		return new ExtendedComparisonScanUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(isUnloadEvent(topic)) {
			getControl().clear();
			return false;
		} else if(isPeakUpdateEvent(topic) || isScanUpdateEvent(topic)) {
			Object object = objects.get(0);
			IScanMSD scan = null;
			IIdentificationTarget identificationTarget = null;
			if(object instanceof IScanMSD) {
				scan = (IScanMSD)object;
				identificationTarget = IIdentificationTarget.getBestIdentificationTarget(scan.getTargets());
			} else if(object instanceof IPeakMSD) {
				IPeakMSD peakMSD = (IPeakMSD)object;
				scan = peakMSD.getExtractedMassSpectrum();
				identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peakMSD.getTargets());
			}
			//
			if(identificationTarget != null) {
				getControl().update(scan, identificationTarget);
				return true;
			} else {
				getControl().update(scan);
				return true;
			}
		} else if(isScanTargetComparisonEvent(topic)) {
			Object object = objects.get(0);
			if(object instanceof Object[]) {
				Object[] values = (Object[])object;
				Object object1 = values[0];
				Object object2 = values[1];
				//
				if(object1 instanceof IScanMSD && object2 instanceof IIdentificationTarget) {
					IScanMSD unknownMassSpectrum = (IScanMSD)object1;
					IIdentificationTarget identificationTarget = (IIdentificationTarget)object2;
					getControl().update(unknownMassSpectrum, identificationTarget);
				}
			}
		} else if(isScanReferenceComparisonEvent(topic)) {
			Object object = objects.get(0);
			if(object instanceof Object[]) {
				Object[] values = (Object[])object;
				Object object1 = values[0];
				Object object2 = values[1];
				//
				if(object1 instanceof IScanMSD && object2 instanceof IScanMSD) {
					IScanMSD unknownMassSpectrum = (IScanMSD)object1;
					IScanMSD referenceMassSpectrum = (IScanMSD)object2;
					getControl().update(unknownMassSpectrum, referenceMassSpectrum);
				}
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isPeakUpdateEvent(topic) || isScanUpdateEvent(topic) || isScanTargetComparisonEvent(topic) || isScanReferenceComparisonEvent(topic) || isUnloadEvent(topic);
	}

	private boolean isScanUpdateEvent(String topic) {

		return IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION.equals(topic);
	}

	private boolean isPeakUpdateEvent(String topic) {

		return IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION.equals(topic);
	}

	private boolean isScanTargetComparisonEvent(String topic) {

		return IChemClipseEvents.TOPIC_SCAN_TARGET_UPDATE_COMPARISON.equals(topic);
	}

	private boolean isScanReferenceComparisonEvent(String topic) {

		return IChemClipseEvents.TOPIC_SCAN_REFERENCE_UPDATE_COMPARISON.equals(topic);
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
