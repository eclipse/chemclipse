/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileWriter;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.Activator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class EditorCalibration extends MultiPageEditorPart {

	private static final Logger logger = Logger.getLogger(EditorCalibration.class);
	//
	private PageCalibration pageCalibration;
	private File file;
	private boolean isDirty = false;
	private boolean initialize = true;
	//
	private ISeparationColumnIndices separationColumnIndices;
	//
	private ArrayList<EventHandler> registeredEventHandler;
	private List<Object> objects = new ArrayList<>();

	@Override
	public void doSave(IProgressMonitor monitor) {

		CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
		calibrationFileWriter.write(file, separationColumnIndices);
		setDirty(false);
	}

	@Override
	public void doSaveAs() {

		FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		fileDialog.setText("Save the *.cal file.");
		fileDialog.setFilterExtensions(new String[]{"*.cal"});
		fileDialog.setFilterNames(new String[]{"AMDIS Calibration *.cal"});
		String pathRetentionIndexFile = fileDialog.open();
		if(pathRetentionIndexFile != null) {
			File file = new File(pathRetentionIndexFile);
			CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
			calibrationFileWriter.write(file, separationColumnIndices);
			setDirty(false);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		super.init(site, input);
		String fileName = input.getName();
		fileName = fileName.substring(0, fileName.length() - 4);
		setPartName(fileName);
		if(input instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput)input;
			this.file = fileEditorInput.getFile().getLocation().toFile();
		} else {
			throw new PartInitException("The file could't be loaded.");
		}
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	public void registerEvent(String topic, String property) {

		registerEvent(topic, new String[]{property});
	}

	public void registerEvent(String topic, String[] properties) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			registeredEventHandler.add(registerEventHandler(eventBroker, topic, properties));
		}
	}

	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_RI_LIBRARY_UPDATE, IChemClipseEvents.EVENT_BROKER_DATA);
	}

	public void setDirty(boolean isDirty) {

		this.isDirty = isDirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void setFocus() {

		if(initialize) {
			initialize = false;
			CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
			separationColumnIndices = calibrationFileReader.parse(file);
			pageCalibration.showData(file, separationColumnIndices);
		}
	}

	public void updateObjects(List<Object> objects) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof Object[]) {
				Object[] array = (Object[])object;
				if(array.length == 2) {
					Object content = array[1];
					if(content instanceof ISeparationColumnIndices) {
						ISeparationColumnIndices separationColumnIndices = (ISeparationColumnIndices)content;
						if(this.separationColumnIndices == separationColumnIndices) {
							setDirty(separationColumnIndices.isDirty());
						}
					}
				}
			}
		}
	}

	@PreDestroy
	private void preDestroy() {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
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

	private void update(String topic) {

		updateObjects(objects);
	}

	@Override
	protected void createPages() {

		pageCalibration = new PageCalibration(getContainer());
		int pageIndex = addPage(pageCalibration.getControl());
		setPageText(pageIndex, "Retention Index Calibration (*.cal)");
		registeredEventHandler = new ArrayList<>();
		registerEvents();
	}
}
