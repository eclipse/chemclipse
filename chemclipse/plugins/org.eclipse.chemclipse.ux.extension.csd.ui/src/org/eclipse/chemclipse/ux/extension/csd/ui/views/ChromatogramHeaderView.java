/*******************************************************************************
 * Copyright (c) 2016, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics, Logging
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ChromatogramHeaderView {

	private static final Logger logger = Logger.getLogger(ChromatogramHeaderView.class);
	@Inject
	private MPart part;
	@Inject
	private EPartService partService;
	private Text text;
	//
	private SimpleDateFormat dateFormat;
	private DecimalFormat decimalFormat;

	@Inject
	public ChromatogramHeaderView(Composite parent, IEventBroker eventBroker) {

		//
		dateFormat = ValueFormat.getDateFormatEnglish();
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
		//
		text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram overview updates.
			 */
			EventHandler eventHandlerFileOverview = new EventHandler() {

				@Override
				public void handleEvent(Event event) {

					try {
						Object object = event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_RAWFILE);
						if(object instanceof File) {
							setChromatogram((File)object);
						}
					} catch(Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE, eventHandlerFileOverview);
			/*
			 * Receives and handles chromatogram instances overview updates.
			 */
			EventHandler eventHandlerInstanceOverview = new EventHandler() {

				@Override
				public void handleEvent(Event event) {

					try {
						Object object = event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_OVERVIEW);
						if(object instanceof IChromatogramOverview) {
							IChromatogramOverview chromatogramOverview = (IChromatogramOverview)object;
							updateChromatogram(chromatogramOverview);
						}
					} catch(Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW, eventHandlerInstanceOverview);
		}
	}

	@Focus
	public void setFocus() {

		text.setFocus();
	}

	/**
	 * Sets the chromatogram overview.
	 *
	 * @param chromatogramOverview
	 */
	private void updateChromatogram(IChromatogramOverview chromatogramOverview) {

		if(partService.isPartVisible(part) && chromatogramOverview != null) {
			StringBuilder builder = new StringBuilder();
			addHeaderLine(builder, "Name", chromatogramOverview.getName());
			addHeaderLine(builder, "Data Name", chromatogramOverview.getDataName());
			addHeaderLine(builder, "Operator", chromatogramOverview.getOperator());
			Date date = chromatogramOverview.getDate();
			if(date != null) {
				addHeaderLine(builder, "Date", dateFormat.format(chromatogramOverview.getDate()));
			} else {
				addHeaderLine(builder, "Date", "");
			}
			addHeaderLine(builder, "Info", chromatogramOverview.getShortInfo());
			addHeaderLine(builder, "Misc", chromatogramOverview.getMiscInfo());
			addHeaderLine(builder, "Misc (separated)", chromatogramOverview.getMiscInfoSeparated());
			addHeaderLine(builder, "Details", chromatogramOverview.getDetailedInfo());
			addHeaderLine(builder, "Scans", Integer.toString(chromatogramOverview.getNumberOfScans()));
			addHeaderLine(builder, "Start RT (min)", decimalFormat.format(chromatogramOverview.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			addHeaderLine(builder, "Stop RT (min)", decimalFormat.format(chromatogramOverview.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			addHeaderLine(builder, "Barcode", chromatogramOverview.getBarcode());
			text.setText(builder.toString());
		}
	}

	private void addHeaderLine(StringBuilder builder, String key, String value) {

		builder.append(key);
		builder.append(": ");
		builder.append(value);
		builder.append("\n");
	}

	/**
	 * Try to show the overview of the given chromatogram overview.
	 *
	 * @param file
	 * @throws FileIsEmptyException
	 * @throws FileIsNotReadableException
	 * @throws NoChromatogramConverterAvailableException
	 * @throws FileNotFoundException
	 */
	private void setChromatogram(File file) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException {

		/*
		 * Update the ui only if the actual view part is visible.
		 */
		if(partService.isPartVisible(part)) {
			/*
			 * Load the chromatogram overview.
			 */
			IProcessingInfo<IChromatogramOverview> processingInfo = ChromatogramConverterCSD.getInstance().convertOverview(file, new NullProgressMonitor());
			IChromatogramOverview chromatogramOverview = processingInfo.getProcessingResult();
			if(chromatogramOverview != null) {
				updateChromatogram(chromatogramOverview);
			}
		}
	}
}
