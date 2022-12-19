/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring target label support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.TargetReferenceLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.TargetDisplaySettingsWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.TargetDisplaySettingsWizardListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.BaseChart;

public class TargetLabelEditAction extends Action {

	public static final boolean DEF_SHOW_PREVIEW = false;
	public static final String P_SHOW_PREVIEW = "TargetLabelEditAction.showPreview";
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final List<Runnable> listeners = new CopyOnWriteArrayList<>();
	private ILabelEditSettings labelEditSettings = null;

	public TargetLabelEditAction(ILabelEditSettings labelEditSettings) {

		super("Labels", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_LABELS, IApplicationImage.SIZE_16x16));
		this.labelEditSettings = labelEditSettings;
		setToolTipText("Manage the labels to display in the chromatogram");
		setShowPreviewDefault(DEF_SHOW_PREVIEW);
	}

	public void setShowPreviewDefault(boolean previewDefaultValue) {

		if(preferenceStore != null) {
			preferenceStore.setDefault(P_SHOW_PREVIEW, previewDefaultValue);
		}
	}

	@Override
	public void run() {

		if(labelEditSettings != null && labelEditSettings.getChromatogramSelection() != null && labelEditSettings.getChromatogramUI() != null) {
			IChromatogramSelection<?, ?> chromatogramSelection = labelEditSettings.getChromatogramSelection();
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			ITargetDisplaySettings targetDisplaySettings = chromatogram;
			List<TargetReference> targetReferences;
			try {
				targetReferences = DisplayUtils.executeBusy(() -> {
					List<TargetReference> targetReferenceList = new ArrayList<>();
					targetReferenceList.addAll(TargetReference.getPeakReferences(chromatogram.getPeaks(), targetDisplaySettings));
					targetReferenceList.addAll(TargetReference.getScanReferences(ChromatogramDataSupport.getIdentifiedScans(chromatogram), targetDisplaySettings));
					Collections.sort(targetReferenceList, new Comparator<TargetReference>() {

						@Override
						public int compare(TargetReference targetReference1, TargetReference targetReference2) {

							int compare = Double.compare(targetReference1.getSignal().getX(), targetReference2.getSignal().getX());
							if(compare == 0) {
								compare = targetReference1.getRetentionTimeMinutes().compareToIgnoreCase(targetReference2.getRetentionTimeMinutes());
							}
							//
							return compare;
						}
					});
					return targetReferenceList;
				});
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} catch(ExecutionException e) {
				throw new RuntimeException("Can't get the reference", e.getCause());
			}
			/*
			 * References
			 */
			TargetReferenceLabelMarker targetReferenceLabelMarker = new TargetReferenceLabelMarker(true, PreferenceConstants.DEF_SYMBOL_SIZE * 2);
			ChromatogramChart chromatogramChart = labelEditSettings.getChromatogramUI().getChromatogramChart();
			chromatogramChart.getBaseChart().getPlotArea().addCustomPaintListener(targetReferenceLabelMarker);
			TargetDisplaySettingsWizardListener settingsWizardListener = new TargetDisplaySettingsWizardListener() {

				private boolean showPreview = true;

				@Override
				public String getLabelID() {

					return "RT";
				}

				@Override
				public void setPreviewSettings(ITargetDisplaySettings targetDisplaySettings) {

					if(targetDisplaySettings == null) {
						return;
					}
					//
					if(showPreview) {
						labelEditSettings.getChromatogramUI().update();
						chromatogramChart.getBaseChart().redraw();
					}
				}

				@Override
				public boolean isShowPreview() {

					if(preferenceStore != null) {
						return preferenceStore.getBoolean(P_SHOW_PREVIEW);
					}
					return showPreview;
				}

				@Override
				public void setShowPreview(boolean preview) {

					if(preferenceStore != null) {
						preferenceStore.setValue(P_SHOW_PREVIEW, preview);
					}
					this.showPreview = preview;
				}
			};
			/*
			 * Opens the Wizard
			 */
			BaseChart baseChart = chromatogramChart.getBaseChart();
			Shell shell = baseChart.getShell();
			boolean settingsChanged = TargetDisplaySettingsWizard.openWizard(shell, targetReferences, chromatogramSelection.getChromatogram(), settingsWizardListener);
			baseChart.getPlotArea().removeCustomPaintListener(targetReferenceLabelMarker);
			settingsWizardListener.setPreviewSettings(null);
			if(settingsChanged) {
				labelEditSettings.getChromatogramUI().update();
				chromatogramChart.getBaseChart().redraw();
				listeners.forEach(Runnable::run);
			}
		}
	}

	public void addChangeListener(Runnable listener) {

		listeners.add(listener);
	}
}