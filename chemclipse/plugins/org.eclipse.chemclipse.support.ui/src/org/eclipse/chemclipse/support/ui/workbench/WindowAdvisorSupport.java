/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.workbench;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

@SuppressWarnings("restriction")
public class WindowAdvisorSupport {

	/**
	 * Hides the give menu entries, e.g.:
	 * 
	 * menuId = "help"
	 * List<String> menuElementsToHide = new ArrayList<String>();
	 * menuElementsToHide.add("org.eclipse.equinox.p2.ui.sdk.update");
	 * menuElementsToHide.add("org.eclipse.equinox.p2.ui.sdk.install");
	 * menuElementsToHide.add("org.eclipse.equinox.p2.ui.sdk.installationDetails");
	 * 
	 * @param menuId
	 * @param menuElementsToHide
	 */
	public static void hideMenuEntries(String menuId, List<String> menuElementsToHide) {

		MWindow window = ((WorkbenchWindow)PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
		/*
		 * Remove menu elements
		 */
		MMenu mainMenu = window.getMainMenu();
		for(MMenuElement mainElement : mainMenu.getChildren()) {
			if(mainElement.getElementId().equals(menuId)) {
				if(mainElement instanceof MMenu) {
					MMenu helpMenu = (MMenu)mainElement;
					for(MMenuElement menuElement : helpMenu.getChildren()) {
						if(menuElementsToHide.contains(menuElement.getElementId())) {
							menuElement.setVisible(false);
							menuElement.setToBeRendered(false);
						}
					}
				}
			}
		}
	}

	/**
	 * Hides the QuickAccess search field from the top trim bar.
	 */
	public static void hideSearchField() {

		MWindow window = ((WorkbenchWindow)PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
		EModelService modelService = window.getContext().get(EModelService.class);
		/*
		 * Remove QuickAccess search field.
		 */
		MToolControl searchField = (MToolControl)modelService.find("SearchField", window);
		MTrimBar trimBarTop = modelService.getTrim((MTrimmedWindow)window, SideValue.TOP);
		trimBarTop.getChildren().remove(searchField);
		Control control = (Control)searchField.getWidget();
		control.setVisible(false);
	}
}
