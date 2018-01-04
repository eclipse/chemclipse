/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ChromatogramHeaderView extends AbstractChromatogramOverviewView implements IChromatogramOverviewView {

	private Text text;
	private SimpleDateFormat dateFormat;
	private DecimalFormat decimalFormat;

	@Inject
	public ChromatogramHeaderView(Composite parent, MPart part, EPartService partService, IEventBroker eventBroker) {
		//
		super(part, partService, eventBroker);
		//
		dateFormat = ValueFormat.getDateFormatEnglish();
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
		//
		text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	}

	@Focus
	public void setFocus() {

		text.setFocus();
	}

	@Override
	public void updateChromatogram(IChromatogramOverview chromatogramOverview) {

		if(doUpdate(chromatogramOverview)) {
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
			addHeaderLine(builder, "Start RT (min)", decimalFormat.format(chromatogramOverview.getStartRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			addHeaderLine(builder, "Stop RT (min)", decimalFormat.format(chromatogramOverview.getStopRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
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
}
