/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.MethodImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ProcessMethodEditor extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private static final Logger logger = Logger.getLogger(ProcessMethodEditor.class);
	//
	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.processMethodEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/method.gif";
	public static final String TOOLTIP = "Process Method Editor";
	//
	private MPart part;
	private MDirtyable dirtyable;
	//
	private File processMethodFile;
	private ProcessMethod processMethod;
	private ExtendedMethodUI extendedMethodUI;
	//
	private Shell shell;

	@Inject
	public ProcessMethodEditor(Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
		super(part);
		//
		this.part = part;
		this.dirtyable = dirtyable;
		this.shell = shell;
		//
		initialize(parent);
	}

	@Override
	public void registerEvents() {

	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			//
		}
	}

	@Focus
	public void setFocus() {

		//
	}

	@PreDestroy
	private void preDestroy() {

		EModelService modelService = ModelSupportAddon.getModelService();
		if(modelService != null) {
			MApplication application = ModelSupportAddon.getApplication();
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			DisplayUtils.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					partStack.getChildren().remove(part);
				}
			});
		}
		/*
		 * Run the garbage collector.
		 */
		System.gc();
	}

	@Persist
	public void save() {

		if(processMethod != null && processMethodFile != null) {
			MethodConverter.convert(processMethodFile, processMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
			dirtyable.setDirty(false);
		}
	}

	private void initialize(Composite parent) {

		createEditorPages(parent);
		dirtyable.setDirty(true);
		processMethod = loadProcessMethod();
		extendedMethodUI.update(processMethod);
	}

	private void createEditorPages(Composite parent) {

		createPage(parent);
	}

	private void createPage(Composite parent) {

		extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE);
		extendedMethodUI.setModificationHandler(new IModificationHandler() {

			@Override
			public void setDirty(boolean dirty) {

				dirtyable.setDirty(dirty);
			}
		});
	}

	private synchronized ProcessMethod loadProcessMethod() {

		ProcessMethod processMethod = null;
		Object object = part.getObject();
		if(object instanceof Map) {
			/*
			 * Map
			 */
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
			File file = new File((String)map.get(EditorSupport.MAP_FILE));
			boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
			processMethod = loadProcessMethod(file, batch);
		}
		//
		return processMethod;
	}

	private ProcessMethod loadProcessMethod(File file, boolean batch) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		MethodImportRunnable runnable = new MethodImportRunnable(file);
		try {
			/*
			 * No fork, otherwise it might crash when loading the data takes too long.
			 */
			boolean fork = (batch) ? false : true;
			dialog.run(fork, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		//
		processMethodFile = file;
		return runnable.getProcessMethod();
	}
}
