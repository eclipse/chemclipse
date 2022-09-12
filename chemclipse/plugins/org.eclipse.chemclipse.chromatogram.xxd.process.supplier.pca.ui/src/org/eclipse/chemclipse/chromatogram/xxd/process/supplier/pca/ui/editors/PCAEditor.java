/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Dr. Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt.AnalysisEditorUI;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class PCAEditor {

	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui/org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.PCAEditor";
	public static final String ICON_URI = IApplicationImage.getLocation(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImageProvider.SIZE_16x16);
	public static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.PCAEditor";
	public static final String LABEL = "PCA Editor";
	public static final String TOOL_TIPS = "PCA Editor";
	//
	@Inject
	private Composite parent;
	@Inject
	private IEclipseContext context;
	//
	private AnalysisEditorUI analysisEditorUI;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void createControl() {

		analysisEditorUI = new AnalysisEditorUI(parent, SWT.NONE);
		if(context != null) {
			ISamplesPCA<IVariable, ISample> samples = context.get(ISamplesPCA.class);
			analysisEditorUI.setInput(samples);
		}
	}

	@PreDestroy
	public void preDestroy() {

		if(context != null) {
			context.remove(ISamplesPCA.class);
		}
		//
		List<String> clearTopics = Arrays.asList(IChemClipseEvents.TOPIC_PCA_UPDATE_SELECTION);
		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_EDITOR_PCA_CLOSE, clearTopics);
	}

	@Focus
	public void setFocus() {

		analysisEditorUI.setFocus();
	}
}