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
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChromatogramEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ChromatogramFileSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("rawtypes")
public abstract class AbstractChromatogramEditor extends AbstractDataUpdateSupport implements IChromatogramEditor, IDataUpdateSupport {

	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String TOOLTIP = "Chromatogram Editor";
	//
	private static final Logger logger = Logger.getLogger(AbstractChromatogramEditor.class);
	private static final Runnable runGC = () -> System.gc();
	//
	private DataType dataType;
	private MPart part;
	private MDirtyable dirtyable;
	//
	private IEventBroker eventBroker;
	//
	private File chromatogramFile = null;
	private ExtendedChromatogramUI extendedChromatogramUI;
	//
	private Shell shell;

	public AbstractChromatogramEditor(DataType dataType, Composite parent, MPart part, MDirtyable dirtyable, Shell shell) {
		super(part);
		//
		this.dataType = dataType;
		this.part = part;
		this.dirtyable = dirtyable;
		this.eventBroker = ModelSupportAddon.getEventBroker();
		this.shell = shell;
		//
		initialize(parent);
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		registerEvent(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		registerEvent(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
		registerEvent(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
	}

	@Focus
	public void onFocus() {

		extendedChromatogramUI.fireUpdate();
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IChromatogramSelection) {
				IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
				if(extendedChromatogramUI.isActiveChromatogramSelection(chromatogramSelection)) {
					extendedChromatogramUI.update();
				}
			} else if(object instanceof IScan) {
				extendedChromatogramUI.updateSelectedScan();
			} else if(object instanceof IPeak) {
				extendedChromatogramUI.updateSelectedPeak();
			}
		}
	}

	@PreDestroy
	private void preDestroy() {

		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, null);
		eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, null);
		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION, null);
		//
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
		Display display = DisplayUtils.getDisplay();
		if(display != null) {
			display.timerExec(200, runGC);
		} else {
			runGC.run();
		}
	}

	@Persist
	public void save() {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Save Chromatogram", IProgressMonitor.UNKNOWN);
					try {
						saveChromatogram(monitor);
					} catch(NoChromatogramConverterAvailableException e) {
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
		}
	}

	@Override
	public boolean saveAs() {

		boolean saveSuccessful = false;
		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			try {
				saveSuccessful = ChromatogramFileSupport.saveChromatogram(shell, chromatogramSelection.getChromatogram(), dataType);
				dirtyable.setDirty(!saveSuccessful);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return saveSuccessful;
	}

	@Override
	public IChromatogramSelection getChromatogramSelection() {

		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		return chromatogramSelection;
	}

	private synchronized void initialize(Composite parent) {

		IChromatogramSelection chromatogramSelection = loadChromatogram();
		createEditorPages(parent);
		extendedChromatogramUI.updateChromatogramSelection(chromatogramSelection);
		//
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String type = "";
			if(chromatogram instanceof IChromatogramMSD) {
				type = " [MSD]";
			} else if(chromatogram instanceof IChromatogramCSD) {
				type = " [CSD]";
			} else if(chromatogram instanceof IChromatogramWSD) {
				type = " [WSD]";
			}
			//
			part.setLabel(chromatogram.getName() + type);
			dirtyable.setDirty(true);
			chromatogramSelection.update(true);
		}
	}

	private synchronized IChromatogramSelection loadChromatogram() {

		IChromatogramSelection chromatogramSelection = null;
		try {
			Object object = part.getObject();
			if(object instanceof Map) {
				/*
				 * Map
				 */
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>)object;
				File file = new File((String)map.get(EditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
				chromatogramSelection = loadChromatogramSelection(file, batch);
			} else {
				/*
				 * Already available.
				 */
				if(object instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogram = (IChromatogramMSD)object;
					chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
				} else if(object instanceof IChromatogramCSD) {
					IChromatogramCSD chromatogram = (IChromatogramCSD)object;
					chromatogramSelection = new ChromatogramSelectionCSD(chromatogram);
				} else if(object instanceof IChromatogramWSD) {
					IChromatogramWSD chromatogram = (IChromatogramWSD)object;
					chromatogramSelection = new ChromatogramSelectionWSD(chromatogram);
				}
				chromatogramFile = null;
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		//
		return chromatogramSelection;
	}

	private synchronized IChromatogramSelection loadChromatogramSelection(File file, boolean batch) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException, ChromatogramIsNullException {

		IChromatogramSelection chromatogramSelection = null;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(file, dataType);
		try {
			/*
			 * No fork, otherwise it might crash when loading a chromatogram takes too long.
			 */
			boolean fork = (batch) ? false : true;
			dialog.run(fork, false, runnable);
			chromatogramSelection = runnable.getChromatogramSelection();
			chromatogramFile = file;
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		//
		return chromatogramSelection;
	}

	private void saveChromatogram(IProgressMonitor monitor) throws NoChromatogramConverterAvailableException {

		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null && shell != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String converterId = chromatogram.getConverterId();
			if(converterId != null && !converterId.equals("") && chromatogramFile != null) {
				monitor.subTask("Save Chromatogram");
				//
				IProcessingInfo processingInfo = null;
				if(chromatogram instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
					processingInfo = ChromatogramConverterMSD.convert(chromatogramFile, chromatogramMSD, converterId, monitor);
				} else if(chromatogram instanceof IChromatogramCSD) {
					IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
					processingInfo = ChromatogramConverterCSD.convert(chromatogramFile, chromatogramCSD, converterId, monitor);
				} else if(chromatogram instanceof IChromatogramWSD) {
					IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
					processingInfo = ChromatogramConverterWSD.convert(chromatogramFile, chromatogramWSD, converterId, monitor);
				}
				//
				if(processingInfo != null) {
					try {
						processingInfo.getProcessingResult(File.class);
						dirtyable.setDirty(false);
					} catch(TypeCastException e) {
						throw new NoChromatogramConverterAvailableException();
					}
				} else {
					throw new NoChromatogramConverterAvailableException();
				}
			} else {
				throw new NoChromatogramConverterAvailableException();
			}
		}
	}

	private void createEditorPages(Composite parent) {

		createChromatogramPage(parent);
	}

	private void createChromatogramPage(Composite parent) {

		extendedChromatogramUI = new ExtendedChromatogramUI(parent);
	}
}
