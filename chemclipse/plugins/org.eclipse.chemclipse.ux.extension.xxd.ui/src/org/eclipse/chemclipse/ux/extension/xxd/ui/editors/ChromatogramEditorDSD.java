/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.support.HeaderUtil;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.AbstractProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.ui.E4ProcessSupplierContext;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.XmiSupport;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChromatogramEditor;
import org.eclipse.chemclipse.ux.extension.ui.parts.AbstractUpdater;
import org.eclipse.chemclipse.ux.extension.ui.support.ObjectChangedListener;
import org.eclipse.chemclipse.ux.extension.ui.support.ProcessMethodNotifications;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.ChromatogramFileSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.MeasurementResultNotification;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedElectropherogramEditorUI;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swtchart.extensions.linecharts.LineChart;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

public class ChromatogramEditorDSD extends AbstractUpdater<ExtendedElectropherogramEditorUI> implements IChromatogramEditor {

	private static final Logger logger = Logger.getLogger(ChromatogramEditorDSD.class);

	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramEditorDSD";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorDSD";
	public static final String ICON_URI = ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_CHROMATOGRAM_DSD, IApplicationImageProvider.SIZE_16x16);

	private static final String TOPIC_CHROMATOGRAM = IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION;
	private static final String TOPIC_SCAN = IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION;
	private static final String TOPIC_EDITOR_UPDATE = IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE;
	private static final String TOPIC_EDITOR_ADJUST = IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_ADJUST;
	private static final String TOPIC_TOOLBAR_UPDATE = IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_TOOLBAR_UPDATE;

	private final DataType dataType;
	private final MPart part;
	private final MDirtyable dirtyable;
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final Shell shell;
	private final ObjectChangedListener<IMeasurementResult<?>> updateMeasurementResult = new MeasurementResultListener();
	private final IProcessSupplierContext processSupplierContext;

	private File chromatogramFile = null;
	private ExtendedElectropherogramEditorUI extendedElectropherogramEditorUI;

	private final ObjectChangedListener<Object> updateMenuListener = (_, _, _) -> {

		if(extendedElectropherogramEditorUI != null) {
			extendedElectropherogramEditorUI.updateMenu();
			extendedElectropherogramEditorUI.updateMethods();
		}
	};

	@Inject
	public ChromatogramEditorDSD(Composite parent, MPart part, MDirtyable dirtyable, Shell shell, E4ProcessSupplierContext processSupplierContext) {

		super(TOPIC_CHROMATOGRAM, Activator.getDefault().getDataUpdateSupport());

		this.dataType = DataType.DSD;
		this.part = part;
		this.dirtyable = dirtyable;
		this.processSupplierContext = processSupplierContext;
		this.shell = shell;

		initialize(parent);
	}

	@Focus
	public void onFocus() {

		if(shell != null) {
			Display display = shell.getDisplay();
			extendedElectropherogramEditorUI.fireUpdate(display);
			shell.getDisplay().asyncExec(() -> {

				if(chromatogramFile != null) {
					extendedElectropherogramEditorUI.updateToolbar();
					extendedElectropherogramEditorUI.updateCommands();
				}
			});
		}
	}

	@PostConstruct
	private void postConstruct(ProcessMethodNotifications methodNotification, MeasurementResultNotification measurementNotification) {

		methodNotification.addObjectChangedListener(updateMenuListener);
		measurementNotification.addObjectChangedListener(updateMeasurementResult);
	}

	@PreDestroy
	private void preDestroy(ProcessMethodNotifications notifications, MeasurementResultNotification measurementNotification) {

		chromatogramFile = null;
		notifications.removeObjectChangedListener(updateMenuListener);
		measurementNotification.removeObjectChangedListener(updateMeasurementResult);

		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, null);
		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, null);
		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE, "ChromatogramEditor4x Close");

		extendedElectropherogramEditorUI.dispose();
	}

	@Persist
	public void save() {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = monitor -> {

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
		IChromatogramSelection chromatogramSelection = extendedElectropherogramEditorUI.getChromatogramSelection();
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

		return extendedElectropherogramEditorUI.getChromatogramSelection();
	}

	private String getFilterPath() {

		String filterPath = preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_SAVE_AS_FOLDER);
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
		createChromatogramPage(parent);
		extendedElectropherogramEditorUI.updateChromatogramSelection(chromatogramSelection);
		processChromatogram(chromatogramSelection);
		setControl(extendedElectropherogramEditorUI);

		if(chromatogramSelection != null) {
			/*
			 * The label and tooltip are built from vendor data, which may contain
			 * characters that the workbench model is unable to serialize to XML.
			 */
			part.setLabel(XmiSupport.removeInvalidCharacters(ChromatogramDataSupport.getChromatogramEditorLabel(chromatogramSelection, false)));
			part.setTooltip(XmiSupport.removeInvalidCharacters(ChromatogramDataSupport.getReferenceLabel(chromatogramSelection.getChromatogram(), 0, false)));
			chromatogramSelection.update(true);
		}
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IChromatogramSelection chromatogramSelection) {
				if(extendedElectropherogramEditorUI.isActiveChromatogramSelection(chromatogramSelection)) {
					extendedElectropherogramEditorUI.update();
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					if(chromatogram != null) {
						dirtyable.setDirty(chromatogram.isDirty());
					}
					return true;
				}
			} else if(object instanceof IScan) {
				extendedElectropherogramEditorUI.updateSelectedScan();
				return true;
			} else if(TOPIC_EDITOR_UPDATE.equals(topic)) {
				logger.info("Update the chromatogram editor: " + object);
				extendedElectropherogramEditorUI.update();
				dirtyable.setDirty(extendedElectropherogramEditorUI.getChromatogramSelection().getChromatogram().isDirty());
				return true;
			} else if(TOPIC_EDITOR_ADJUST.equals(topic)) {
				logger.info("Adjust the chromatogram editor: " + object);
				extendedElectropherogramEditorUI.adjustChromatogramChart();
				return true;
			} else if(TOPIC_TOOLBAR_UPDATE.equals(topic)) {
				logger.info("Updating processor toolbar");
				extendedElectropherogramEditorUI.updateToolbar();
			}
		}

		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC_CHROMATOGRAM.equals(topic) || TOPIC_SCAN.equals(topic) || TOPIC_EDITOR_UPDATE.equals(topic) || TOPIC_EDITOR_ADJUST.equals(topic) || TOPIC_TOOLBAR_UPDATE.equals(topic);
	}

	private void processChromatogram(IChromatogramSelection chromatogramSelection) {

		String loadProcessMethodPath = preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_LOAD_PROCESS_METHOD);
		if(!loadProcessMethodPath.trim().isEmpty()) {
			File file = new File(loadProcessMethodPath);
			if(file.exists() && chromatogramSelection != null) {
				try {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
					dialog.run(false, false, monitor -> {

						IProcessMethod processMethod = Adapters.adapt(file, IProcessMethod.class);
						if(processMethod != null) {
							AbstractProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, new ProcessingInfo<>(), processSupplierContext), IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection));
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
	}

	@SuppressWarnings("unchecked")
	private synchronized IChromatogramSelection loadChromatogram() {

		IChromatogramSelection chromatogramSelection = null;
		try {
			Object object = part.getObject();
			if(object instanceof Map<?, ?> map) {
				/*
				 * Map
				 */
				File file = new File((String)map.get(EditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
				String supplierId = getSupplierID(map);
				chromatogramSelection = loadChromatogramSelection(file, supplierId, batch);
				if(chromatogramSelection != null) {
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					if(map.get(EditorSupport.MAP_HEADER_MAP) instanceof Map headerMap) {
						Set<Map.Entry<?, ?>> headerEntries = headerMap.entrySet();
						for(Map.Entry<?, ?> headerEntry : headerEntries) {
							if(headerEntry.getKey() instanceof HeaderField headerField) {
								String value = headerEntry.getValue().toString();
								HeaderUtil.setHeaderData(chromatogram, headerField, value, false);
							}
						}
					}
				}
			} else {
				/*
				 * Already available.
				 */
				if(object instanceof IChromatogramWSD chromatogram) {
					chromatogramSelection = new ChromatogramSelectionWSD(chromatogram);
				}
				chromatogramFile = null;
			}
		} catch(Exception e) {
			logger.error(e);
		}

		return chromatogramSelection;
	}

	/**
	 * Could be also null if not set yet.
	 *
	 * @return String
	 */
	private synchronized String getSupplierID(Map<?, ?> map) {

		String supplierId = null;
		Object id = map.get(EditorSupport.MAP_SUPPLIER_ID);
		if(id instanceof String value) {
			if(!value.isBlank()) {
				supplierId = value.trim();
			}
		}

		return supplierId;
	}

	private synchronized IChromatogramSelection loadChromatogramSelection(File file, String supplierId, boolean batch) throws ChromatogramIsNullException {

		IChromatogramSelection chromatogramSelection = null;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(file, dataType, supplierId);

		try {
			/*
			 * No fork, otherwise it might crash when loading a chromatogram takes too long.
			 */
			boolean fork = !batch;
			dialog.run(fork, true, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
			logger.warn(e.getCause());
		} catch(InterruptedException e) {
			logger.warn(e);
			Thread.currentThread().interrupt();
		}
		chromatogramSelection = runnable.getChromatogramSelection();
		chromatogramFile = file;

		return chromatogramSelection;
	}

	private void saveChromatogram(IProgressMonitor monitor) throws NoChromatogramConverterAvailableException {

		IChromatogramSelection chromatogramSelection = extendedElectropherogramEditorUI.getChromatogramSelection();
		if(chromatogramSelection != null && shell != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String converterId = chromatogram.getConverterId();
			if(converterId != null && !converterId.equals("") && chromatogramFile != null) {
				monitor.subTask(ExtensionMessages.saveChromatogram);

				IProcessingInfo<?> processingInfo = null;
				if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
					processingInfo = ChromatogramConverterWSD.getInstance().convert(chromatogramFile, chromatogramWSD, converterId, monitor);
				}

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

	private void createChromatogramPage(Composite parent) {

		extendedElectropherogramEditorUI = new ExtendedElectropherogramEditorUI(parent, SWT.NONE, processSupplierContext);
	}

	private final class MeasurementResultListener implements ObjectChangedListener<IMeasurementResult<?>>, PropertyChangeListener {

		private ICustomPaintListener oldPaintListener;
		private PropertyChangeSupport oldObserver;

		@Override
		public void objectChanged(ChangeType type, IMeasurementResult<?> newObject, IMeasurementResult<?> oldObject) {

			if(type == ChangeType.SELECTED) {
				boolean mustRedraw = false;

				if(oldPaintListener != null) {
					extendedElectropherogramEditorUI.getChromatogramChart().getBaseChart().getPlotArea().removeCustomPaintListener(oldPaintListener);
					mustRedraw = true;
					oldPaintListener = null;
				}

				if(oldObserver != null) {
					oldObserver.removePropertyChangeListener(this);
					oldObserver = null;
				}

				ICustomPaintListener paintListener = Adapters.adapt(newObject, ICustomPaintListener.class);
				if(paintListener != null) {
					oldPaintListener = paintListener;
					extendedElectropherogramEditorUI.getChromatogramChart().getBaseChart().getPlotArea().addCustomPaintListener(paintListener);
					mustRedraw = true;
				}

				PropertyChangeSupport observable = Adapters.adapt(newObject, PropertyChangeSupport.class);
				if(observable != null) {
					oldObserver = observable;
					observable.addPropertyChangeListener(this);
				}

				if(mustRedraw) {
					Display.getDefault().asyncExec(this::redraw);
				}
			}
		}

		private void redraw() {

			LineChart chart = extendedElectropherogramEditorUI.getChromatogramChart();
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
