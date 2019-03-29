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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.filter.FilterFactory;
import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.nmr.converter.core.ScanConverterNMR;
import org.eclipse.chemclipse.nmr.model.core.FIDMeasurement;
import org.eclipse.chemclipse.nmr.model.selection.DataNMRSelection;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.editors.IScanEditorNMR;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.NMRMeasurementsUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedNMRScanUI;
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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ScanEditorNMR extends AbstractDataUpdateSupport implements IScanEditorNMR, IDataUpdateSupport {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.scanEditorNMR";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ScanEditorNMR";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/scan-nmr.gif";
	public static final String TOOLTIP = "NMR Editor";
	//
	private MPart part;
	private MDirtyable dirtyable;
	//
	private IEventBroker eventBroker;
	//
	private ExtendedNMRScanUI extendedNMRScanUI;
	//
	private Shell shell;
	private DataNMRSelection selection;
	private NMRMeasurementsUI measurementsUI;
	private FilterFactory filterFactory;

	@Inject
	public ScanEditorNMR(Composite parent, IEventBroker eventBroker, MPart part, MDirtyable dirtyable, Shell shell, FilterFactory filterFactory) {
		super(part);
		//
		this.part = part;
		this.dirtyable = dirtyable;
		this.eventBroker = eventBroker;
		this.shell = shell;
		this.filterFactory = filterFactory;
		//
		initialize(parent);
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SCAN_SELECTION);
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

		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_NMR_UNLOAD_SELECTION, null);
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
		System.gc();
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
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {

									extendedNMRScanUI.update(selection);
									measurementsUI.update(selection);
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
	}

	private void createEditorPages(Composite parent) {

		createScanPage(parent);
	}

	private void createScanPage(Composite parent) {

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		Composite left = new Composite(sashForm, SWT.NONE);
		left.setLayout(new FillLayout());
		Composite right = new Composite(sashForm, SWT.NONE);
		right.setLayout(new FillLayout());
		sashForm.setWeights(new int[]{800, 200});
		extendedNMRScanUI = new ExtendedNMRScanUI(left);
		measurementsUI = new NMRMeasurementsUI(right, filterFactory);
	}
}
