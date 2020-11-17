/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedMoleculeUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MoleculePart extends AbstractPart<ExtendedMoleculeUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;

	@Inject
	public MoleculePart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedMoleculeUI createControl(Composite parent) {

		return new ExtendedMoleculeUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isScanTopic(topic) || isPeakTopic(topic)) {
				/*
				 * Scan/Peak
				 */
				Object object = objects.get(0);
				ILibraryInformation libraryInformation = null;
				if(object instanceof ILibraryMassSpectrum) {
					ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)object;
					libraryInformation = libraryMassSpectrum.getLibraryInformation();
				}
				getControl().setInput(libraryInformation);
				return true;
			} else if(isIdentificationTargetTopic(topic)) {
				/*
				 * Identification Target
				 */
				Object object = objects.get(0);
				ILibraryInformation libraryInformation = null;
				if(object instanceof IIdentificationTarget) {
					IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
					libraryInformation = identificationTarget.getLibraryInformation();
				}
				getControl().setInput(libraryInformation);
				return true;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isScanTopic(topic) || isPeakTopic(topic) || isIdentificationTargetTopic(topic);
	}

	private boolean isScanTopic(String topic) {

		return topic.equals(TOPIC);
	}

	private boolean isPeakTopic(String topic) {

		return topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION);
	}

	private boolean isIdentificationTargetTopic(String topic) {

		return topic.equals(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE);
	}
}
