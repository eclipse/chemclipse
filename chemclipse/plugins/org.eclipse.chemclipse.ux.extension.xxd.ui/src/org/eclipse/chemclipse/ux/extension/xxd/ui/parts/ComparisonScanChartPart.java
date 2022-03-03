/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
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
	public void setFocus() {

		getControl().setFocus();
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(isChromatogramCloseEvent(topic) || isLibraryCloseEvent(topic)) {
			getControl().clear();
			return false;
		} else {
			if(objects.size() == 1) {
				Object object = objects.get(0);
				if(isPeakUpdateEvent(topic) || isScanUpdateEvent(topic)) {
					IScanMSD scan = null;
					IIdentificationTarget identificationTarget = null;
					if(object instanceof IScanMSD) {
						scan = (IScanMSD)object;
						float retentionIndex = scan.getRetentionIndex();
						IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(retentionIndex);
						identificationTarget = IIdentificationTarget.getBestIdentificationTarget(scan.getTargets(), identificationTargetComparator);
					} else if(object instanceof IPeakMSD) {
						IPeakMSD peakMSD = (IPeakMSD)object;
						scan = peakMSD.getExtractedMassSpectrum();
						float retentionIndex = scan.getRetentionIndex();
						IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(retentionIndex);
						identificationTarget = IIdentificationTarget.getBestIdentificationTarget(peakMSD.getTargets(), identificationTargetComparator);
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
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isPeakUpdateEvent(topic) || isScanUpdateEvent(topic) || isScanTargetComparisonEvent(topic) || isScanReferenceComparisonEvent(topic) || isChromatogramCloseEvent(topic) || isLibraryCloseEvent(topic);
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

	private boolean isChromatogramCloseEvent(String topic) {

		return IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE.equals(topic);
	}

	private boolean isLibraryCloseEvent(String topic) {

		return IChemClipseEvents.TOPIC_EDITOR_LIBRARY_CLOSE.equals(topic);
	}
}
