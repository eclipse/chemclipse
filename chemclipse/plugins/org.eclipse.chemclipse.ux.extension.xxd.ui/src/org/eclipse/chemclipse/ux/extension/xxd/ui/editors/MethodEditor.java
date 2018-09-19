/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MethodEditor extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.methodEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.MethodEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/sequenceListDefault.gif";
	public static final String TOOLTIP = "Method Editor";
	//
	private MPart part;
	private MDirtyable dirtyable;
	//
	private File methodFile;
	private ExtendedMethodUI extendedMethodUI;

	@Inject
	public MethodEditor(Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
		super(part);
		//
		this.part = part;
		this.dirtyable = dirtyable;
		//
		initialize(parent);
	}

	@Override
	public void registerEvents() {

	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			//
		}
	}

	@Focus
	public void setFocus() {

		//
	}

	@PreDestroy
	private void preDestroy() {

		EModelService modelService = ModelSupportAddon.getModelService();
		if(modelService != null) {
			MApplication application = ModelSupportAddon.getApplication();
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			DisplayUtils.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					partStack.getChildren().remove(part);
				}
			});
		}
		/*
		 * Run the garbage collector.
		 */
		System.gc();
	}

	@Persist
	public void save() {

		System.out.println(methodFile);
		dirtyable.setDirty(false);
	}

	private void initialize(Composite parent) {

		createEditorPages(parent);
		extendedMethodUI.update(null); // TODO
	}

	private void createEditorPages(Composite parent) {

		createPage(parent);
	}

	private void createPage(Composite parent) {

		extendedMethodUI = new ExtendedMethodUI(parent);
	}
}
