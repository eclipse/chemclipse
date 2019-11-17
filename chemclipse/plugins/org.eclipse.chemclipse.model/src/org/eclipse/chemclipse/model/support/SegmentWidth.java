/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

public enum SegmentWidth {
	WIDTH_5(5), //
	WIDTH_7(7), //
	WIDTH_9(9), //
	WIDTH_11(11), //
	WIDTH_13(13), //
	WIDTH_15(15), //
	WIDTH_17(17), //
	WIDTH_19(19);

	private int width;

	private SegmentWidth(int segmentWidth) {
		this.width = segmentWidth;
	}

	public int getWidth() {

		return width;
	}

	public static String[][] getElements() {

		String[][] elements = new String[values().length][2];
		int counter = 0;
		for(SegmentWidth segment : values()) {
			elements[counter][0] = Integer.toString(segment.getWidth());
			elements[counter][1] = segment.name();
			counter++;
		}
		return elements;
	}

	public static SegmentWidth getLower(SegmentWidth segmentWidth) {

		SegmentWidth lower = null;
		for(SegmentWidth w : SegmentWidth.values()) {
			int current = w.getWidth();
			if(current < segmentWidth.getWidth()) {
				if(lower == null || current > lower.getWidth()) {
					lower = w;
				}
			}
		}
		return lower;
	}
}
