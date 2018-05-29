/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.wizards;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.wizard.Wizard;
import org.osgi.service.prefs.BackingStoreException;

public class InputEntriesWizard extends Wizard {

	public enum TreeSelection {
		NONE, USER_LOCATION, DRIVER, HOME;
	}

	private InputEntriesWizardPage inputEntriesPage;
	private IChromatogramWizardElements chromatogramWizardElements;
	private IBaseLabelProvider labelProvider;
	private IContentProvider contentProvider;
	//
	private String title;
	private String description;
	private String selectedDrivePath;
	private String selectedHomePath;
	private String selectedUserLocationPath;
	private IEclipsePreferences eclipsePreferences;
	private String nodeName;

	public InputEntriesWizard(String title, String description, IBaseLabelProvider labelProvider, IContentProvider contentProvider) {
		super();
		setNeedsProgressMonitor(true);
		this.chromatogramWizardElements = new ChromatogramWizardElements();
		this.title = (title == null) ? "" : title;
		this.description = (description == null) ? "" : description;
		this.labelProvider = labelProvider;
		this.contentProvider = contentProvider;
	}

	public InputEntriesWizard(IChromatogramWizardElements chromatogramWizardElements, String title, String description, IBaseLabelProvider labelProvider, IContentProvider contentProvider) {
		this(title, description, labelProvider, contentProvider);
		this.chromatogramWizardElements = chromatogramWizardElements;
	}

	public void setEclipsePreferes(IEclipsePreferences eclipsePreferences, String nodeName) {

		if(eclipsePreferences != null && nodeName != null) {
			this.eclipsePreferences = eclipsePreferences;
			this.nodeName = nodeName;
			this.selectedDrivePath = eclipsePreferences.node(nodeName).get(TreeSelection.DRIVER.name(), "");
			this.selectedHomePath = eclipsePreferences.node(nodeName).get(TreeSelection.HOME.name(), "");
			this.selectedUserLocationPath = eclipsePreferences.node(nodeName).get(TreeSelection.USER_LOCATION.name(), "");
		}
	}

	@Override
	public void addPages() {

		inputEntriesPage = new InputEntriesWizardPage(chromatogramWizardElements, title, description, labelProvider, contentProvider, selectedDrivePath, selectedHomePath, selectedUserLocationPath);
		addPage(inputEntriesPage);
	}

	public void setDefaultFilePath() {

		selectedDrivePath = PreferenceSupplier.getSelectedDrivePath();
		selectedHomePath = PreferenceSupplier.getSelectedHomePath();
		selectedUserLocationPath = PreferenceSupplier.getSelectedUserLocationPath();
	}

	public IChromatogramWizardElements getChromatogramWizardElements() {

		return chromatogramWizardElements;
	}

	public void setSelectedDrivePath(String selectedDrivePath) {

		this.selectedDrivePath = selectedDrivePath;
	}

	public void setSelectedHomePath(String selectedHomePath) {

		this.selectedHomePath = selectedHomePath;
	}

	public void setSelectedUserLocationPath(String selectedUserLocationPath) {

		this.selectedUserLocationPath = selectedUserLocationPath;
	}

	public TreeSelection getTreeSelection() {

		return inputEntriesPage.getTreeSelection();
	}

	@Override
	public boolean performFinish() {

		if(eclipsePreferences != null && nodeName != null) {
			List<String> files = chromatogramWizardElements.getSelectedChromatograms();
			if(files != null && !files.isEmpty()) {
				if(!inputEntriesPage.getTreeSelection().equals(TreeSelection.NONE)) {
					File file = new File(files.get(0));
					if(file.isFile()) {
						eclipsePreferences.node(nodeName).put(inputEntriesPage.getTreeSelection().name(), file.getParent());
						try {
							eclipsePreferences.flush();
						} catch(BackingStoreException e) {
						}
					}
				}
			}
		}
		return true;
	}
}
