/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.segments;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.chemclipse.model.support.AnalysisSegment;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.chemclipse.support.ui.swt.columns.SimpleColumnDefinition;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.IntegerColumnEditingSupport;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeNode;

public class AnalysisSegmentColumnDefinition implements ColumnDefinitionProvider {

	private final Runnable updateListener;

	public AnalysisSegmentColumnDefinition(Runnable updateListener) {
		this.updateListener = updateListener;
	}

	@Override
	public List<ColumnDefinition<?, ?>> getColumnDefinitions() {

		List<ColumnDefinition<?, ?>> list = new ArrayList<>();
		list.add(new SimpleColumnDefinition<>("Start Scan", 100, new AnalysisSegmentColumnLabelProvider<IAnalysisSegment>(IAnalysisSegment.class, IAnalysisSegment::getStartScan)).withEditingSupport(viewer -> new IntegerColumnEditingSupport<IAnalysisSegment>(viewer, getProperty(IAnalysisSegment::getStartScan), new BiConsumer<IAnalysisSegment, Integer>() {

			@Override
			public void accept(IAnalysisSegment segment, Integer value) {

				if(segment instanceof AnalysisSegment) {
					((AnalysisSegment)segment).setStartScan(value);
					updateListener.run();
				}
			}
		})));
		list.add(new SimpleColumnDefinition<>("Stop Scan", 100, new AnalysisSegmentColumnLabelProvider<IAnalysisSegment>(IAnalysisSegment.class, IAnalysisSegment::getStopScan)).withEditingSupport(viewer -> new IntegerColumnEditingSupport<IAnalysisSegment>(viewer, getProperty(IAnalysisSegment::getStopScan), new BiConsumer<IAnalysisSegment, Integer>() {

			@Override
			public void accept(IAnalysisSegment segment, Integer value) {

				if(segment instanceof AnalysisSegment) {
					((AnalysisSegment)segment).setStopScan(value);
					updateListener.run();
				}
			}
		})));
		list.add(new SimpleColumnDefinition<>("Width", 100, new AnalysisSegmentColumnLabelProvider<IAnalysisSegment>(IAnalysisSegment.class, IAnalysisSegment::getWidth)));
		return list;
	}

	private Function<IAnalysisSegment, Integer> getProperty(Function<IAnalysisSegment, Integer> propertyFunction) {

		return propertyFunction.compose(new Function<IAnalysisSegment, IAnalysisSegment>() {

			@Override
			public IAnalysisSegment apply(IAnalysisSegment segment) {

				if(segment instanceof AnalysisSegment) {
					// can only edit AnalysisSegment instances atm
					return segment;
				}
				return null;
			}
		});
	}

	protected static final class AnalysisSegmentColumnLabelProvider<X extends IAnalysisSegment> extends ColumnLabelProvider {

		private final Function<X, Object> converter;
		private final Class<X> type;

		public AnalysisSegmentColumnLabelProvider(Class<X> type, Function<X, Object> converter) {
			this.type = type;
			this.converter = converter;
		}

		@Override
		public String getText(Object element) {

			if(element instanceof TreeNode) {
				element = ((TreeNode)element).getValue();
			}
			if(type.isInstance(element)) {
				return String.valueOf(converter.apply(type.cast(element)));
			}
			return "-";
		}
	}
}
