/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;

public class PcaResulRegistryConfiguration extends AbstractRegistryConfiguration {

	private TableProvider provider;

	public PcaResulRegistryConfiguration(TableProvider provider) {
		super();
		this.provider = provider;
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {

		setFormatCell(configRegistry);
	}

	private void setFormatCell(IConfigRegistry configRegistry) {

		// Set format for sample data
		String norm = provider.getNormalizationData();
		configRegistry.unregisterConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.NORMAL, TableProvider.COLUMN_LABEL_GROUP_DATA);
		configRegistry.unregisterConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.NORMAL, TableProvider.COLUMN_LABEL_SAMPLE_DATA);
		if(norm.equals(TableProvider.NORMALIZATION_NONE)) {
			DefaultDoubleDisplayConverter format = new DefaultDoubleDisplayConverter();
			format.setNumberFormat(ValueFormat.getNumberFormatEnglish());
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
					format, DisplayMode.NORMAL, //
					TableProvider.COLUMN_LABEL_GROUP_DATA);
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
					format, DisplayMode.NORMAL, //
					TableProvider.COLUMN_LABEL_SAMPLE_DATA);
		} else if(norm.equals(TableProvider.NORMALIZATION_COLUMN) || norm.equals(TableProvider.NORMALIZATION_ROW)) {
			NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.US);
			percentFormat.setMaximumFractionDigits(3);
			DefaultDoubleDisplayConverter format = new DefaultDoubleDisplayConverter();
			format.setNumberFormat(percentFormat);
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
					format, DisplayMode.NORMAL, //
					TableProvider.COLUMN_LABEL_GROUP_DATA);
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
					format, DisplayMode.NORMAL, //
					TableProvider.COLUMN_LABEL_SAMPLE_DATA);
		}
		// Set format for retention times
		DefaultDoubleDisplayConverter formatRetTime = new DefaultDoubleDisplayConverter();
		formatRetTime.setNumberFormat(ValueFormat.getNumberFormatEnglish());
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
				formatRetTime, DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_RETENTION_TIMES);
		// Set style for compound
		Style style = new Style();
		style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, //
				style, DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_PEAKS_NAMES);
	}
}
