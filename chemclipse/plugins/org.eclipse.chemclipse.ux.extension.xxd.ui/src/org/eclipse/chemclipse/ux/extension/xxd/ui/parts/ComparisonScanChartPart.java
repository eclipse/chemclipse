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

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedComparisonScanUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class ComparisonScanChartPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(ComparisonScanChartPart.class);
	private ExtendedComparisonScanUI extendedComparisonScanUI;

	@Inject
	public ComparisonScanChartPart(Composite parent, MPart part) {
		super(part);
		extendedComparisonScanUI = new ExtendedComparisonScanUI(parent);
	}

	@Focus
	public void setFocus() {

		updateObject(getObject(), getTopic());
	}

	@Override
	public void registerEvents() {

		// TODO Multiple Topics
		// IScanMSD unknownMassSpectrum = (IScanMSD)event.getProperty(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN);
		// IIdentificationTarget identificationTarget = (IIdentificationTarget)event.getProperty(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY);
		registerEvent(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN);
	}

	@Override
	public void updateObject(Object object, String topic) {

		// get the library comparison entry
		extendedComparisonScanUI.update(object);
	}
	// private void update(IScanMSD unknownMassSpectrum, IIdentificationTarget identificationTarget) {
	//
	// if(doUpdate()) {
	// IRunnableWithProgress runnable = new LibraryServiceRunnable(this, unknownMassSpectrum, identificationTarget);
	// ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
	// try {
	// monitor.run(true, true, runnable);
	// } catch(InvocationTargetException e) {
	// logger.warn(e);
	// } catch(InterruptedException e) {
	// logger.warn(e);
	// }
	// }
	// }
}
