/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - exclude unrelated import/export wizards
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.support.ui.workbench.WorkbenchAdvisorSupport;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	/*
	 * This is the default perspective.
	 */
	private static final String PERSPECTIVE_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {

		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {

		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
		WorkbenchAdvisorSupport.declareProjectExplorerImages(configurer);
		//
		WorkbenchPlugin defaultWorkbenchPlugin = WorkbenchPlugin.getDefault();
		removeWizards(defaultWorkbenchPlugin.getImportWizardRegistry());
		removeWizards(defaultWorkbenchPlugin.getExportWizardRegistry());
	}

	@Override
	public String getInitialWindowPerspectiveId() {

		return PERSPECTIVE_ID;
	}

	@Override
	public IAdaptable getDefaultPageInput() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}

	private static final String IMPORT_EXPORT_WIZARDS = "org\\.eclipse\\.ui\\.wizards\\.(import|export)\\.(?!Preferences).*";
	private static final String EQUINOX_WIZARDS = "org\\.eclipse\\.equinox\\.p2\\.replication.*";
	private static final String TEAM_E4_WIZARDS = "org\\.eclipse\\.(team|e4)\\.ui.*";

	private void removeWizards(IWizardRegistry wizardRegistry) {

		IWizardCategory[] categories = wizardRegistry.getRootCategory().getCategories();
		for(IWizardDescriptor wizard : getAllWizards(categories)) {
			if(wizard.getId().matches(IMPORT_EXPORT_WIZARDS) || wizard.getId().matches(EQUINOX_WIZARDS) || wizard.getId().matches(TEAM_E4_WIZARDS)) {
				WorkbenchWizardElement wizardElement = (WorkbenchWizardElement)wizard;
				AbstractExtensionWizardRegistry abstractWizardRegistry = (AbstractExtensionWizardRegistry)wizardRegistry;
				abstractWizardRegistry.removeExtension(wizardElement.getConfigurationElement().getDeclaringExtension(), new Object[]{wizardElement});
			}
		}
	}

	private IWizardDescriptor[] getAllWizards(IWizardCategory[] categories) {

		List<IWizardDescriptor> results = new ArrayList<>();
		for(IWizardCategory wizardCategory : categories) {
			results.addAll(Arrays.asList(wizardCategory.getWizards()));
			results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
		}
		return results.toArray(new IWizardDescriptor[0]);
	}
}