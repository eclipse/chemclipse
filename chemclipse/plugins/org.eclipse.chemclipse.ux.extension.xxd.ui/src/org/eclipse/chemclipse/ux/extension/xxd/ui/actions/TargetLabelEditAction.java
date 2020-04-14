/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.TargetReferenceLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.SignalTargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.TargetDisplaySettingsWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.TargetDisplaySettingsWizardListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.IScrollableChart;

public class TargetLabelEditAction extends Action {

	public static final boolean DEF_SHOW_PREVIEW = false;
	public static final String P_SHOW_PREVIEW = "TargetLabelEditAction.showPreview";
	private final LabelChart labelChart;
	private final IPreferenceStore preferenceStore;
	private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

	public TargetLabelEditAction(LabelChart labelChart, IPreferenceStore preferenceStore) {
		super("Labels", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_LABELS, IApplicationImage.SIZE_16x16));
		this.labelChart = labelChart;
		this.preferenceStore = preferenceStore;
		setToolTipText("Mange the labels to display in the chromatogram");
		setShowPreviewDefault(DEF_SHOW_PREVIEW);
	}

	public void setShowPreviewDefault(boolean previewDefaultValue) {

		if(preferenceStore != null) {
			preferenceStore.setDefault(P_SHOW_PREVIEW, previewDefaultValue);
		}
	}

	@Override
	public void run() {

		IChromatogramSelection<?, ?> chromatogramSelection = labelChart.getChromatogramSelection();
		if(chromatogramSelection != null) {
			List<SignalTargetReference> identifications;
			try {
				identifications = DisplayUtils.executeBusy(() -> {
					List<SignalTargetReference> list = new ArrayList<>();
					list.addAll(SignalTargetReference.getPeakReferences(chromatogramSelection.getChromatogram().getPeaks()));
					list.addAll(SignalTargetReference.getScanReferences(ChromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram())));
					Collections.sort(list, new Comparator<SignalTargetReference>() {

						@Override
						public int compare(SignalTargetReference o1, SignalTargetReference o2) {

							int compare = Double.compare(o1.getSignal().getX(), o2.getSignal().getX());
							if(compare == 0) {
								return o1.getName().compareToIgnoreCase(o2.getName());
							} else {
								return compare;
							}
						}
					});
					return list;
				});
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} catch(ExecutionException e) {
				throw new RuntimeException("can't get reference", e.getCause());
			}
			IScrollableChart chart = labelChart.getChart();
			TargetReferenceLabelMarker previewMarker = new TargetReferenceLabelMarker(chart.getBaseChart(), true, PreferenceConstants.DEF_SYMBOL_SIZE * 2, preferenceStore);
			chart.getBaseChart().getPlotArea().addCustomPaintListener(previewMarker);
			TargetDisplaySettingsWizardListener listener = new TargetDisplaySettingsWizardListener() {

				boolean previewDisabled = true;
				private boolean showPreview;

				@Override
				public String getIDLabel() {

					return "RT";
				}

				@Override
				public void setPreviewSettings(TargetDisplaySettings previewSettings, Predicate<TargetReference> activeFilter) {

					if(previewSettings == null && previewDisabled) {
						return;
					}
					previewDisabled = previewSettings == null;
					labelChart.setChartMarkerVisible(previewDisabled);
					Predicate<TargetReference> settingsFilter = previewMarker.setData(identifications, previewSettings, activeFilter);
					if(previewDisabled) {
						chart.setRange(IExtendedChart.X_AXIS, new Range(chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime()));
					} else {
						double minRT = Double.NaN;
						double maxRT = Double.NaN;
						for(SignalTargetReference scanTargetReference : identifications) {
							if(settingsFilter.test(scanTargetReference) && (activeFilter == null || activeFilter.test(scanTargetReference))) {
								double rt = scanTargetReference.getSignal().getX();
								if(Double.isNaN(minRT) || rt < minRT) {
									minRT = rt;
								}
								if(Double.isNaN(maxRT) || rt > maxRT) {
									maxRT = rt;
								}
							}
						}
						int absStart = chromatogramSelection.getChromatogram().getStartRetentionTime();
						if(Double.isNaN(minRT)) {
							minRT = absStart;
						}
						int absStop = chromatogramSelection.getChromatogram().getStopRetentionTime();
						if(Double.isNaN(maxRT)) {
							maxRT = absStop;
						}
						long windowOffset = TimeUnit.MINUTES.toMillis(1);
						chart.setRange(IExtendedChart.X_AXIS, new Range(Math.max(minRT - windowOffset, absStart), Math.min(absStop, maxRT + windowOffset)));
					}
					labelChart.redraw();
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
			boolean settingsChanged = TargetDisplaySettingsWizard.openWizard(chart.getBaseChart().getShell(), identifications, listener, labelChart.getTargetSettings());
			chart.getBaseChart().getPlotArea().removeCustomPaintListener(previewMarker);
			listener.setPreviewSettings(null, null);
			if(settingsChanged) {
				listeners.forEach(Runnable::run);
			}
		}
	}

	public void addChangeListener(Runnable listener) {

		listeners.add(listener);
	}

	public static interface LabelChart {

		IScrollableChart getChart();

		/**
		 * triggers a simple redraw action
		 */
		void redraw();

		TargetDisplaySettings getTargetSettings();

		IChromatogramSelection<?, ?> getChromatogramSelection();

		void setChartMarkerVisible(boolean visible);
	}
}
