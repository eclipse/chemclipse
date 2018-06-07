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
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.wizard.Wizard;
import org.osgi.service.prefs.BackingStoreException;

public class InputEntriesWizard extends Wizard {

	private static final Logger logger = Logger.getLogger(InputEntriesWizard.class);
	private static final String DEFAULT_TREE_SELECTION = "DEFAULT_TREE_SELECTION";

	public enum TreeSelection {
		DRIVES, //
		HOME, //
		USER_LOCATION, //
		NONE;
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

	public InputEntriesWizard(InputWizardSettings inputWizardSettings) {
		setNeedsProgressMonitor(true);
		init(inputWizardSettings);
	}

	@Override
	public void addPages() {

		inputEntriesPage = new InputEntriesWizardPage(chromatogramWizardElements, title, description, labelProvider, contentProvider, selectedDrivePath, selectedHomePath, selectedUserLocationPath, defaultTree);
		addPage(inputEntriesPage);
	}

	public IChromatogramWizardElements getChromatogramWizardElements() {

		return chromatogramWizardElements;
	}

	@Override
	public boolean performFinish() {

		if(eclipsePreferences != null && nodeName != null) {
			List<String> files = chromatogramWizardElements.getSelectedChromatograms();
			if(files != null && !files.isEmpty()) {
				if(!inputEntriesPage.getTreeSelection().equals(TreeSelection.NONE)) {
					/*
					 * Get the parent directory.
					 */
					File file = new File(files.get(0));
					File directory = null;
					if(file.isFile()) {
						directory = file.getParentFile();
					} else if(file.isDirectory()) {
						directory = file;
					}
					/*
					 * Save the directory.
					 */
					if(directory != null) {
						eclipsePreferences.node(nodeName).put(inputEntriesPage.getTreeSelection().name(), directory.getAbsolutePath());
						eclipsePreferences.node(nodeName).put(DEFAULT_TREE_SELECTION, inputEntriesPage.getTreeSelection().name());
						try {
							eclipsePreferences.flush();
						} catch(BackingStoreException e) {
							logger.warn(e);
						}
					}
				}
			}
		}
		return true;
	}

	private void init(InputWizardSettings inputWizardSettings) {

		/*
		 * The wizard elements are used to store the selected files.
		 */
		this.chromatogramWizardElements = new ChromatogramWizardElements();
		/*
		 * Title, etc.
		 */
		this.title = (inputWizardSettings.getTitle() == null) ? "" : (inputWizardSettings.getTitle());
		this.description = (inputWizardSettings.getDescription() == null) ? "" : inputWizardSettings.getDescription();
		this.labelProvider = inputWizardSettings.getLabelProvider();
		this.contentProvider = inputWizardSettings.getContentProvider();
		this.defaultTree = TreeSelection.NONE;
		/*
		 * Persist the path of the selected file.
		 */
		this.eclipsePreferences = inputWizardSettings.getEclipsePreferences();
		this.nodeName = inputWizardSettings.getNodeName();
		//
		if(eclipsePreferences != null && nodeName != null) {
			this.selectedDrivePath = eclipsePreferences.node(nodeName).get(TreeSelection.DRIVES.name(), "");
			this.selectedHomePath = eclipsePreferences.node(nodeName).get(TreeSelection.HOME.name(), "");
			this.selectedUserLocationPath = eclipsePreferences.node(nodeName).get(TreeSelection.USER_LOCATION.name(), "");
			this.defaultTree = TreeSelection.valueOf(eclipsePreferences.node(nodeName).get(DEFAULT_TREE_SELECTION, TreeSelection.DRIVES.name()));
		}
	}
}
