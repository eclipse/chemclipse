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

	private final static String DEFAULT_TREE_SELECTION = "DEFAULT_TREE_SELECTION";

	public enum TreeSelection {
		DRIVER, HOME, USER_LOCATION, NONE;
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
	private TreeSelection defaultTree;

	public InputEntriesWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public InputEntriesWizard(IChromatogramWizardElements chromatogramWizardElements) {
		this();
		this.chromatogramWizardElements = chromatogramWizardElements;
	}

	protected void init(String title, String description, IBaseLabelProvider labelProvider, IContentProvider contentProvider) {

		this.chromatogramWizardElements = new ChromatogramWizardElements();
		this.title = (title == null) ? "" : title;
		this.description = (description == null) ? "" : description;
		this.labelProvider = labelProvider;
		this.contentProvider = contentProvider;
		this.defaultTree = TreeSelection.NONE;
	}

	public void setEclipsePreferes(IEclipsePreferences eclipsePreferences, String nodeName) {

		if(eclipsePreferences != null && nodeName != null) {
			this.eclipsePreferences = eclipsePreferences;
			this.nodeName = nodeName;
			this.selectedDrivePath = eclipsePreferences.node(nodeName).get(TreeSelection.DRIVER.name(), "");
			this.selectedHomePath = eclipsePreferences.node(nodeName).get(TreeSelection.HOME.name(), "");
			this.selectedUserLocationPath = eclipsePreferences.node(nodeName).get(TreeSelection.USER_LOCATION.name(), "");
			this.defaultTree = TreeSelection.valueOf(eclipsePreferences.node(nodeName).get(DEFAULT_TREE_SELECTION, TreeSelection.DRIVER.name()));
		}
	}

	@Override
	public void addPages() {

		inputEntriesPage = new InputEntriesWizardPage(chromatogramWizardElements, title, description, labelProvider, contentProvider, selectedDrivePath, selectedHomePath, selectedUserLocationPath, defaultTree);
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

	public void setDefaultTree(TreeSelection defaultTree) {

		this.defaultTree = defaultTree;
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
						eclipsePreferences.node(nodeName).put(DEFAULT_TREE_SELECTION, inputEntriesPage.getTreeSelection().name());
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
