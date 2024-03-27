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
 * Alexander Kerner - Generics
 * Christoph Läubrich - refactoring for new method API, optimize E4 access
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodFileSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ProcessMethodNotifications;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

public class ProcessMethodEditor implements IModificationHandler, IChemClipseEditor {

	private static final Logger logger = Logger.getLogger(ProcessMethodEditor.class);
	//
	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.processMethodEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor";
	public static final String ICON_URI = ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_METHOD, IApplicationImageProvider.SIZE_16x16);
	public static final String TOOLTIP = "Process Method Editor";
	//
	@Inject
	private MPart part;
	@Inject
	private MDirtyable dirtyable;
	@Inject
	private IProcessSupplierContext processSupplierContext;
	@Inject
	private ProcessMethodNotifications notifications;
	@Inject
	private PartSupport partsupport;
	//
	private File processMethodFile;
	private ExtendedMethodUI extendedMethodUI;
	private IProcessMethod currentProcessMethod;

	@Focus
	public void setFocus() {

		extendedMethodUI.setFocus();
	}

	@PreDestroy
	private void preDestroy() {

		partsupport.closePart(part);
	}

	@Persist
	public void save() {

		if(processMethodFile != null) {
			IProcessMethod oldMethod = currentProcessMethod;
			ProcessMethod newMethod = new ProcessMethod(extendedMethodUI.getProcessMethod());
			IProcessingInfo<?> processingInfo = MethodConverter.convert(processMethodFile, newMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
			if(processingInfo.hasErrorMessages()) {
				ProcessingInfoPartSupport.getInstance().update(processingInfo);
			} else {
				dirtyable.setDirty(false);
				currentProcessMethod = newMethod;
				notifications.updated(newMethod, oldMethod);
				UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_METHOD_UPDATE, currentProcessMethod);
			}
		}
	}

	@Override
	public boolean saveAs() {

		boolean saveSuccessful = false;
		ProcessMethod newMethod = new ProcessMethod(extendedMethodUI.getProcessMethod());
		newMethod.setName(null); // will be set after the filename was chosen
		try {
			saveSuccessful = MethodFileSupport.saveProccessMethod(Display.getDefault().getActiveShell(), newMethod);
			dirtyable.setDirty(!saveSuccessful);
			notifications.created(newMethod);
		} catch(NoConverterAvailableException e) {
			logger.warn(e);
		}
		return saveSuccessful;
	}

	@PostConstruct
	public void initialize(Composite parent) {

		currentProcessMethod = null;
		Object object = part.getObject();
		if(object instanceof Map<?, ?> map) {
			processMethodFile = new File((String)map.get(EditorSupport.MAP_FILE));
			currentProcessMethod = Adapters.adapt(processMethodFile, IProcessMethod.class);
		} else {
			processMethodFile = null;
		}
		/*
		 * Backward compatibility
		 */
		if(currentProcessMethod == null) {
			String message = "The current process method was not adapted correctly.";
			logger.warn(message);
			throw new RuntimeException(message);
		}
		//
		DataCategory[] categories = currentProcessMethod.getDataCategories().toArray(new DataCategory[]{});
		if(categories == null || categories.length == 0) {
			categories = DataCategory.chromatographyCategories();
		}
		//
		String processMethodName = currentProcessMethod.getName();
		logger.info("Process Method Name: " + processMethodName);
		String label = processMethodName.isEmpty() ? part.getLabel() : processMethodName;
		List<DataCategory> categoriesSorted = Arrays.asList(categories);
		Collections.sort(categoriesSorted, (c1, c2) -> c1.name().compareTo(c2.name()));
		part.setLabel(label + " " + categoriesSorted);
		/*
		 * It seems to happen that the process supplier context is null.
		 * Probably, it is not initialized in time. Hence this additional check.
		 * Further inspections are required to check why initialization has not been done yet.
		 */
		if(processSupplierContext == null) {
			String message = "The process supplier context has not been injected correctly.";
			logger.warn(message);
			throw new RuntimeException(message);
		}
		//
		extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE, processSupplierContext, categories);
		extendedMethodUI.setModificationHandler(this);
		extendedMethodUI.setProcessMethod(currentProcessMethod);
	}

	@Override
	public void setDirty(boolean dirty) {

		dirtyable.setDirty(!extendedMethodUI.getProcessMethod().equals(currentProcessMethod));
	}
}