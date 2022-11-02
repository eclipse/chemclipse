/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedColumnIndicesUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ColumnIndicesPart extends AbstractPart<ExtendedColumnIndicesUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public ColumnIndicesPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedColumnIndicesUI createControl(Composite parent) {

		return new ExtendedColumnIndicesUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isLibraryInformationTopic(topic)) {
				Object object = objects.get(0);
				ILibraryInformation libraryInformation = null;
				//
				if(object instanceof ILibraryMassSpectrum) {
					ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)object;
					libraryInformation = libraryMassSpectrum.getLibraryInformation();
				} else if(object instanceof IIdentificationTarget) {
					IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
					libraryInformation = identificationTarget.getLibraryInformation();
				}
				//
				getControl().setInput(libraryInformation);
				return true;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isLibraryInformationTopic(topic);
	}

	private boolean isLibraryInformationTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE)) {
			return true;
		}
		//
		return false;
	}
}