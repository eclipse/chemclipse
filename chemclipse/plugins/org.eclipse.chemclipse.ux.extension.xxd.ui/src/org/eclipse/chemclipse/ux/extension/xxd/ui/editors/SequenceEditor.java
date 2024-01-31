/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
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
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.converter.model.reports.ISequence;
import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.SequenceImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedSequenceListUI;
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

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

public class SequenceEditor {

	private static final Logger logger = Logger.getLogger(SequenceEditor.class);
	//
	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.sequenceEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.SequenceEditor";
	public static final String ICON_URI = ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_SEQUENCE_LIST_DEFAULT, IApplicationImageProvider.SIZE_16x16);
	public static final String TOOLTIP = ExtensionMessages.sequenceEditor;
	//
	private final MPart part;
	private final MDirtyable dirtyable;
	private final EModelService modelService;
	private final MApplication application;
	//
	private File sequenceFile;
	private ExtendedSequenceListUI extendedSequenceListUI;

	@Inject
	public SequenceEditor(Composite parent, MPart part, MDirtyable dirtyable, EModelService modelService, MApplication application, Shell shell) {

		this.part = part;
		this.dirtyable = dirtyable;
		this.modelService = modelService;
		this.application = application;
		//
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		extendedSequenceListUI.setFocus();
	}

	@PreDestroy
	protected void preDestroy() {

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

		// TODO
		System.out.println(sequenceFile);
		dirtyable.setDirty(false);
	}

	private void initialize(Composite parent) {

		createEditorPages(parent);
		extendedSequenceListUI.update(loadSequence());
	}

	private synchronized ISequence<? extends ISequenceRecord> loadSequence() {

		ISequence<? extends ISequenceRecord> sequence = null;
		try {
			Object object = part.getObject();
			if(object instanceof Map<?, ?> map) {
				/*
				 * Map
				 */
				File file = new File((String)map.get(EditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
				sequence = loadSequence(file, batch);
			} else {
				/*
				 * Already available.
				 */
				if(object instanceof ISequence<?> storedSequence) {
					sequence = storedSequence;
				}
				sequenceFile = null;
			}
		} catch(Exception e) {
			logger.error(e);
		}
		//
		return sequence;
	}

	private synchronized ISequence<? extends ISequenceRecord> loadSequence(File file, boolean batch) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException, ChromatogramIsNullException {

		ISequence<? extends ISequenceRecord> sequence = null;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
		SequenceImportRunnable runnable = new SequenceImportRunnable(file);
		try {
			/*
			 * No fork, otherwise it might crash when loading a chromatogram takes too long.
			 */
			boolean fork = !batch;
			dialog.run(fork, false, runnable);
			sequence = runnable.getSequence();
			sequenceFile = file;
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
			Thread.currentThread().interrupt();
		}
		//
		return sequence;
	}

	private void createEditorPages(Composite parent) {

		createPage(parent);
	}

	private void createPage(Composite parent) {

		extendedSequenceListUI = new ExtendedSequenceListUI(parent, SWT.NONE);
	}
}
