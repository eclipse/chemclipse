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

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;

public class PcaResulRegistryConfiguration extends AbstractRegistryConfiguration {

	public PcaResulRegistryConfiguration(TableProvider provider) {
		super();
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {

		setFormatCell(configRegistry);
	}

	private void setFormatCell(IConfigRegistry configRegistry) {

		// Set format for sample data
		configRegistry.unregisterConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.NORMAL, TableProvider.COLUMN_LABEL_GROUP_DATA);
		configRegistry.unregisterConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.NORMAL, TableProvider.COLUMN_LABEL_SAMPLE_DATA);
		DefaultDoubleDisplayConverter format = new DefaultDoubleDisplayConverter();
		format.setNumberFormat(ValueFormat.getNumberFormatEnglish());
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
				format, DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_GROUP_DATA);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
				format, DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_SAMPLE_DATA);
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
		//
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
				new DefaultBooleanDisplayConverter(), //
				DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_SELECTED);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, //
				new CheckBoxPainter(), //
				DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_SELECTED);
		// configure editing
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, //
				IEditableRule.ALWAYS_EDITABLE, DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_SELECTED);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, //
				new CheckBoxCellEditor(), //
				DisplayMode.NORMAL, //
				TableProvider.COLUMN_LABEL_SELECTED);
	}
}
