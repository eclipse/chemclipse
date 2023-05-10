/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.LibraryInformationComposite;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractLibraryInformationPart<T extends LibraryInformationComposite> extends AbstractPart<T> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	public AbstractLibraryInformationPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isLibraryInformationTopic(topic);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isLibraryInformationTopic(topic)) {
				Object object = objects.get(0);
				ILibraryInformation libraryInformation = null;
				boolean update = true;
				//
				if(object instanceof ILibraryMassSpectrum libraryMassSpectrum) {
					libraryInformation = libraryMassSpectrum.getLibraryInformation();
				} else if(object instanceof IIdentificationTarget identificationTarget) {
					libraryInformation = identificationTarget.getLibraryInformation();
				} else {
					/*
					 * Prevent that the part is cleaned.
					 * A scan could be a ILibraryMassSpectrum, hence perform
					 * a check if it's not of type library spectrum.
					 */
					if(object instanceof IScan || object instanceof IPeak) {
						update = false;
					}
				}
				//
				if(update) {
					getControl().setInput(libraryInformation);
					return true;
				}
			}
		}
		//
		return false;
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