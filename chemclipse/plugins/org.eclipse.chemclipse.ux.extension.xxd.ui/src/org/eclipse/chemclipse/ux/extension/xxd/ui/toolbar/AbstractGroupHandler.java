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
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.activator.ContextAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractGroupHandler implements IGroupHandler {

	private static final String COMMAND_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.command.partHandler";
	private static final String SETTINGS_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.SettingsHandler";
	private static final String ACTION_CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.ActionHandler";
	//
	private static final String TOOL_ITEM_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.directtoolitem";
	private static final String HANDLED_MENU_ITEM = "org.eclipse.chemclipse.ux.extension.xxd.ui.handledmenuitem";
	private static final String DIRECT_MENU_ITEM = "org.eclipse.chemclipse.ux.extension.xxd.ui.directmenuitem";
	private static final String PREFIX_MENU_SEPARATOR = "org.eclipse.chemclipse.ux.extension.xxd.ui.menuseparator";

	@Execute
	public void execute(MDirectToolItem directToolItem) {

		setPartStatus(directToolItem, GroupHandler.toggleShow(getName()));
	}

	@Override
	public List<IPartHandler> getPartHandler() {

		List<IPartHandler> partHandler = new ArrayList<>();
		partHandler.addAll(getPartHandlerMandatory());
		partHandler.addAll(getPartHandlerAdditional());
		return partHandler;
	}

	@Override
	public void setPartStatus(boolean visible) {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		//
		if(modelService != null && application != null) {
			/*
			 * Try to get tool item to modify the tooltip and image.
			 */
			MDirectToolItem directToolItem = getDirectToolItem();
			Display display = Display.getDefault();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					GroupHandler.setShow(getName(), visible);
					setPartStatus(directToolItem, visible);
					updateMenu();
				}
			});
		}
	}

	@Override
	public void updateMenu() {

		EModelService modelService = ContextAddon.getModelService();
		if(modelService != null) {
			/*
			 * Adjust the menu.
			 */
			MDirectToolItem directToolItem = getDirectToolItem();
			Display display = Display.getDefault();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					updateMenuItems(directToolItem, modelService);
				}
			});
		}
	}

	@Override
	public String getPartElementId(IPartHandler partHandler) {

		return HANDLED_MENU_ITEM + "." + getGroupHandlerId() + "." + getPartHandlerId(partHandler);
	}

	@Override
	public String getSettingsElementId() {

		return DIRECT_MENU_ITEM + "." + getGroupHandlerId();
	}

	@Override
	public String getActionElementId(Action action) {

		return DIRECT_MENU_ITEM + "." + getGroupHandlerId() + "." + action.id();
	}

	private void adjustIcon(MDirectToolItem directToolItem, boolean show) {

		if(directToolItem != null) {
			String iconHide = ApplicationImageFactory.getInstance().getURI(getImageHide(), IApplicationImageProvider.SIZE_16x16);
			String iconShow = ApplicationImageFactory.getInstance().getURI(getImageShow(), IApplicationImageProvider.SIZE_16x16);
			directToolItem.setIconURI(show ? iconHide : iconShow);
		}
	}

	private void setPartStatus(MDirectToolItem directToolItem, boolean show) {

		adjustIcon(directToolItem, show);
		/*
		 * If parts are activated, only activate the mandatory parts.
		 * When disable parts, disable all parts.
		 */
		List<IPartHandler> partHandlers;
		if(show) {
			partHandlers = getPartHandlerMandatory();
		} else {
			partHandlers = getPartHandler();
		}
		//
		for(IPartHandler partHandler : partHandlers) {
			partHandler.action(show);
		}
	}

	private MDirectToolItem getDirectToolItem() {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(modelService != null && application != null) {
			String toolItemId = getDirectToolItemId();
			return PartSupport.getDirectToolItem(toolItemId, modelService, application);
		}
		//
		return null;
	}

	private MCommand getCommand() {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		if(modelService != null && application != null) {
			Object object = modelService.findElements(application, COMMAND_ID, MCommand.class, Collections.emptyList()).get(0);
			if(object instanceof MCommand command) {
				return command;
			}
		}
		//
		return null;
	}

	private MHandledMenuItem getHandledItem(MMenu menu, String id) {

		MMenuElement menuElement = get(menu, id);
		if(menuElement instanceof MHandledMenuItem handledMenuItem) {
			return handledMenuItem;
		}
		//
		return null;
	}

	private MDirectMenuItem getDirectItem(MMenu menu, String id) {

		MMenuElement menuElement = get(menu, id);
		if(menuElement instanceof MDirectMenuItem directMenuItem) {
			return directMenuItem;
		}
		//
		return null;
	}

	private MMenuSeparator getSeparatorItem(MMenu menu, String id) {

		MMenuElement menuElement = get(menu, id);
		if(menuElement instanceof MMenuSeparator menuSeparator) {
			return menuSeparator;
		}
		//
		return null;
	}

	private MMenuElement get(MMenu menu, String id) {

		for(MMenuElement menuElement : menu.getChildren()) {
			if(id.equals(menuElement.getElementId())) {
				return menuElement;
			}
		}
		//
		return null;
	}

	private void updateMenuItems(MDirectToolItem directToolItem, EModelService modelService) {

		List<MenuContribution> menuContributions = new ArrayList<>();
		MMenu menu = directToolItem.getMenu();
		//
		List<IPartHandler> partHandlersMandatory = getPartHandlerMandatory();
		List<IPartHandler> partHandlersAdditional = getPartHandlerAdditional();
		int offset = partHandlersMandatory.size();
		//
		populateHandler(menu, modelService, partHandlersMandatory, menuContributions, 0);
		populateHandlerAdditional(menu, modelService, partHandlersAdditional, menuContributions, offset);
		populateActionMenu(menu, modelService, menuContributions);
		populateSettingsMenu(menu, modelService, menuContributions);
		//
		addMenuItems(menu, menuContributions);
	}

	private void populateHandler(MMenu menu, EModelService modelService, List<IPartHandler> partHandlers, List<MenuContribution> menuContributions, int offset) {

		MCommand command = getCommand();
		int size = partHandlers.size();
		for(int i = 0; i < size; i++) {
			IPartHandler partHandler = partHandlers.get(i);
			String partElementId = getPartElementId(partHandler);
			MHandledMenuItem menuItem = getHandledItem(menu, partElementId);
			if(menuItem == null) {
				/*
				 * Create a new menu item.
				 */
				menuItem = modelService.createModelElement(MHandledMenuItem.class);
				menuItem.setElementId(partElementId);
				menuItem.setLabel(partHandler.getName());
				menuItem.setTooltip("");
				menuItem.setIconURI(partHandler.getIconURI());
				menuItem.setCommand(command);
				/*
				 * Place the items in the correct order.
				 */
				menuContributions.add(new MenuContribution(menuItem, offset + i));
			}
			/*
			 * Adjust the label.
			 */
			String prefix = partHandler.isPartVisible() ? Action.HIDE.label() + " " : Action.SHOW.label() + " ";
			String label = prefix + partHandler.getName();
			menuItem.setLabel(label);
			/*
			 * If the user has defined to use the part, show it.
			 */
			menuItem.setVisible(partHandler.isPartStackAssigned());
		}
	}

	private void populateHandlerAdditional(MMenu menu, EModelService modelService, List<IPartHandler> partHandlers, List<MenuContribution> menuContributions, int offset) {

		if(!partHandlers.isEmpty()) {
			/*
			 * Add the separator if not available yet.
			 */
			String separatorId = getAdditionalSeparatorId();
			populateSeparator(menu, separatorId, modelService, menuContributions);
			populateHandler(menu, modelService, partHandlers, menuContributions, offset + 1);
		}
	}

	private void populateActionMenu(MMenu menu, EModelService modelService, List<MenuContribution> menuContributions) {

		/*
		 * Add the separator if not available yet.
		 */
		String separatorId = getActionsSeparatorId();
		populateSeparator(menu, separatorId, modelService, menuContributions);
		/*
		 * Activate
		 */
		String activateElementId = getActionElementId(Action.SHOW);
		MDirectMenuItem activateMenuItem = getDirectItem(menu, activateElementId);
		if(activateMenuItem == null) {
			/*
			 * Create a new settings item.
			 */
			MDirectMenuItem menuItem = modelService.createModelElement(MDirectMenuItem.class);
			menuItem.setElementId(activateElementId);
			menuItem.setLabel(Action.SHOW.label());
			menuItem.setTooltip("Activate all mandatory parts.");
			menuItem.setIconURI(ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_CHECK_ALL, IApplicationImageProvider.SIZE_16x16));
			menuItem.setContributionURI(ACTION_CONTRIBUTION_URI);
			menuContributions.add(new MenuContribution(menuItem));
		}
		/*
		 * Deactivate
		 */
		String deactivateElementId = getActionElementId(Action.HIDE);
		MDirectMenuItem deactivateMenuItem = getDirectItem(menu, deactivateElementId);
		if(deactivateMenuItem == null) {
			/*
			 * Create a new settings item.
			 */
			MDirectMenuItem menuItem = modelService.createModelElement(MDirectMenuItem.class);
			menuItem.setElementId(deactivateElementId);
			menuItem.setLabel(Action.HIDE.label());
			menuItem.setTooltip("Deactivate all parts.");
			menuItem.setIconURI(ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_UNCHECK_ALL, IApplicationImageProvider.SIZE_16x16));
			menuItem.setContributionURI(ACTION_CONTRIBUTION_URI);
			menuContributions.add(new MenuContribution(menuItem));
		}
	}

	private void populateSettingsMenu(MMenu menu, EModelService modelService, List<MenuContribution> menuContributions) {

		/*
		 * Add the separator if not available yet.
		 */
		String separatorId = getSettingsSeparatorId();
		populateSeparator(menu, separatorId, modelService, menuContributions);
		//
		String settingsElementId = getSettingsElementId();
		MDirectMenuItem settingsMenuItem = getDirectItem(menu, settingsElementId);
		if(settingsMenuItem == null) {
			/*
			 * Create a new settings item.
			 */
			MDirectMenuItem menuItem = modelService.createModelElement(MDirectMenuItem.class);
			menuItem.setElementId(settingsElementId);
			menuItem.setLabel("Settings");
			menuItem.setTooltip("Settings to show/hide parts.");
			menuItem.setIconURI(ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_PREFERENCES, IApplicationImageProvider.SIZE_16x16));
			menuItem.setContributionURI(SETTINGS_CONTRIBUTION_URI);
			menuContributions.add(new MenuContribution(menuItem));
		}
	}

	private void populateSeparator(MMenu menu, String separatorId, EModelService modelService, List<MenuContribution> menuContributions) {

		MMenuSeparator menuSeparator = getSeparatorItem(menu, separatorId);
		if(menuSeparator == null) {
			menuSeparator = modelService.createModelElement(MMenuSeparator.class);
			menuSeparator.setElementId(separatorId);
			menuContributions.add(new MenuContribution(menuSeparator));
		}
	}

	private void addMenuItems(MMenu menu, List<MenuContribution> menuContributions) {

		List<MMenuElement> menuElements = menu.getChildren();
		for(MenuContribution menuContribution : menuContributions) {
			/*
			 * Add menu item at the given position or append it at the end.
			 */
			MMenuElement menuElement = menuContribution.getMenuElement();
			int position = menuContribution.getIndex();
			if(position >= 0) {
				menuElements.add(position, menuElement);
			} else {
				menuElements.add(menuElement);
			}
		}
	}

	private String getDirectToolItemId() {

		return TOOL_ITEM_ID + "." + getGroupHandlerId();
	}

	private String getAdditionalSeparatorId() {

		return getSeparatorId("additional");
	}

	private String getActionsSeparatorId() {

		return getSeparatorId("actions");
	}

	private String getSettingsSeparatorId() {

		return getSeparatorId("settings");
	}

	private String getSeparatorId(String postfix) {

		return PREFIX_MENU_SEPARATOR + "." + getGroupHandlerId() + "." + postfix;
	}

	private String getPartHandlerId(IPartHandler partHandler) {

		return normalize(partHandler.getName());
	}

	private String getGroupHandlerId() {

		return normalize(getName());
	}

	private String normalize(String value) {

		return value.toLowerCase().replaceAll("[^a-z]", "").trim();
	}
}