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
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

public class PcaResulRegistryConfiguration extends AbstractRegistryConfiguration {

	public PcaResulRegistryConfiguration() {
		super();
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {

		setSelectionBox(configRegistry);
	}

	private void setSelectionBox(IConfigRegistry configRegistry) {

		// Set format for sample data
		DefaultDoubleDisplayConverter format = new DefaultDoubleDisplayConverter();
		format.setNumberFormat(ValueFormat.getNumberFormatEnglish());
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
				format, DisplayMode.NORMAL, //
				PcaResultLabelProvider.COLUMN_LABEL_DATA);
		format.setNumberFormat(ValueFormat.getNumberFormatEnglish());
		// Set format for retention times
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, //
				format, DisplayMode.NORMAL, //
				PcaResultLabelProvider.COLUMN_LABEL_RETENTION_TIMES);
		//
	}
}
