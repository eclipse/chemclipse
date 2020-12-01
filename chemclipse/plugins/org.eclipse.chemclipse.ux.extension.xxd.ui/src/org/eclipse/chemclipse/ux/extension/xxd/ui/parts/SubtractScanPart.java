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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedSubtractScanUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SubtractScanPart extends AbstractPart<ExtendedSubtractScanUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM;

	@Inject
	public SubtractScanPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedSubtractScanUI createControl(Composite parent) {

		return new ExtendedSubtractScanUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM.equals(topic)) {
				IScanMSD scanMSD = PreferenceSupplier.getSessionSubtractMassSpectrum();
				getControl().update(scanMSD);
				return true;
			} else if(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION.equals(topic)) {
				if(object instanceof IChromatogramSelectionMSD) {
					getControl().update(object);
					return true;
				}
			} else {
				getControl().update(null);
				return true;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic);
	}
}
