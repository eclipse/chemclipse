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
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.ui.activator.ContextAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * fragment.e4xmi
 * 
 * @Evaluate is used by the "Imperative Expression"
 * @Execute is used by "Direct Menu Item"
 * 
 */
public abstract class AbstractPartHandler implements IPartHandler {

	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication application;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public EPartService getPartService() {

		return partService != null ? partService : ContextAddon.getPartService();
	}

	public EModelService getModelService() {

		return modelService != null ? modelService : ContextAddon.getModelService();
	}

	public MApplication getApplication() {

		return application != null ? application : ContextAddon.getApplication();
	}

	@Evaluate
	public boolean isVisible(IEclipseContext context) {

		return isPartStackAssigned();
	}

	@Execute
	public void execute() {

		toggleVisibility();
	}

	@Override
	public boolean isPartStackAssigned() {

		String partStackId = preferenceStore.getString(getPartStackReference().getStackPositionKey());
		if(partStackId.isEmpty() || PartSupport.PARTSTACK_NONE.equals(partStackId)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isPartVisible() {

		boolean isVisible = false;
		String partId = getPartStackReference().getPartId();
		EModelService modelService = getModelService();
		MApplication application = getApplication();
		//
		isVisible = PartSupport.isPartVisible(partId, modelService, application);
		if(isVisible) {
			isVisible = PartSupport.isPartToBeRendered(partId, modelService, application);
		}
		//
		return isVisible;
	}

	@Override
	public void action(boolean show) {

		String partId = getPartStackReference().getPartId();
		EPartService partService = getPartService();
		EModelService modelService = getModelService();
		MApplication application = getApplication();
		//
		if(isPartStackAssigned()) {
			/*
			 * Part Stack
			 */
			String stackPositionKey = getPartStackReference().getStackPositionKey();
			String partStackId = preferenceStore.getString(stackPositionKey);
			/*
			 * Show/Hide the part.
			 */
			PartSupport.setPartVisibility(partId, partStackId, false, partService, modelService, application);
			if(show) {
				PartSupport.togglePartVisibility(partId, partStackId, partService, modelService, application);
			}
		} else {
			/*
			 * If the part stack has been set to "--", hide it.
			 */
			MPart part = PartSupport.getPart(partId, modelService, application);
			if(part != null && part.isVisible()) {
				part.setVisible(false);
			}
		}
	}

	@Override
	public void toggleVisibility() {

		boolean show = !isPartVisible();
		action(show);
	}
}
