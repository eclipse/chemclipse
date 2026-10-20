/*******************************************************************************
 * Copyright (c) 2021, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.IPageLayout;

public class ResetPerspectiveHandler {

	private static final Logger logger = Logger.getLogger(ResetPerspectiveHandler.class);

	@Execute
	public void execute(MApplication application, MWindow window, EModelService modelService, EPartService partService, IEventBroker eventBroker) {

		MPerspectiveStack perspectiveStack = (MPerspectiveStack)modelService.find(IPerspectiveAndViewIds.STACK_PERSPECTIVES, application);
		if(perspectiveStack == null) {
			return;
		}
		MPerspective perspective = perspectiveStack.getSelectedElement();
		if(perspective == null) {
			return;
		}
		/*
		 * Remember the live shared editor area, so that it can be reconnected to the reset
		 * perspective below in order to keep the open editors.
		 */
		MUIElement editorArea = null;
		if(modelService.find(IPageLayout.ID_EDITOR_AREA, perspective) instanceof MPlaceholder placeholder) {
			editorArea = placeholder.getRef();
		}
		modelService.resetPerspectiveModel(perspective, window);
		MUIElement snippet = modelService.cloneSnippet(application, perspective.getElementId(), window);
		if(!(snippet instanceof MPerspective resetPerspective)) {
			logger.warn("No snapshot is available to reset the perspective: " + perspective.getElementId());
			return;
		}
		reconnectEditorArea(modelService, window, resetPerspective, editorArea);
		reconnectPlaceholders(modelService, perspective, resetPerspective);
		modelService.hideLocalPlaceholders(window, resetPerspective);
		List<MPartSashContainerElement> resetChildren = resetPerspective.getChildren();
		List<MPartSashContainerElement> perspectiveChildren = perspective.getChildren();
		int resetCount = resetChildren.size();
		while(!resetChildren.isEmpty()) {
			perspectiveChildren.add(resetChildren.remove(0));
		}
		while(perspectiveChildren.size() > resetCount) {
			MUIElement obsoleteChild = perspectiveChildren.get(0);
			obsoleteChild.setToBeRendered(false);
			perspectiveChildren.remove(0);
		}
		perspective.getTags().clear();
		perspective.getTags().addAll(resetPerspective.getTags());
		restoreEditorAreaStackId(modelService, window);
		partService.requestActivation();
		eventBroker.post(IChemClipseEvents.TOPIC_APPLICATION_RESET_PERSPECTIVE, perspective.getElementId());
	}

	/*
	 * Reconnect the placeholder to the existing area so that the open editors are preserved.
	 */
	private void reconnectEditorArea(EModelService modelService, MWindow window, MPerspective resetPerspective, MUIElement editorArea) {

		if(editorArea != null && modelService.find(IPageLayout.ID_EDITOR_AREA, resetPerspective) instanceof MPlaceholder placeholder) {
			MUIElement createdArea = placeholder.getRef();
			if(createdArea != editorArea) {
				placeholder.setRef(editorArea);
				if(createdArea != null) {
					window.getSharedElements().remove(createdArea);
				}
			}
		}
	}

	/*
	 * The snapshot placeholders are resolved by their elementId, which fails for placeholders
	 * that reference an imported shared part with a different elementId. Take the reference
	 * from the matching placeholder of the current perspective instead.
	 */
	private void reconnectPlaceholders(EModelService modelService, MPerspective perspective, MPerspective resetPerspective) {

		for(MPlaceholder placeholder : modelService.findElements(resetPerspective, null, MPlaceholder.class)) {
			if(placeholder.getRef() != null) {
				continue;
			}
			if(modelService.find(placeholder.getElementId(), perspective) instanceof MPlaceholder current && current.getRef() != null) {
				placeholder.setRef(current.getRef());
			} else {
				logger.warn("The placeholder could not be resolved: " + placeholder.getElementId());
				placeholder.setToBeRendered(false);
			}
		}
	}

	/*
	 * Restore shared editor area id so that newly opened editors are still docked into it.
	 */
	private void restoreEditorAreaStackId(EModelService modelService, MWindow window) {

		for(MPartStack partStack : modelService.findElements(window, null, MPartStack.class)) {
			if(partStack.getTags().contains(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID) && !IPerspectiveAndViewIds.EDITOR_PART_STACK_ID.equals(partStack.getElementId())) {
				partStack.setElementId(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID);
			}
		}
	}
}
