/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.logging.ui.swt.LogTableUI;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class LogFileEditor {

	public static final String ID = "org.eclipse.chemclipse.logging.ui.part.logfileEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.logging.ui/org.eclipse.chemclipse.logging.ui.editors.LogFileEditor";
	public static final String ICON_URI = IApplicationImage.getLocation(IApplicationImage.IMAGE_LOG, IApplicationImageProvider.SIZE_16x16);
	public static final String TOOLTIP = "Log File Editor";
	private static final Logger logger = Logger.getLogger(LogFileEditor.class);
	private MDirtyable dirtyable;
	private File file;
	private LogTableUI logTableUI;
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	private MPart inputPart;

	@Inject
	public LogFileEditor(Composite parent, MPart part, final MDirtyable dirtyable) {

		this.dirtyable = dirtyable;
		parent.setLayout(new FillLayout());
		this.inputPart = part;
		/*
		 * Set the text editor content.
		 */
		logTableUI = new LogTableUI(parent, SWT.NONE);
		logTableUI.setLayout(new FillLayout());
		try {
			Object object = part.getObject();
			if(object instanceof String) {
				file = new File((String)object);
				logTableUI.setContent(getInputFileContent());
			}
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	@Focus
	public void setFocus() {

		logTableUI.setFocus();
	}

	@PreDestroy
	private void preDestroy() {

		/*
		 * Remove the editor from the listed parts.
		 */
		if(modelService != null) {
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			inputPart.setToBeRendered(false);
			inputPart.setVisible(false);
			partStack.getChildren().remove(inputPart);
		}
	}

	@Persist
	public void save() {

		try (FileWriter fileWriter = new FileWriter(file)) {
			StringBuilder builder = new StringBuilder();
			for(String line : getInputFileContent()) {
				builder.append(line);
			}
			fileWriter.write(builder.toString());
			fileWriter.flush();
			dirtyable.setDirty(false);
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	private List<String> getInputFileContent() throws IOException {

		List<String> lines = new ArrayList<>();
		if(file != null) {
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
				String line;
				while((line = bufferedReader.readLine()) != null) {
					lines.add(line);
				}
			}
		}
		return lines;
	}
}
