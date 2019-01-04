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

import org.eclipse.chemclipse.converter.quantitation.QuantDBConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.editors.IQuantitationDatabaseEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.QuantDBImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedQuantCompoundListUI;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class QuantitationDatabaseEditor extends AbstractDataUpdateSupport implements IDataUpdateSupport, IQuantitationDatabaseEditor {

	private static final Logger logger = Logger.getLogger(SequenceEditor.class);
	//
	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.quantitationDatabaseEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.QuantitationDatabaseEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/database.gif";
	public static final String TOOLTIP = "Quantitation Editor";
	//
	private MPart part;
	private MDirtyable dirtyable;
	//
	private File quantitationDatabaseFile;
	private IQuantitationDatabase quantitationDatabase;
	private ExtendedQuantCompoundListUI extendedQuantCompoundListUI;
	//
	private Shell shell;

	@Inject
	public QuantitationDatabaseEditor(Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
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

		if(quantitationDatabase != null && quantitationDatabaseFile != null) {
			QuantDBConverter.convert(quantitationDatabaseFile, quantitationDatabase, QuantDBConverter.DEFAULT_QUANT_DB_CONVERTER_ID, new NullProgressMonitor());
			dirtyable.setDirty(false);
		}
	}

	@Override
	public boolean saveAs() {

		return false;
	}

	@Override
	public IQuantitationDatabase getQuantitationDatabase() {

		return quantitationDatabase;
	}

	private void initialize(Composite parent) {

		createEditorPages(parent);
		dirtyable.setDirty(true);
		quantitationDatabase = loadQuantitationDatabase();
		extendedQuantCompoundListUI.update(quantitationDatabase);
	}

	private void createEditorPages(Composite parent) {

		createPage(parent);
	}

	private void createPage(Composite parent) {

		extendedQuantCompoundListUI = new ExtendedQuantCompoundListUI(parent);
		extendedQuantCompoundListUI.setModificationHandler(new IModificationHandler() {

			@Override
			public void setDirty(boolean dirty) {

				dirtyable.setDirty(dirty);
			}
		});
	}

	private synchronized IQuantitationDatabase loadQuantitationDatabase() {

		IQuantitationDatabase quantitationDatabase = null;
		Object object = part.getObject();
		if(object instanceof Map) {
			/*
			 * Map
			 */
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
			File file = new File((String)map.get(EditorSupport.MAP_FILE));
			boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
			quantitationDatabase = loadQuantitationDatabase(file, batch);
		}
		//
		return quantitationDatabase;
	}

	private IQuantitationDatabase loadQuantitationDatabase(File file, boolean batch) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		QuantDBImportRunnable runnable = new QuantDBImportRunnable(file);
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
		quantitationDatabaseFile = file;
		return runnable.getQuantitationDatabase();
	}
}
