/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.ui.editors.IScanEditorXIR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ScanXIRImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedXIRScanUI;
import org.eclipse.chemclipse.xir.model.core.IScanXIR;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ScanEditorXIR implements IScanEditorXIR {

	private static final Logger logger = Logger.getLogger(ScanEditorXIR.class);
	//
	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanEditorXIR";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorXIR";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/scan-xir.gif";
	public static final String TOOLTIP = "FTIR/NIR/MIR Editor";
	//
	private final MPart part;
	private final MDirtyable dirtyable;
	private final EModelService modelService;
	private final MApplication application;
	//
	private File scanFile;
	private ExtendedXIRScanUI extendedScanXIREditorUI;
	//
	private IScanXIR scanXIR = null;
	//
	private final Shell shell;

	@Inject
	public ScanEditorXIR(Composite parent, MPart part, MDirtyable dirtyable, EModelService modelService, MApplication application, Shell shell) {

		this.part = part;
		this.dirtyable = dirtyable;
		this.modelService = modelService;
		this.application = application;
		this.shell = shell;
		//
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		extendedScanXIREditorUI.setFocus();
	}

	@PreDestroy
	protected void preDestroy() {

		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_SCAN_XIR_UNLOAD_SELECTION, null);
		//
		if(modelService != null && application != null) {
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

		System.out.println(scanFile);
		dirtyable.setDirty(false);
	}

	@Override
	public boolean saveAs() {

		return true;
	}

	@Override
	public IScanXIR getScanSelection() {

		return scanXIR;
	}

	private void initialize(Composite parent) {

		scanXIR = loadScan();
		createEditorPages(parent);
		extendedScanXIREditorUI.update(scanXIR);
	}

	private synchronized IScanXIR loadScan() {

		IScanXIR scanXIR = null;
		//
		try {
			Object object = part.getObject();
			if(object instanceof Map) {
				/*
				 * String
				 */
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>)object;
				File file = new File((String)map.get(EditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
				scanXIR = loadScan(file, batch);
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		//
		return scanXIR;
	}

	private IScanXIR loadScan(File file, boolean batch) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		ScanXIRImportRunnable runnable = new ScanXIRImportRunnable(file);
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
		scanFile = file;
		return runnable.getScanXIR();
	}

	private void createEditorPages(Composite parent) {

		createScanPage(parent);
	}

	private void createScanPage(Composite parent) {

		extendedScanXIREditorUI = new ExtendedXIRScanUI(parent, SWT.NONE);
	}
}
