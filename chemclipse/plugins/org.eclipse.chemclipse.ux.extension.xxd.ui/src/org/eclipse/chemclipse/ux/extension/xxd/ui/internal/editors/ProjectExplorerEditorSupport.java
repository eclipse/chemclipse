/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractSupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class ProjectExplorerEditorSupport extends AbstractSupplierFileEditorSupport implements ISupplierEditorSupport {

	private static final Logger logger = Logger.getLogger(ProjectExplorerEditorSupport.class);
	//
	private String type = "";
	private static final String DATA_EXPLORER = ExtensionMessages.dataExplorer;

	public ProjectExplorerEditorSupport(DataType dataType) {

		super(getSupplier(dataType));
		initialize(dataType);
	}

	private static List<ISupplier> getSupplier(DataType dataType) {

		List<ISupplier> supplier = new ArrayList<>();
		switch(dataType) {
			case CAL:
				supplier.add(new CalibrationFileSupplier());
				break;
			default:
				// No action
		}
		//
		return supplier;
	}

	private void initialize(DataType dataType) {

		switch(dataType) {
			case CAL:
				type = TYPE_CAL;
				break;
			default:
				type = "";
		}
	}

	@Override
	public String getType() {

		return type;
	}

	@Override
	public boolean openEditor(File file, Map<HeaderField, String> headerMap) {

		return openEditor(file, headerMap, false);
	}

	@Override
	public boolean openEditor(final File file, Map<HeaderField, String> headerMap, boolean batch) {

		boolean success = false;
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project = workspace.getRoot().getProject(DATA_EXPLORER);
			/*
			 * Create and open the file explorer project to link the files from the
			 * local file system.
			 */
			if(!project.exists()) {
				project.create(null);
			}
			//
			if(!project.isOpen()) {
				project.open(null);
			}
			/*
			 * Resource will be replaced on an open event.
			 */
			IPath path = new Path(file.getAbsolutePath());
			IFile input = project.getFile(path.lastSegment());
			input.createLink(path, IResource.REPLACE, null);
			//
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if(page != null) {
				IDE.openEditor(page, input);
				success = true;
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		return success;
	}

	@Override
	public void openEditor(IMeasurement measurement) {

	}

	@Override
	public void openOverview(final File file) {

	}

	@Override
	public void openOverview(IMeasurementInfo measurementInfo) {

	}

	@Override
	public boolean openEditor(File file, Map<HeaderField, String> headerMap, ISupplier supplier) {

		return openEditor(file, headerMap);
	}
}