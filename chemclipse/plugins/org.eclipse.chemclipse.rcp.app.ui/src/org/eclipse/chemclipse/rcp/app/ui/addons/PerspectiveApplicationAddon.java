/*******************************************************************************
 * Copyright (c) 2013, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.addons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import jakarta.annotation.PostConstruct;

public class PerspectiveApplicationAddon {

	private static final String PROPERTY_PERSPECTIVE = "application.perspective";
	/*
	 * The legacy action sets of org.eclipse.ui.editors are marked as initially visible and are
	 * therefore activated in every perspective, which adds their disabled tool items to the trim.
	 */
	private static final String HIDDEN_ITEMS_KEY = "persp.hiddenItems";
	private static final String HIDDEN_ACTION_SET_PREFIX = "persp.hideActionSetSC:";
	private static final List<String> HIDDEN_ACTION_SET_IDS = List.of( //
			"org.eclipse.ui.edit.text.actionSet.annotationNavigation", //
			"org.eclipse.ui.edit.text.actionSet.navigation");

	@PostConstruct
	public void postConstruct(MApplication application, EModelService modelService, IEventBroker eventBroker) {
		// TODO remove once e4 is fixed to not inject action sets unconditionally
		hideActionSets(application, modelService);
		MPerspective perspective = findPerspective(application, modelService);
		if(perspective == null) {
			return;
		}
		/*
		 * The Bug #408678 has been fixed since Eclipse 4.3.2
		 */
		MPerspectiveStack perspectiveStack = (MPerspectiveStack)modelService.find(IPerspectiveAndViewIds.STACK_PERSPECTIVES, application);
		perspectiveStack.setSelectedElement(perspective);
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, perspective.getElementId());
			scheduleSnapshot(application, modelService, perspectiveStack, eventBroker);
		}
	}

	/*
	 * Marks the action sets as hidden before the compatibility layer sets up its workbench page,
	 * which activates each initially visible action set in every perspective it knows.
	 */
	private void hideActionSets(MApplication application, EModelService modelService) {

		for(MPerspective perspective : modelService.findElements(application, null, MPerspective.class)) {
			Map<String, String> persistedState = perspective.getPersistedState();
			String hiddenItems = persistedState.getOrDefault(HIDDEN_ITEMS_KEY, "");
			StringBuilder builder = new StringBuilder(hiddenItems);
			for(String actionSetId : HIDDEN_ACTION_SET_IDS) {
				String hiddenItem = HIDDEN_ACTION_SET_PREFIX + actionSetId + ",";
				if(!hiddenItems.contains(hiddenItem)) {
					builder.append(hiddenItem);
				}
			}
			persistedState.put(HIDDEN_ITEMS_KEY, builder.toString());
		}
	}

	/*
	 * The perspective to be shown on startup is determined in the following order:
	 * 1. The perspective the user has marked as default in "General > Perspectives".
	 * 2. The perspective defined in the product definition, e.g.: -Dapplication.perspective=
	 * 3. The welcome perspective.
	 */
	private MPerspective findPerspective(MApplication application, EModelService modelService) {

		List<String> perspectiveIds = new ArrayList<>();
		perspectiveIds.add(PlatformUI.getPreferenceStore().getString(IWorkbenchPreferenceConstants.DEFAULT_PERSPECTIVE_ID));
		Properties properties = System.getProperties();
		if(properties.get(PROPERTY_PERSPECTIVE) instanceof String text) {
			perspectiveIds.add(text);
		}
		perspectiveIds.add(IPerspectiveAndViewIds.PERSPECTIVE_WELCOME);

		for(String perspectiveId : perspectiveIds) {
			if(perspectiveId != null && !perspectiveId.isEmpty() && modelService.find(perspectiveId, application) instanceof MPerspective perspective) {
				return perspective;
			}
		}

		return null;
	}

	/*
	 * The perspectives are snapshotted as snippets so that the reset perspective command can clone
	 * them later. Snapshotting is deferred until the UI has been rendered, because adding snippets
	 * while the model is still being processed disturbs the initial trim layout.
	 */
	private void scheduleSnapshot(MApplication application, EModelService modelService, MPerspectiveStack perspectiveStack, IEventBroker eventBroker) {

		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				eventBroker.unsubscribe(this);
				snapshotPerspectives(application, modelService, perspectiveStack);
			}
		});
	}

	private void snapshotPerspectives(MApplication application, EModelService modelService, MPerspectiveStack perspectiveStack) {

		for(MPerspective perspective : perspectiveStack.getChildren()) {
			String perspectiveId = perspective.getElementId();
			if(perspectiveId == null || perspectiveId.isEmpty()) {
				continue;
			}
			/*
			 * The model is rebuilt from the fragments each start and the snapshot is always pristine.
			 */
			if(modelService.findSnippet(application, perspectiveId) == null) {
				modelService.cloneElement(perspective, application);
			}
		}
	}
}
