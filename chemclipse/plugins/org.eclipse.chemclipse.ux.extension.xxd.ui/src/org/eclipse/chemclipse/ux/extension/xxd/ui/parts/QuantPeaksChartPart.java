/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.QuantPeaksChartUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class QuantPeaksChartPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private QuantPeaksChartUI quantPeaksChartUI;

	@Inject
	public QuantPeaksChartPart(Composite parent, MPart part) {
		super(part);
		quantPeaksChartUI = new QuantPeaksChartUI(parent, SWT.NONE);
	}

	@Focus
	public void setFocus() {

		updateObjects(getObjects(), getTopic());
	}

	@Override
	public void registerEvents() {

		System.out.println("Change TOPIC and PROPERTY");
		registerEvent("quantitation/msd/update/supplier/chemclipse/quantitationcompounddocument", "QuantitationCompoundDocument");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IQuantitationCompound) {
				quantPeaksChartUI.update((IQuantitationCompound)object);
			} else {
				quantPeaksChartUI.update(null);
			}
		}
	}
}
