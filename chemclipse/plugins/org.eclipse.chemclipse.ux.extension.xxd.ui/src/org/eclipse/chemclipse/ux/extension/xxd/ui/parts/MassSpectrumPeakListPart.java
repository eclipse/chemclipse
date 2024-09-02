/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IVendorStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.ExtendedMassSpectrumPeakListUI;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import jakarta.inject.Inject;

public class MassSpectrumPeakListPart extends AbstractPart<ExtendedMassSpectrumPeakListUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public MassSpectrumPeakListPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedMassSpectrumPeakListUI createControl(Composite parent) {

		return new ExtendedMassSpectrumPeakListUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IVendorStandaloneMassSpectrum vendorStandaloneMassSpectrum) {
				getControl().update(vendorStandaloneMassSpectrum);
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic);
	}
}
