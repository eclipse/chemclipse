/*******************************************************************************
 * Copyright (c) 2014, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - update upon events
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.exceptions.NoMassSpectrumConverterAvailableException;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.XmiSupport;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.MassSpectrumImportRunnable;
import org.eclipse.chemclipse.ux.extension.msd.ui.swt.ExtendedMassSpectrumUI;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.EventHandler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

public class MassSpectrumEditor implements IMassSpectrumEditor {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectrumEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.msd.ui/org.eclipse.chemclipse.ux.extension.msd.ui.editors.MassSpectrumEditor";
	public static final String ICON_URI = ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_MASS_SPECTRUM_FILE, IApplicationImageProvider.SIZE_16x16);
	public static final String TOOLTIP = "Mass Spectrum - Detector Type: MSD";

	private static final Logger logger = Logger.getLogger(MassSpectrumEditor.class);
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
	private File massSpectrumFile;
	private IMassSpectra massSpectra;
	private IScanMSD massSpectrum;
	private ArrayList<EventHandler> registeredEventHandler;
	private List<Object> objects = new ArrayList<>();
	private ExtendedMassSpectrumUI extendedMassSpectrumUI;

	@PostConstruct
	private void createControl(Composite parent) {

		loadMassSpectra();
		setPartLabel();
		createPages(parent);
		registeredEventHandler = new ArrayList<>();
		registerEvents();
		announceSelection();
	}

	private void createPages(Composite parent) {

		extendedMassSpectrumUI = new ExtendedMassSpectrumUI(parent, SWT.NONE);
		extendedMassSpectrumUI.update(massSpectra);
	}

	@Focus
	public void setFocus() {

		extendedMassSpectrumUI.setFocus();
		announceSelection();
	}

	private void announceSelection() {

		IScanMSD displayedMassSpectrum = extendedMassSpectrumUI.getMassSpectrum();
		if(displayedMassSpectrum != null) {
			massSpectrum = displayedMassSpectrum;
			UpdateNotifier.update(displayedMassSpectrum);
		}
	}

	@PreDestroy
	private void preDestroy() {

		IScan scan = null;
		UpdateNotifierUI.update(Display.getDefault(), scan);
		/*
		 * Remove the editor from the listed parts.
		 */
		if(modelService != null) {
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			partStack.getChildren().remove(part);
		}
	}

	@Persist
	public boolean save() {

		Shell shell = extendedMassSpectrumUI.getShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = monitor -> {

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
		};
		/*
		 * Run the export
		 */
		try {
			/*
			 * True to show the moving progress bar. False, a mass spectrum
			 * should be imported as a whole.
			 */
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			return saveAs();
		} catch(InterruptedException e) {
			logger.warn(e);
			Thread.currentThread().interrupt();
			return false;
		}
		return true;
	}

	private void saveMassSpectra(IProgressMonitor monitor, Shell shell) throws NoMassSpectrumConverterAvailableException {

		/*
		 * Try to save the mass spectrum.
		 */
		if(massSpectrumFile != null && massSpectra != null && shell != null) {
			/*
			 * Convert the mass spectra.
			 */
			String converterId = massSpectra.getConverterId();
			if(converterId != null && !converterId.equals("")) {
				/*
				 * Try to save the mass spectrum.
				 */
				monitor.subTask("Save Mass Spectrum");
				IProcessingInfo<File> processingInfo = DatabaseConverter.convert(massSpectrumFile, massSpectra, false, converterId, monitor);
				/*
				 * If no failures have occurred, set the dirty status to
				 * false.
				 */
				processingInfo.getProcessingResult();
				if(massSpectrum != null) {
					massSpectrum.setDirty(false);
				}
				dirtyable.setDirty(false);
			} else {
				throw new NoMassSpectrumConverterAvailableException();
			}
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

	private void loadMassSpectra() {

		try {
			/*
			 * Import the mass spectrum without showing it on the user interface.
			 * The GUI will take care itself of this action.
			 */
			Object object = part.getObject();
			if(object instanceof Map<?, ?> map) {
				/*
				 * String
				 */
				File file = new File((String)map.get(EditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
				importMassSpectrum(file, batch);
			} else if(object instanceof String path) {
				/*
				 * Legacy ... Deprecated
				 */
				File file = new File(path);
				importMassSpectrum(file, true);
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	private void importMassSpectrum(File file, boolean batch) throws ChromatogramIsNullException {

		/*
		 * Import the mass spectrum here, but do not set to the mass spectrum UI,
		 * as it must be initialized first.
		 */
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
		MassSpectrumImportRunnable runnable = new MassSpectrumImportRunnable(file);
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
		massSpectra = runnable.getMassSpectra();
		if(extendedMassSpectrumUI != null) {
			extendedMassSpectrumUI.update(massSpectra);
		}
		massSpectrumFile = file;
	}

	private void setPartLabel() {

		if(massSpectra == null) {
			return;
		}

		String name = ("".equals(massSpectra.getName())) ? "NoName" : massSpectra.getName();

		if(!massSpectra.isEmpty()) {
			massSpectrum = massSpectra.getMassSpectrum(1);
			massSpectrum.setDirty(false);
			if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
				name = standaloneMassSpectrum.getName();
			} else if(massSpectrum instanceof IRegularLibraryMassSpectrum regularLibraryMassSpectrum) {
				ILibraryInformation libraryInformation = regularLibraryMassSpectrum.getLibraryInformation();
				if(libraryInformation != null) {
					name = libraryInformation.getName();
				}
			}
		}
		part.setLabel(XmiSupport.removeInvalidCharacters(name));
	}

	public void registerEvent(String topic, String property) {

		registerEvent(topic, new String[]{property});
	}

	public void registerEvent(String topic, String[] properties) {

		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, topic, properties));
		}
	}

	private EventHandler registerEventHandler(IEventBroker eventBroker, String topic, String[] properties) {

		EventHandler eventHandler = event -> {

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
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private void update(String topic) {

		if(extendedMassSpectrumUI.isVisible() || IChemClipseEvents.TOPIC_MASS_SPECTRUM_UPDATE_SELECTION.equals(topic)) {
			updateObjects(objects, topic);
		}
	}

	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, IChemClipseEvents.EVENT_BROKER_DATA);
		registerEvent(IChemClipseEvents.TOPIC_MASS_SPECTRUM_UPDATE_SELECTION, IChemClipseEvents.EVENT_BROKER_DATA);
	}

	public void updateObjects(List<Object> objects, String topic) {

		switch(topic) {
			case IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION: {
				if(objects.size() == 1) {
					Object object = objects.get(0);
					if(object instanceof IScanMSD scanMSD && containsMassSpectrum(scanMSD)) {
						massSpectrum = scanMSD;
						if(scanMSD != extendedMassSpectrumUI.getMassSpectrum()) {
							extendedMassSpectrumUI.update(scanMSD);
						}
					}
				}
				break;
			}
			case IChemClipseEvents.TOPIC_MASS_SPECTRUM_UPDATE_SELECTION: {
				if(objects.size() == 1) {
					Object object = objects.get(0);
					if(object instanceof IScanMSD scanMSD) {
						if(scanMSD == extendedMassSpectrumUI.getMassSpectrum()) {
							extendedMassSpectrumUI.refresh();
							dirtyable.setDirty(scanMSD.isDirty());
						}
					} else if(object == massSpectra) {
						dirtyable.setDirty(massSpectra.isDirty());
					}
				}
				break;
			}
		}
	}

	private boolean containsMassSpectrum(IScanMSD scanMSD) {

		if(massSpectra == null) {
			return false;
		}
		for(IScanMSD massSpectrumOfFile : massSpectra.getList()) {
			if(massSpectrumOfFile == scanMSD) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IMassSpectra getScanSelection() {

		return massSpectra;
	}
}
