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

import jakarta.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedMassSpectrumHeaderUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumHeaderPart extends AbstractPart<ExtendedMassSpectrumHeaderUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public MassSpectrumHeaderPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedMassSpectrumHeaderUI createControl(Composite parent) {

		return new ExtendedMassSpectrumHeaderUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IVendorMassSpectrum vendorMassSpectrum) {
				getControl().updateMassSpectrum(vendorMassSpectrum);
				return true;
			} else {
				getControl().updateMassSpectrum(null);
				return false;
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
