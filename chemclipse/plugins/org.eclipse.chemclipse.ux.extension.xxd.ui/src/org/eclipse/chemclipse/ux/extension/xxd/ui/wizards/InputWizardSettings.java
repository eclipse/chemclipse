/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Christoph Läubrich - support new lazy table model, support NMR_SCANs as InputDataType
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard.TreeSelection;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

public class InputWizardSettings {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	//
	private static final Logger logger = Logger.getLogger(InputEntriesWizard.class);
	private static final String DEFAULT_TREE_SELECTION = "DEFAULT_TREE_SELECTION";

	public enum InputDataType {
		MSD_CHROMATOGRAM, //
		MSD_PEAKS, //
		CSD_CHROMATOGRAM, //
		WSD_CHROMATOGRAM, NMR_SCANS;
	}

	private String title = "Title";
	private String description = "Description";
	private IEclipsePreferences eclipsePreferences = null;
	private String nodeName = "";
	//
	private IChromatogramWizardElements chromatogramWizardElements = new ChromatogramWizardElements();
	//
	private String selectedDrivePath = "";
	private String selectedHomePath = "";
	private String selectedUserLocationPath = "";
	private TreeSelection defaultTree = TreeSelection.NONE;
	private List<ISupplierFileIdentifier> supplierFileIdentifierList;

	public InputWizardSettings(InputDataType dataType) {
		this(new InputDataType[]{dataType});
	}

	public InputWizardSettings(InputDataType[] dataTypes) {
		supplierFileIdentifierList = new ArrayList<>();
		for(InputDataType dataType : dataTypes) {
			switch(dataType) {
				case MSD_CHROMATOGRAM:
					supplierFileIdentifierList.add(new EditorSupportFactory(DataType.MSD).getInstanceIdentifier());
					break;
				case MSD_PEAKS:
					supplierFileIdentifierList.add(new EditorSupportFactory(DataType.MSD).getInstanceIdentifier());
					break;
				case CSD_CHROMATOGRAM:
					supplierFileIdentifierList.add(new EditorSupportFactory(DataType.CSD).getInstanceIdentifier());
					break;
				case WSD_CHROMATOGRAM:
					supplierFileIdentifierList.add(new EditorSupportFactory(DataType.WSD).getInstanceIdentifier());
					break;
				case NMR_SCANS:
					supplierFileIdentifierList.add(new EditorSupportFactory(DataType.NMR).getInstanceIdentifier());
					break;
				default:
					break;
			}
		}
		/*
		 * Assign the label and content provider.
		 */
	}

	public List<ISupplierFileIdentifier> getSupplierFileIdentifierList() {

		return supplierFileIdentifierList;
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
