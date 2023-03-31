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
 * Matthias Mailänder - adjust the x label
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.converter.scan.IScanConverterSupport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.converter.core.PlateConverterPCR;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.xxd.process.ui.menu.IMenuIcon;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IPrimaryAxisSettings;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.linecharts.LineChart;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class ChartPCR extends LineChart {

	private static final String MENU_ICON = "org.eclipse.chemclipse.xxd.process.ui.menu.icon";
	private static final Logger logger = Logger.getLogger(ChartPCR.class);
	//
	private IPlate plate;

	public ChartPCR() {

		super();
		initialize();
	}

	public ChartPCR(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public void updatePlate(IPlate plate) {

		this.plate = plate;
	}

	private void initialize() {

		modify();
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setTitleVisible(false);
	}

	private void modify() {

		IChartSettings chartSettings = getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(false);
		chartSettings.getRangeRestriction().setZeroX(false);
		chartSettings.getRangeRestriction().setZeroY(false);
		chartSettings.setEnableTooltips(true);
		//
		setPrimaryAxisSet(chartSettings);
		setExportMenu(chartSettings);
		applySettings(chartSettings);
	}

	private void setPrimaryAxisSet(IChartSettings chartSettings) {

		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Cycle");
		primaryAxisSettingsX.setDecimalFormat(new DecimalFormat(("0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsX.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		primaryAxisSettingsX.setPosition(Position.Primary);
		primaryAxisSettingsX.setVisible(true);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Fluorescence");
		primaryAxisSettingsY.setDecimalFormat(new DecimalFormat(("0.0#E0"), new DecimalFormatSymbols(Locale.ENGLISH)));
		primaryAxisSettingsY.setColor(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
	}

	@SuppressWarnings("deprecation")
	private void setExportMenu(IChartSettings settings) {

		IScanConverterSupport converterSupport = PlateConverterPCR.getScanConverterSupport();
		List<ISupplier> exportSupplier = converterSupport.getExportSupplier();
		for(ISupplier supplier : exportSupplier) {
			settings.addMenuEntry(new IChartMenuEntry() {

				@Override
				public String getName() {

					return supplier.getFilterName();
				}

				@Override
				public String getToolTipText() {

					return supplier.getDescription();
				}

				@Override
				public Image getIcon() {

					IExtensionRegistry registry = Platform.getExtensionRegistry();
					IConfigurationElement[] config = registry.getConfigurationElementsFor(MENU_ICON);
					try {
						for(IConfigurationElement element : config) {
							final String id = element.getAttribute("id");
							if(!(supplier.getId().equals(id))) {
								continue;
							}
							final Object object = element.createExecutableExtension("class");
							if(object instanceof IMenuIcon menuIcon) {
								return menuIcon.getImage();
							}
						}
					} catch(CoreException e) {
						logger.warn(e);
					}
					return null;
				}

				@Override
				public String getCategory() {

					return "Export";
				}

				@Override
				public void execute(Shell shell, ScrollableChart scrollableChart) {

					if(plate == null) {
						return;
					}
					FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
					fileDialog.setText("PCR Export");
					fileDialog.setFileName(plate.getName() + "." + supplier.getFileExtension());
					fileDialog.setFilterExtensions(new String[]{"*" + supplier.getFileExtension()});
					fileDialog.setFilterNames(new String[]{supplier.getFilterName()});
					String pathname = fileDialog.open();
					if(pathname != null) {
						File file = new File(pathname);
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
						try {
							dialog.run(true, true, new IRunnableWithProgress() {

								@Override
								public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

									IProcessingInfo<File> convert = PlateConverterPCR.convert(file, plate, supplier.getId(), monitor);
									ProcessingInfoPartSupport.getInstance().update(convert);
								}
							});
						} catch(InvocationTargetException e) {
							IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
							processingInfo.addErrorMessage("PCR Export", "Export failed", e.getCause());
							ProcessingInfoPartSupport.getInstance().update(processingInfo);
						} catch(InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
			});
		}
	}
}
