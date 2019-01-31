/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Image;

public class CheckBoxPainterExtended extends CheckBoxPainter {

	private final Image uncheckedImg_unselected;
	private PcaResulDataProvider tableProvider;

	public CheckBoxPainterExtended(PcaResulDataProvider tableProvider) {

		super();
		this.uncheckedImg_unselected = GUIHelper.getImage("unchecked_disabled"); //$NON-NLS-1$
		this.tableProvider = tableProvider;
	}

	/**
	 * @since 1.4
	 */
	public CheckBoxPainterExtended(boolean paintBg, PcaResulDataProvider tableProvider) {

		super(paintBg);
		this.uncheckedImg_unselected = GUIHelper.getImage("checked_disable"); //$NON-NLS-1$
		this.tableProvider = tableProvider;
	}

	@Override
	protected Image getImage(ILayerCell cell, IConfigRegistry configRegistry) {

		int row = cell.getRowIndex();
		if(tableProvider.isRowEditable(row)) {
			return super.getImage(cell, configRegistry);
		} else {
			return uncheckedImg_unselected;
		}
	}
}