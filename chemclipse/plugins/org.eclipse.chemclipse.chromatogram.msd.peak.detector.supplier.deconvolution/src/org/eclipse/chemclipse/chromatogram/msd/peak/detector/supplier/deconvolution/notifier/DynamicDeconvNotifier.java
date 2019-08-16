/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.notifier;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.e4.core.services.events.IEventBroker;

public class DynamicDeconvNotifier implements IDynamicDeconvNotifier {

	@Inject
	private IEventBroker eventBroker;

	@Override
	public void update(IArraysViewDeconv arraysViewDeconv) {

		eventBroker.send("chromatogram/msd/update/peakdecondetec", arraysViewDeconv);
	}
}
