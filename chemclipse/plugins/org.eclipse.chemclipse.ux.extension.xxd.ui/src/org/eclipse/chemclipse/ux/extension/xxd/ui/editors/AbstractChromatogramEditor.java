/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChromatogramEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ChromatogramFileSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.MeasurementResultNotification;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ObjectChangedListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ProcessMethodNotifications;
import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.AbstractUpdater;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.selection.ChromatogramSelectionISD;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.ICustomPaintListener;

@SuppressWarnings("rawtypes")
public abstract class AbstractChromatogramEditor extends AbstractUpdater<ExtendedChromatogramUI> implements IChromatogramEditor {

	private static final Logger logger = Logger.getLogger(AbstractChromatogramEditor.class);
	//
	public static final String ICON_URI = ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImageProvider.SIZE_16x16);
	public static final String TOOLTIP = ExtensionMessages.chromatogramEditor;
	//
	private static final String TOPIC_CHROMATOGRAM = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;
	private static final String TOPIC_SCAN = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;
	private static final String TOPIC_PEAK = IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION;
	private static final String TOPIC_EDITOR_UPDATE = IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE;
	private static final String TOPIC_EDITOR_ADJUST = IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_ADJUST;
	private static final String TOPIC_TOOLBAR_UPDATE = IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_TOOLBAR_UPDATE;
	//
	private final DataType dataType;
	private final MPart part;
	private final MDirtyable dirtyable;
	//
	private final IEventBroker eventBroker;
	//
	private File chromatogramFile = null;
	private ExtendedChromatogramUI extendedChromatogramUI;
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final Shell shell;
	private final ObjectChangedListener<Object> updateMenuListener = new ObjectChangedListener<>() {

		@Override
		public void objectChanged(ChangeType type, Object newObject, Object oldObject) {

			if(extendedChromatogramUI != null) {
				extendedChromatogramUI.updateMenu();
				extendedChromatogramUI.updateMethods();
			}
		}
	};
	//
	private final ObjectChangedListener<IMeasurementResult<?>> updateMeasurementResult = new MeasurementResultListener();
	private final IProcessSupplierContext processSupplierContext;

	@Deprecated
	public AbstractChromatogramEditor(DataType dataType, Composite parent, MPart part, MDirtyable dirtyable, ProcessorFactory filterFactory, Shell shell) {

		this(dataType, parent, part, dirtyable, new ProcessTypeSupport(), shell);
	}

	public AbstractChromatogramEditor(DataType dataType, Composite parent, MPart part, MDirtyable dirtyable, IProcessSupplierContext processSupplierContext, Shell shell) {

		super(TOPIC_CHROMATOGRAM);
		//
		this.dataType = dataType;
		this.part = part;
		this.dirtyable = dirtyable;
		this.processSupplierContext = processSupplierContext;
		this.eventBroker = Activator.getDefault().getEventBroker();
		this.shell = shell;
		//
		initialize(parent);
	}

	@Focus
	public void onFocus() {

		if(shell != null) {
			extendedChromatogramUI.fireUpdate(shell.getDisplay());
		}
		extendedChromatogramUI.checkUpdates();
	}

	@PostConstruct
	private void postConstruct(ProcessMethodNotifications methodNotification, MeasurementResultNotification measurementNotification) {

		methodNotification.addObjectChangedListener(updateMenuListener);
		measurementNotification.addObjectChangedListener(updateMeasurementResult);
	}

	@PreDestroy
	private void preDestroy(ProcessMethodNotifications notifications, MeasurementResultNotification measurementNotification, PartSupport partSupport) {

		notifications.removeObjectChangedListener(updateMenuListener);
		measurementNotification.removeObjectChangedListener(updateMeasurementResult);
		//
		List<String> clearTopics = Arrays.asList(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION);
		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE, clearTopics);
		//
		partSupport.closePart(part);
		extendedChromatogramUI.dispose();
	}

	@Persist
	public void save() {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask(ExtensionMessages.saveChromatogram, IProgressMonitor.UNKNOWN);
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
			logger.warn(e);
			logger.warn(e.getCause());
			saveAs();
		} catch(InterruptedException e) {
			logger.warn(e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public boolean saveAs() {

		boolean saveSuccessful = false;
		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null) {
			try {
				/*
				 * Get the path of the loaded data file.
				 */
				String filterPath = getFilterPath();
				saveSuccessful = ChromatogramFileSupport.saveChromatogram(shell, chromatogramSelection.getChromatogram(), dataType, filterPath);
				dirtyable.setDirty(!saveSuccessful);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return saveSuccessful;
	}

	@Override
	public IChromatogramSelection getChromatogramSelection() {

		return extendedChromatogramUI.getChromatogramSelection();
	}

	private String getFilterPath() {

		String filterPath = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SAVE_AS_FOLDER);
		if("".equals(filterPath)) {
			if(chromatogramFile != null) {
				if(chromatogramFile.isDirectory()) {
					filterPath = chromatogramFile.getAbsolutePath();
				} else {
					filterPath = chromatogramFile.getParentFile().getAbsolutePath();
				}
			} else {
				filterPath = UserManagement.getUserHome();
			}
		}
		return filterPath;
	}

	private synchronized void initialize(Composite parent) {

		IChromatogramSelection chromatogramSelection = loadChromatogram();
		createEditorPages(parent);
		extendedChromatogramUI.updateChromatogramSelection(chromatogramSelection);
		processChromatogram(chromatogramSelection);
		setControl(extendedChromatogramUI);
		//
		if(chromatogramSelection != null) {
			part.setLabel(ChromatogramDataSupport.getChromatogramEditorLabel(chromatogramSelection));
			part.setTooltip(ChromatogramDataSupport.getReferenceLabel(chromatogramSelection.getChromatogram(), 0, false));
			chromatogramSelection.update(true);
		}
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IChromatogramSelection chromatogramSelection) {
				if(extendedChromatogramUI.isActiveChromatogramSelection(chromatogramSelection)) {
					extendedChromatogramUI.update();
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					if(chromatogram != null) {
						dirtyable.setDirty(chromatogram.isDirty());
					}
					return true;
				}
			} else if(object instanceof IScan) {
				extendedChromatogramUI.updateSelectedScan();
				return true;
			} else if(object instanceof IPeak) {
				extendedChromatogramUI.updateSelectedPeak();
				return true;
			} else if(TOPIC_EDITOR_UPDATE.equals(topic)) {
				logger.info("Update the chromatogram editor: " + object);
				extendedChromatogramUI.update();
				dirtyable.setDirty(extendedChromatogramUI.getChromatogramSelection().getChromatogram().isDirty());
				return true;
			} else if(TOPIC_EDITOR_ADJUST.equals(topic)) {
				logger.info("Adjust the chromatogram editor: " + object);
				extendedChromatogramUI.adjustChromatogramChart();
				return true;
			} else if(TOPIC_TOOLBAR_UPDATE.equals(topic)) {
				logger.info("Updating processor toolbar");
				extendedChromatogramUI.updateToolbar();
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC_CHROMATOGRAM.equals(topic) || TOPIC_SCAN.equals(topic) || TOPIC_PEAK.equals(topic) || TOPIC_EDITOR_UPDATE.equals(topic) || TOPIC_EDITOR_ADJUST.equals(topic) || TOPIC_TOOLBAR_UPDATE.equals(topic);
	}

	private void processChromatogram(IChromatogramSelection chromatogramSelection) {

		File file = new File(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_LOAD_PROCESS_METHOD));
		if(chromatogramSelection != null) {
			try {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				dialog.run(false, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						IProcessMethod processMethod = Adapters.adapt(file, IProcessMethod.class);
						if(processMethod != null) {
							ProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, new ProcessingInfo<>(), processSupplierContext), IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection));
						}
					}
				});
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
				Thread.currentThread().interrupt();
			}
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
				if(object instanceof IChromatogramMSD chromatogram) {
					chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
				} else if(object instanceof IChromatogramCSD chromatogram) {
					chromatogramSelection = new ChromatogramSelectionCSD(chromatogram);
				} else if(object instanceof IChromatogramWSD chromatogram) {
					chromatogramSelection = new ChromatogramSelectionWSD(chromatogram);
				} else if(object instanceof IChromatogramISD chromatogram) {
					chromatogramSelection = new ChromatogramSelectionISD(chromatogram);
				}
				chromatogramFile = null;
			}
		} catch(Exception e) {
			logger.error(e);
		}
		//
		return chromatogramSelection;
	}

	private synchronized IChromatogramSelection loadChromatogramSelection(File file, boolean batch) throws ChromatogramIsNullException {

		IChromatogramSelection chromatogramSelection = null;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(file, dataType);
		//
		try {
			/*
			 * No fork, otherwise it might crash when loading a chromatogram takes too long.
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
		chromatogramSelection = runnable.getChromatogramSelection();
		chromatogramFile = file;
		//
		return chromatogramSelection;
	}

	private void saveChromatogram(IProgressMonitor monitor) throws NoChromatogramConverterAvailableException {

		IChromatogramSelection chromatogramSelection = extendedChromatogramUI.getChromatogramSelection();
		if(chromatogramSelection != null && shell != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String converterId = chromatogram.getConverterId();
			if(converterId != null && !converterId.equals("") && chromatogramFile != null) {
				monitor.subTask(ExtensionMessages.saveChromatogram);
				//
				IProcessingInfo processingInfo = null;
				if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
					processingInfo = ChromatogramConverterMSD.getInstance().convert(chromatogramFile, chromatogramMSD, converterId, monitor);
				} else if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
					processingInfo = ChromatogramConverterCSD.getInstance().convert(chromatogramFile, chromatogramCSD, converterId, monitor);
				} else if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
					processingInfo = ChromatogramConverterWSD.getInstance().convert(chromatogramFile, chromatogramWSD, converterId, monitor);
				}
				//
				if(processingInfo != null) {
					Object object = processingInfo.getProcessingResult();
					if(object instanceof File) {
						dirtyable.setDirty(false);
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

		extendedChromatogramUI = new ExtendedChromatogramUI(parent, SWT.BORDER, eventBroker, processSupplierContext);
	}

	private final class MeasurementResultListener implements ObjectChangedListener<IMeasurementResult<?>>, PropertyChangeListener {

		private ICustomPaintListener oldPaintListener;
		private PropertyChangeSupport oldObserver;

		@Override
		public void objectChanged(ChangeType type, IMeasurementResult<?> newObject, IMeasurementResult<?> oldObject) {

			if(type == ChangeType.SELECTED) {
				boolean mustRedraw = false;
				//
				if(oldPaintListener != null) {
					extendedChromatogramUI.getChromatogramChart().getBaseChart().getPlotArea().removeCustomPaintListener(oldPaintListener);
					mustRedraw = true;
					oldPaintListener = null;
				}
				//
				if(oldObserver != null) {
					oldObserver.removePropertyChangeListener(this);
					oldObserver = null;
				}
				//
				ICustomPaintListener paintListener = Adapters.adapt(newObject, ICustomPaintListener.class);
				if(paintListener != null) {
					oldPaintListener = paintListener;
					extendedChromatogramUI.getChromatogramChart().getBaseChart().getPlotArea().addCustomPaintListener(paintListener);
					mustRedraw = true;
				}
				//
				PropertyChangeSupport observable = Adapters.adapt(newObject, PropertyChangeSupport.class);
				if(observable != null) {
					oldObserver = observable;
					observable.addPropertyChangeListener(this);
				}
				//
				if(mustRedraw) {
					Display.getDefault().asyncExec(this::redraw);
				}
			}
		}

		private void redraw() {

			ChromatogramChart chart = extendedChromatogramUI.getChromatogramChart();
			if(!chart.isDisposed()) {
				chart.redraw();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {

			Display.getDefault().asyncExec(this::redraw);
		}
	}
}