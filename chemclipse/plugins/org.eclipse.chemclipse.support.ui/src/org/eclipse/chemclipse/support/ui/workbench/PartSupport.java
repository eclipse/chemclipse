/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.workbench;

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * A helper class that can be injected into E4 parts to perform common tasks
 * 
 * @author Christoph Läubrich
 *
 */
@Creatable
public class PartSupport {

	@Inject
	private MApplication mApplication;
	@Inject
	private EModelService eModelService;
	@Inject
	private EPartService ePartService;

	public void removeEditorsFromPartStack() {

		MPartStack partStack = (MPartStack)eModelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, mApplication);
		Collection<MPart> parts = ePartService.getParts();
		for(MPart part : parts) {
			if(part.getObject() != null) {
				part.setToBeRendered(false);
				part.setVisible(false);
				partStack.getChildren().remove(part);
			}
		}
	}

	public boolean saveDirtyParts() {

		return ePartService.saveAll(true);
	}

	/**
	 * Load and show the part.
	 * 
	 * @param partId
	 */
	public void focusPart(String partId) {

		MUIElement element = eModelService.find(partId, mApplication);
		if(element instanceof MPart) {
			MPart part = (MPart)element;
			if(!ePartService.getParts().contains(part)) {
				ePartService.createPart(part.getElementId());
			}
			ePartService.showPart(part, PartState.ACTIVATE);
		}
	}
}
