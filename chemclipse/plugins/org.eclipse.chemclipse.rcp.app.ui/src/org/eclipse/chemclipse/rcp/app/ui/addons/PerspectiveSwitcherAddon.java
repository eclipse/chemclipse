/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Alexander Kurtakov - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.addons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;

import jakarta.annotation.PostConstruct;

/**
 * Some installations only use a single perspective. The perspective switcher is
 * left out of the trim bar in such a case. It lists the perspectives the user has
 * opened recently, the remaining ones are hidden until they are opened.
 */
@SuppressWarnings("restriction")
public class PerspectiveSwitcherAddon {

	/*
	 * The element ids are defined by the Eclipse platform and must not be changed.
	 * PerspectiveSwitcher.PERSPECTIVE_SWITCHER_ID is the id for model contributed
	 * switchers. The id "PerspectiveSwitcher" is reserved for the compatibility
	 * layer, which detaches such an element from the model unless the perspective
	 * bar has been requested via IWorkbenchWindowConfigurer.
	 */
	private static final String ELEMENT_ID_PERSPECTIVE_SWITCHER = "org.eclipse.e4.ui.PerspectiveSwitcher";
	private static final String ELEMENT_ID_PERSPECTIVE_SPACER = "PerspectiveSpacer";
	private static final String COMMAND_ID_RESET_PERSPECTIVE = "org.eclipse.chemclipse.rcp.app.ui.command.resetperspective";
	private static final int MAXIMUM_OPENED_PERSPECTIVES = 5;
	/*
	 * The least recently used perspective is listed first.
	 */
	private final Set<String> openedPerspectives = new LinkedHashSet<>();
	/*
	 * A perspective which is hidden is rendered again once it is opened, at a time it is not
	 * selected yet. Its placeholders don't host the shared elements, e.g. the data explorer or
	 * the editor area, hence the layout is reset when the perspective has been selected.
	 */
	private final Set<String> perspectivesToReset = new HashSet<>();
	private MApplication application;
	private EModelService modelService;
	private IEventBroker eventBroker;
	private MPerspectiveStack perspectiveStack;

