/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard.TreeSelection;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.osgi.service.prefs.BackingStoreException;

public class InputWizardSettings {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	//
	private static final Logger logger = Logger.getLogger(InputEntriesWizard.class);
	private static final String DEFAULT_TREE_SELECTION = "DEFAULT_TREE_SELECTION";

	public enum DataType {
		MSD_CHROMATOGRAM, //
		MSD_PEAKS, //
		CSD_CHROMATOGRAM, //
		WSD_CHROMATOGRAM;
	}

	private String title = "Title";
	private String description = "Description";
	private IBaseLabelProvider labelProvider = null;
	private IContentProvider contentProvider = null;
	private IEclipsePreferences eclipsePreferences = null;
	private String nodeName = "";
	//
	private IChromatogramWizardElements chromatogramWizardElements = new ChromatogramWizardElements();
	//
	private String selectedDrivePath = "";
	private String selectedHomePath = "";
	private String selectedUserLocationPath = "";
	private TreeSelection defaultTree = TreeSelection.NONE;

	public InputWizardSettings(DataType dataType) {
		this(new DataType[]{dataType});
	}

	public InputWizardSettings(DataType[] dataTypes) {
		/*
		 * Collect the items.
		 */
		List<ISupplierFileIdentifier> supplierFileIdentifierList = new ArrayList<>();
		for(DataType dataType : dataTypes) {
			switch(dataType) {
				case MSD_CHROMATOGRAM:
					supplierFileIdentifierList.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceIdentifier());
					break;
				case MSD_PEAKS:
					supplierFileIdentifierList.add(org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceIdentifier());
					break;
				case CSD_CHROMATOGRAM:
					supplierFileIdentifierList.add(org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport.getInstanceIdentifier());
					break;
				case WSD_CHROMATOGRAM:
					supplierFileIdentifierList.add(org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport.getInstanceIdentifier());
					break;
				default:
					break;
			}
		}
		/*
		 * Assign the label and content provider.
		 */
		this.labelProvider = new DataExplorerLabelProvider(supplierFileIdentifierList);
		this.contentProvider = new DataExplorerContentProvider(supplierFileIdentifierList);
	}

	public void saveSelectedPath(TreeSelection treeSelection) {

		if(eclipsePreferences != null && nodeName != null) {
			List<String> files = chromatogramWizardElements.getSelectedChromatograms();
			if(files != null && !files.isEmpty()) {
				if(!treeSelection.equals(TreeSelection.NONE)) {
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
						String name = treeSelection.name();
						eclipsePreferences.node(nodeName).put(name, directory.getAbsolutePath());
						eclipsePreferences.node(nodeName).put(DEFAULT_TREE_SELECTION, name);
						try {
							eclipsePreferences.flush();
						} catch(BackingStoreException e) {
							logger.warn(e);
						}
					}
				}
			}
		}
	}

	public String getTitle() {

		return (title == null) ? "" : title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getDescription() {

		return (description == null) ? "" : description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public IBaseLabelProvider getLabelProvider() {

		return labelProvider;
	}

	public IContentProvider getContentProvider() {

		return contentProvider;
	}

	public IEclipsePreferences getEclipsePreferences() {

		return eclipsePreferences;
	}

	public String getNodeName() {

		return nodeName;
	}

	public void setPathPreferences(IEclipsePreferences eclipsePreferences, String nodeName) {

		this.eclipsePreferences = eclipsePreferences;
		this.nodeName = nodeName;
		initializePathSelection();
	}

	public IChromatogramWizardElements getChromatogramWizardElements() {

		return chromatogramWizardElements;
	}

	public String getSelectedDrivePath() {

		return selectedDrivePath;
	}

	protected void setSelectedDrivePath(String selectedDrivePath) {

		this.selectedDrivePath = selectedDrivePath;
	}

	public String getSelectedHomePath() {

		return selectedHomePath;
	}

	protected void setSelectedHomePath(String selectedHomePath) {

		this.selectedHomePath = selectedHomePath;
	}

	public String getSelectedUserLocationPath() {

		return selectedUserLocationPath;
	}

	protected void setSelectedUserLocationPath(String selectedUserLocationPath) {

		this.selectedUserLocationPath = selectedUserLocationPath;
	}

	public TreeSelection getDefaultTree() {

		return defaultTree;
	}

	protected void setDefaultTree(TreeSelection defaultTree) {

		this.defaultTree = defaultTree;
	}

	private void initializePathSelection() {

		if(eclipsePreferences != null && nodeName != null) {
			setSelectedDrivePath(eclipsePreferences.node(nodeName).get(TreeSelection.DRIVES.name(), ""));
			setSelectedHomePath(eclipsePreferences.node(nodeName).get(TreeSelection.HOME.name(), ""));
			setSelectedUserLocationPath(eclipsePreferences.node(nodeName).get(TreeSelection.USER_LOCATION.name(), ""));
			setDefaultTree(TreeSelection.valueOf(eclipsePreferences.node(nodeName).get(DEFAULT_TREE_SELECTION, TreeSelection.DRIVES.name())));
		}
	}
}
