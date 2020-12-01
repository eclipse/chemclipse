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

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.maximize;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.nmr.converter.core.ScanConverterNMR;
import org.eclipse.chemclipse.nmr.model.core.FIDMeasurement;
import org.eclipse.chemclipse.nmr.model.core.FilteredFIDMeasurement;
import org.eclipse.chemclipse.nmr.model.core.FilteredSpectrumMeasurement;
import org.eclipse.chemclipse.nmr.model.core.SpectrumMeasurement;
import org.eclipse.chemclipse.nmr.model.selection.DataNMRSelection;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.filter.FilterContext;
import org.eclipse.chemclipse.processing.filter.Filtered;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.swt.EditorToolBar;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.ux.extension.ui.editors.IScanEditorNMR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageProcessors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.DynamicSettingsUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.NMRMeasurementsUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedNMRScanUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ScanEditorNMR implements IScanEditorNMR {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanEditorNMR";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorNMR";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/scan-nmr.gif";
	public static final String TOOLTIP = "NMR Editor";
	//
	private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(2));
	//
	private final MPart part;
	private final MDirtyable dirtyable;
	//
	private final IEventBroker eventBroker;
	//
	private ExtendedNMRScanUI extendedNMRScanUI;
	//
	private final Shell shell;
	private DataNMRSelection selection;
	private NMRMeasurementsUI measurementsUI;
	private final ProcessorFactory filterFactory;
	private final PartSupport partSupport;
	private ProcessSupplierContext processSupplierContext;

	@Inject
	public ScanEditorNMR(Composite parent, IEventBroker eventBroker, MPart part, MDirtyable dirtyable, Shell shell, ProcessorFactory filterFactory, PartSupport partSupport, ProcessSupplierContext context) {

		this.partSupport = partSupport;
		this.processSupplierContext = context;
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				executorService.shutdownNow();
			}
		});
		//
		this.part = part;
		this.dirtyable = dirtyable;
		this.eventBroker = eventBroker;
		this.shell = shell;
		this.filterFactory = filterFactory;
		//
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		// TODO
	}

	@PreDestroy
	protected void preDestroy() {

		UpdateNotifierUI.update(Display.getDefault(), IChemClipseEvents.TOPIC_SCAN_NMR_UNLOAD_SELECTION, null);
	}

	@Persist
	public void save() {

		dirtyable.setDirty(false);
	}

	@Override
	public boolean saveAs() {

		return true;
	}

	@Override
	public String getName() {

		return part.getLabel();
	}

	@Override
	public IDataNMRSelection getScanSelection() {

		return selection;
	}

	private void initialize(Composite parent) {

		createEditorPages(parent);
		loadScan();
	}

	private synchronized void loadScan() {

		Object object = part.getObject();
		if(object instanceof Map) {
			/*
			 * String
			 */
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)object;
			File file = new File((String)map.get(EditorSupport.MAP_FILE));
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			try {
				dialog.run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						IProcessingInfo<Collection<IComplexSignalMeasurement<?>>> convert = ScanConverterNMR.convert(file, monitor);
						Collection<IComplexSignalMeasurement<?>> result = convert.getProcessingResult();
						if(result != null) {
							selection = new DataNMRSelection();
							for(IComplexSignalMeasurement<?> measurement : result) {
								selection.addMeasurement(measurement);
							}
							for(IComplexSignalMeasurement<?> measurement : result) {
								if(measurement instanceof FIDMeasurement) {
									selection.setActiveMeasurement(measurement);
									break;
								}
							}
							Display.getDefault().asyncExec(ScanEditorNMR.this::updateSelection);
						} else {
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {

									ProcessingInfoPartSupport.getInstance().update(convert);
									partSupport.closePart(part);
								}
							});
						}
					}
				});
			} catch(InvocationTargetException e) {
				e.printStackTrace();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(object instanceof IComplexSignalMeasurement<?>) {
			selection = new DataNMRSelection();
			String label = addToSelection((IComplexSignalMeasurement<?>)object, selection);
			part.setLabel(label);
			Display.getDefault().asyncExec(this::updateSelection);
		}
	}

	private void updateSelection() {

		extendedNMRScanUI.update(selection);
		measurementsUI.update(selection);
		// selection.addObserver(new Observer() {
		//
		// @Override
		// public void update(Observable o, Object arg) {
		//
		// extendedMeasurementResultUI.update(selection.getMeasurement().getMeasurementResults(), "");
		// }
		// });
	}

	private static String addToSelection(IComplexSignalMeasurement<?> measurement, DataNMRSelection selection) {

		String name = measurement.getDataName();
		if(measurement instanceof Filtered) {
			Object filteredObject = ((Filtered<?, ?>)measurement).getFilterContext().getFilteredObject();
			if(filteredObject instanceof IComplexSignalMeasurement<?>) {
				name = addToSelection((IComplexSignalMeasurement<?>)filteredObject, selection) + " > " + name;
			}
		}
		selection.addMeasurement(measurement);
		return name;
	}

	private void createEditorPages(Composite parent) {

		Composite container = createContainer(parent);
		createToolbar(container);
		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		maximize(sashForm);
		Composite left = new Composite(sashForm, SWT.NONE);
		left.setLayout(new FillLayout());
		Composite right = new Composite(sashForm, SWT.NONE);
		right.setLayout(new FillLayout());
		sashForm.setWeights(new int[]{800, 200});
		extendedNMRScanUI = new ExtendedNMRScanUI(left);
		Composite composite = new Composite(right, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		measurementsUI = new NMRMeasurementsUI(composite, filterFactory, processSupplierContext);
		TreeViewer treeViewer = measurementsUI.getTreeViewer();
		treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		DynamicSettingsUI settingsUI = new DynamicSettingsUI(composite, new GridData(SWT.FILL, SWT.FILL, true, false));
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@SuppressWarnings({"rawtypes", "unchecked"})
			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IComplexSignalMeasurement<?> measurement = measurementsUI.getSelection();
				if(measurement instanceof Filtered) {
					FilterContext<?, ?> context = ((Filtered<?, ?>)measurement).getFilterContext();
					Object filteredObject = context.getFilteredObject();
					if(filteredObject instanceof IComplexSignalMeasurement<?>) {
						IComplexSignalMeasurement<?> original = (IComplexSignalMeasurement<?>)filteredObject;
						settingsUI.setActiveContext(context, new UpdatingObserver(context, measurement, original));
						composite.layout();
						return;
					}
				}
				settingsUI.setActiveContext(null, null);
			}
		});
		// extendedMeasurementResultUI = new ExtendedMeasurementResultUI(composite);
	}

	private EditorToolBar createToolbar(Composite container) {

		EditorToolBar toolBar = new EditorToolBar(container);
		toolBar.addPreferencePages(() -> {
			List<IPreferencePage> list = new ArrayList<>();
			list.add(new PreferencePageProcessors(processSupplierContext));
			return list;
		}, this::updateSetting);
		toolBar.enableToolbarTextButton(Activator.getDefault().getPreferenceStore(), getClass().getSimpleName() + ".showToolBarText");
		return toolBar;
	}

	private void updateSetting() {

		// TODO nothing to do at the moment
	}

	private final class UpdatingObserver<FilteredType, ConfigType> implements Observer {

		private final FilterContext<FilteredType, ConfigType> context;
		private final IComplexSignalMeasurement<?> currentMeasurement;
		private IComplexSignalMeasurement<?> originalMeasurement;

		public UpdatingObserver(FilterContext<FilteredType, ConfigType> context, IComplexSignalMeasurement<?> currentMeasurement, IComplexSignalMeasurement<?> originalMeasurement) {

			this.context = context;
			this.currentMeasurement = currentMeasurement;
			this.originalMeasurement = originalMeasurement;
		}

		@Override
		public void update(Observable o, Object arg) {

			if(context == null) {
				return;
			}
			Filter<ConfigType> filter = context.getFilter();
			if(filter instanceof IMeasurementFilter<?>) {
				IMeasurementFilter<ConfigType> measurementFilter = (IMeasurementFilter<ConfigType>)filter;
				try {
					executorService.submit(new Runnable() {

						@Override
						public void run() {

							try {
								DefaultProcessingResult<Object> result = new DefaultProcessingResult<>();
								ConfigType config = context.getFilterConfig();
								Collection<? extends IMeasurement> filterIMeasurements = measurementFilter.filterIMeasurements(Collections.singleton(originalMeasurement), config, Function.identity(), result, null);
								if(!filterIMeasurements.isEmpty() && !result.hasErrorMessages()) {
									for(IMeasurement measurement : filterIMeasurements) {
										if(measurement instanceof IComplexSignalMeasurement<?>) {
											IComplexSignalMeasurement<?> signalMeasurement = (IComplexSignalMeasurement<?>)measurement;
											if(measurement instanceof Filtered<?, ?>) {
												Filtered<?, ?> filtered = (Filtered<?, ?>)measurement;
												if(filtered.getFilterContext().getFilteredObject() == originalMeasurement) {
													copySignals(signalMeasurement, currentMeasurement);
												}
											}
										}
									}
									Display.getDefault().asyncExec(ScanEditorNMR.this.extendedNMRScanUI::updateScan);
								} else {
									// TODO show an error decoration
								}
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					});
				} catch(RejectedExecutionException e) {
					// then we already have pending updates
				}
			}
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		private void copySignals(IComplexSignalMeasurement<?> from, IComplexSignalMeasurement<?> to) {

			if(to instanceof FilteredFIDMeasurement<?>) {
				if(from instanceof FIDMeasurement) {
					((FilteredFIDMeasurement)to).setSignals(from.getSignals());
				}
			} else if(to instanceof FilteredSpectrumMeasurement<?>) {
				if(from instanceof SpectrumMeasurement) {
					((FilteredSpectrumMeasurement)to).setSignals(from.getSignals());
				}
			}
		}
	}
}
