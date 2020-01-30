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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedComparisonScanUI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ComparisonScanChartPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private final ExtendedComparisonScanUI extendedComparisonScanUI;
	//
	private static final int TARGET_MASS_SPECTRUM_UNKNOWN = 0;
	private static final int TARGET_ENTRY = 1;
	private final ExecutorService eventExecutor = Executors.newSingleThreadExecutor();

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

	@PreDestroy
	@Override
	protected void preDestroy() {

		eventExecutor.shutdownNow();
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		registerEvent(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
		registerEvent(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
		//
		String[] properties = new String[2];
		properties[TARGET_MASS_SPECTRUM_UNKNOWN] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN;
		properties[TARGET_ENTRY] = IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY;
		registerEvent(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, properties);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		try {
			if(isUnloadEvent(topic)) {
				extendedComparisonScanUI.update(null);
				return;
			} else if(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE.equals(topic)) {
				IScanMSD unknownMassSpectrum = (IScanMSD)objects.get(TARGET_MASS_SPECTRUM_UNKNOWN);
				IIdentificationTarget identificationTarget = (IIdentificationTarget)objects.get(TARGET_ENTRY);
				extendedComparisonScanUI.update(unknownMassSpectrum, identificationTarget, eventExecutor).get();
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
					return;
				}
				if(target != null) {
					extendedComparisonScanUI.update(scan, target, eventExecutor).get();
				} else {
					extendedComparisonScanUI.update(scan);
				}
			}
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch(ExecutionException e) {
			Activator activator = Activator.getDefault();
			activator.getLog().log(new Status(IStatus.ERROR, getClass().getName(), "Update scan failed", e));
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
