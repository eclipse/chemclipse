/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - add dirty handling
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.exceptions.NoMassSpectrumConverterAvailableException;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.DatabaseImportRunnable;
import org.eclipse.chemclipse.ux.extension.msd.ui.swt.MassSpectrumLibraryUI;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DatabaseEditor implements IChemClipseEditor {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectrumLibraryEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.msd.ui/org.eclipse.chemclipse.ux.extension.msd.ui.editors.DatabaseEditor";
	public static final String ICON_URI = ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_MASS_SPECTRUM_DATABASE, IApplicationImageProvider.SIZE_16x16);
	public static final String TOOLTIP = "Mass Spectrum Library - Detector Type: MSD";
	//
	private static final Logger logger = Logger.getLogger(DatabaseEditor.class);
	/*
	 * Injected member in constructor
	 */
	@Inject
	private MPart part;
	@Inject
	private MDirtyable dirtyable;
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private IEventBroker eventBroker;
	/*
	 * Mass spectrum selection and the GUI element.
	 */
	private MassSpectrumLibraryUI massSpectrumLibraryUI;
	private File massSpectrumFile = null;
	private IMassSpectra massSpectra = null;
	private ArrayList<EventHandler> registeredEventHandler;
	private List<Object> objects = new ArrayList<>();
	/*
	 * Showing additional info in tabs.
	 */
	private TabFolder tabFolder;

	public void registerEvent(String topic, String property) {

		registerEvent(topic, new String[]{property});
	}

	public void registerEvent(String topic, String[] properties) {

		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, topic, properties));
		}
	}

	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE, IChemClipseEvents.EVENT_BROKER_DATA);
	}

	@Persist
	public void save() {

		Shell shell = DisplayUtils.getShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Save Mass Spectra", IProgressMonitor.UNKNOWN);
					try {
						saveMassSpectra(monitor, shell);
					} catch(NoMassSpectrumConverterAvailableException e) {
						throw new InvocationTargetException(e);
					}
				} finally {
					monitor.done();
				}
			}
		};
		/*
		 * Run the export
		 */
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			saveAs();
		} catch(InterruptedException e) {
			logger.warn(e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public boolean saveAs() {

		boolean saveSuccessful = false;
		if(massSpectra != null) {
			try {
				saveSuccessful = DatabaseFileSupport.saveMassSpectra(massSpectra);
				dirtyable.setDirty(!saveSuccessful);
			} catch(NoConverterAvailableException e) {
				logger.warn(e);
			}
		}
		return saveSuccessful;
	}

	@Focus
	public void setFocus() {

		if(massSpectra != null) {
			UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE_SELECTION, massSpectra);
		}
	}

	public void updateObjects(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IMassSpectra newMassSpectra) {
				if(object == massSpectra) {
					dirtyable.setDirty(newMassSpectra.isDirty());
				}
			}
		}
	}

	@PostConstruct
	private void createControl(Composite parent) {

		loadMassSpectra();
		createPages(parent);
		registeredEventHandler = new ArrayList<>();
		registerEvents();
	}

	private void createEditorPage() {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Library");
		//
		massSpectrumLibraryUI = new MassSpectrumLibraryUI(tabFolder, SWT.NONE);
		updateMassSpectrumListUI();
		//
		tabItem.setControl(massSpectrumLibraryUI);
	}

	private void createErrorMessagePage(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		Label label = new Label(composite, SWT.NONE);
		label.setText("The mass spectrum couldn't be loaded.");
	}

	private void createPages(Composite parent) {

		if(massSpectra != null && massSpectra.getMassSpectrum(1) != null) {
			String label = ("".equals(massSpectra.getName())) ? massSpectrumFile.getName() : massSpectra.getName();
			part.setLabel(label);
			tabFolder = new TabFolder(parent, SWT.BOTTOM);
			createEditorPage();
		} else {
			createErrorMessagePage(parent);
		}
	}

	private void importMassSpectra(File file, boolean batch) throws ChromatogramIsNullException {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
		DatabaseImportRunnable runnable = new DatabaseImportRunnable(file);
		try {
			/*
			 * No fork, otherwise it might crash when loading the data takes too long.
			 */
			boolean fork = !batch;
			dialog.run(fork, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		} catch(InterruptedException e) {
			logger.warn(e);
			Thread.currentThread().interrupt();
		}
		/*
		 * Add the mass spectra handling.
		 */
		massSpectra = runnable.getMassSpectra();
		massSpectrumFile = file;
		massSpectra.addUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateMassSpectrumListUI();
			}
		});
	}

	private void loadMassSpectra() {

		try {
			Object object = part.getObject();
			if(object instanceof Map<?, ?> map) {
				/*
				 * String
				 */
				File file = new File((String)map.get(EditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
				importMassSpectra(file, batch);
			} else if(object instanceof String text) {
				/*
				 * Legacy ... Deprecated
				 */
				File file = new File(text);
				importMassSpectra(file, true);
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	@PreDestroy
	private void preDestroy() {

		massSpectrumLibraryUI.dispose();
		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE_SELECTION, null);
		/*
		 * Remove the editor from the listed parts.
		 */
		List<String> clearTopics = Arrays.asList(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION);
		UpdateNotifier.update(IChemClipseEvents.TOPIC_EDITOR_LIBRARY_CLOSE, clearTopics);
		//
		if(modelService != null) {
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			partStack.getChildren().remove(part);
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String topic, String[] properties) {

		EventHandler eventHandler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				try {
					objects.clear();
					for(String property : properties) {
						Object object = event.getProperty(property);
						objects.add(object);
					}
					update(topic);
				} catch(Exception e) {
					logger.warn(e + "\t" + event);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private void saveMassSpectra(IProgressMonitor monitor, Shell shell) throws NoMassSpectrumConverterAvailableException {

		if(massSpectrumFile != null && massSpectra != null && shell != null) {
			/*
			 * Convert the mass spectra.
			 */
			String converterId = massSpectra.getConverterId();
			if(converterId != null && !converterId.equals("")) {
				monitor.subTask("Save Mass Spectra");
				IProcessingInfo<File> processingInfo = DatabaseConverter.convert(massSpectrumFile, massSpectra, false, converterId, monitor);
				try {
					/*
					 * If no failures have occurred, set the dirty status to
					 * false.
					 */
					processingInfo.getProcessingResult();
					dirtyable.setDirty(false);
				} catch(TypeCastException e) {
					logger.warn(e);
				}
			} else {
				throw new NoMassSpectrumConverterAvailableException();
			}
		}
	}

	private void update(String topic) {

		if(massSpectrumLibraryUI.isVisible()) {
			updateObjects(objects, topic);
		}
	}

	private void updateMassSpectrumListUI() {

		massSpectrumLibraryUI.update(massSpectrumFile, massSpectra);
	}
}