	@PostConstruct
	public void postConstruct(MApplication application, EModelService modelService, IEventBroker eventBroker) {

		this.application = application;
		this.modelService = modelService;
		this.eventBroker = eventBroker;
		/*
		 * The tool controls are adjusted before the trim bar is rendered.
		 * The value is applied in both directions, otherwise a persisted state
		 * would keep the switcher hidden once the preference is enabled again.
		 */
		boolean showPerspectiveSwitcher = PreferenceSupplier.getShowPerspectiveSwitcher();
		setToBeRendered(ELEMENT_ID_PERSPECTIVE_SWITCHER, showPerspectiveSwitcher);
		setToBeRendered(ELEMENT_ID_PERSPECTIVE_SPACER, showPerspectiveSwitcher);
		PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_OPEN_ON_PERSPECTIVE_BAR, false);
		adjustPerspectiveMenu();
		if(modelService.find(IPerspectiveAndViewIds.STACK_PERSPECTIVES, application) instanceof MPerspectiveStack stack) {
			perspectiveStack = stack;
			hideClosedPerspectives();
			trackOpenedPerspectives();
		}
	}

	/*
	 * The context menu of the switcher is created by the platform, hence it is adjusted before it
	 * is shown. The menu is identified by the perspective the platform attaches to it, see
	 * PerspectiveSwitcher#openMenuFor.
	 */
	private void adjustPerspectiveMenu() {

		Display.getDefault().addFilter(SWT.Show, event -> {
			if(event.widget instanceof Menu menu && menu.getData() instanceof MPerspective perspective) {
				for(MenuItem menuItem : menu.getItems()) {
					String text = menuItem.getText();
					if(WorkbenchMessages.PerspectiveBar_customize.equals(text) || WorkbenchMessages.PerspectiveBar_saveAs.equals(text)) {
						menuItem.dispose();
					} else if(WorkbenchMessages.WorkbenchWindow_close.equals(text)) {
						int index = menu.indexOf(menuItem);
						menuItem.dispose();
						createCloseItem(menu, perspective, index);
					} else if(WorkbenchMessages.PerspectiveBar_reset.equals(text)) {
						int index = menu.indexOf(menuItem);
						menuItem.dispose();
						createResetItem(menu, index);
					}
				}
			}
		});
	}

	/*
	 * The perspective is hidden instead of being removed from the model. The platform would
	 * activate another perspective via the compatibility layer, which fails for the ones that
	 * are not rendered, see WorkbenchPage#closePerspective.
	 */
	private void createCloseItem(Menu menu, MPerspective perspective, int index) {

		MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
		menuItem.setText(WorkbenchMessages.WorkbenchWindow_close);
		menuItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(_ -> closePerspective(perspective)));
	}

	/*
	 * The platform resets the perspective via the compatibility layer, which drops the shared
	 * editor area. Execute the same command as the Window > Reset Perspective menu entry instead.
	 * Like the platform, the selected perspective is reset.
	 */
	private void createResetItem(Menu menu, int index) {

		MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
		menuItem.setText(WorkbenchMessages.PerspectiveBar_reset);
		menuItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(_ -> {
			MPerspective perspective = perspectiveStack.getSelectedElement();
			if(perspective != null) {
				resetPerspective(perspective);
			}
		}));
	}

	private void closePerspective(MPerspective perspective) {

		if(perspectiveStack.getSelectedElement() == perspective) {
			MPerspective replacement = findReplacement(perspective);
			if(replacement == null) {
				return;
			}
			replacement.setToBeRendered(true);
			perspectiveStack.setSelectedElement(replacement);
			eventBroker.post(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, replacement.getElementId());
		}
		hidePerspective(perspective);
	}

	/*
	 * The active part may belong to the hidden perspective, whose context is disposed with it.
	 * Only the parts of the active perspective are taken into account, hence the part service
	 * activates a valid one, see PartServiceImpl#requestActivation.
	 */
	private void hidePerspective(MPerspective perspective) {

		perspective.setToBeRendered(false);
		MWindow window = modelService.getTopLevelWindowFor(perspective);
		if(window == null || window.getContext() == null) {
			return;
		}
		EPartService partService = window.getContext().get(EPartService.class);
		if(partService != null) {
			partService.requestActivation();
		}
		perspectivesToReset.add(perspective.getElementId());
	}

	private void resetPerspective(MPerspective perspective) {

		MWindow window = modelService.getTopLevelWindowFor(perspective);
		if(window == null || window.getContext() == null) {
			return;
		}
		ECommandService commandService = window.getContext().get(ECommandService.class);
		EHandlerService handlerService = window.getContext().get(EHandlerService.class);
		if(commandService == null || handlerService == null) {
			return;
		}
		/*
		 * The command resets the perspective which is selected, hence it is executed once the
		 * perspective has been rendered.
		 */
		ParameterizedCommand command = commandService.createCommand(COMMAND_ID_RESET_PERSPECTIVE, Collections.emptyMap());
		if(command != null) {
			Display.getDefault().asyncExec(() -> {

				if(perspectiveStack.getSelectedElement() == perspective) {
					handlerService.executeHandler(command);
				}
			});
		}
	}

	/*
	 * The most recently used perspective takes over, the welcome perspective is opened if none
	 * of the others is rendered.
	 */
	private MPerspective findReplacement(MPerspective perspective) {

		List<String> recentlyUsed = new ArrayList<>(openedPerspectives);
		for(int index = recentlyUsed.size() - 1; index >= 0; index--) {
			if(modelService.find(recentlyUsed.get(index), perspectiveStack) instanceof MPerspective candidate && candidate != perspective && candidate.isToBeRendered()) {
				return candidate;
			}
		}
		if(modelService.find(IPerspectiveAndViewIds.PERSPECTIVE_WELCOME, perspectiveStack) instanceof MPerspective welcomePerspective && welcomePerspective != perspective) {
			return welcomePerspective;
		}
		return null;
	}

	private void setToBeRendered(String elementId, boolean toBeRendered) {

		List<MToolControl> toolControls = modelService.findElements(application, elementId, MToolControl.class, null);
		for(MToolControl toolControl : toolControls) {
			toolControl.setToBeRendered(toBeRendered);
		}
	}

	/*
	 * The perspectives which have not been opened yet are kept in the model, but are not rendered.
	 * Only the rendered ones are listed by the switcher.
	 */
	private void hideClosedPerspectives() {

		/*
		 * The perspective to be shown on startup has been selected by the PerspectiveApplicationAddon.
		 */
		MPerspective selectedPerspective = perspectiveStack.getSelectedElement();
		if(selectedPerspective == null) {
			return;
		}
		openedPerspectives.addAll(PreferenceSupplier.getOpenedPerspectives());
		for(MPerspective perspective : perspectiveStack.getChildren()) {
			if(perspective != selectedPerspective && !openedPerspectives.contains(perspective.getElementId())) {
				perspective.setToBeRendered(false);
				perspectivesToReset.add(perspective.getElementId());
			}
		}
		markAsRecentlyUsed(selectedPerspective.getElementId());
		hideLeastRecentlyUsedPerspectives();
		persistOpenedPerspectives();
	}

	private void trackOpenedPerspectives() {

		if(eventBroker == null) {
			return;
		}
		eventBroker.subscribe(UIEvents.UIElement.TOPIC_TOBERENDERED, event -> {
			if(event.getProperty(UIEvents.EventTags.ELEMENT) instanceof MPerspective perspective && perspectiveStack.getChildren().contains(perspective)) {
				if(perspective.isToBeRendered()) {
					markAsRecentlyUsed(perspective.getElementId());
					hideLeastRecentlyUsedPerspectives();
				} else {
					openedPerspectives.remove(perspective.getElementId());
				}
				persistOpenedPerspectives();
			}
		});
		eventBroker.subscribe(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT, event -> {
			if(event.getProperty(UIEvents.EventTags.ELEMENT) == perspectiveStack && perspectiveStack.getSelectedElement() instanceof MPerspective perspective) {
				markAsRecentlyUsed(perspective.getElementId());
				persistOpenedPerspectives();
				if(perspectivesToReset.remove(perspective.getElementId())) {
					resetPerspective(perspective);
				}
			}
		});
	}

	private void markAsRecentlyUsed(String perspectiveId) {

		if(perspectiveId != null) {
			openedPerspectives.remove(perspectiveId);
			openedPerspectives.add(perspectiveId);
		}
	}

	/*
	 * The switcher lists the most recently used perspectives only. The selected one is kept in
	 * any case, otherwise the window would be left without a rendered perspective.
	 */
	private void hideLeastRecentlyUsedPerspectives() {

		MPerspective selectedPerspective = perspectiveStack.getSelectedElement();
		for(String perspectiveId : new ArrayList<>(openedPerspectives)) {
			if(openedPerspectives.size() <= MAXIMUM_OPENED_PERSPECTIVES) {
				return;
			}
			if(selectedPerspective != null && perspectiveId.equals(selectedPerspective.getElementId())) {
				continue;
			}
			openedPerspectives.remove(perspectiveId);
			if(modelService.find(perspectiveId, perspectiveStack) instanceof MPerspective perspective) {
				hidePerspective(perspective);
			}
		}
	}

	private void persistOpenedPerspectives() {

		PreferenceSupplier.setOpenedPerspectives(openedPerspectives);
	}
}
