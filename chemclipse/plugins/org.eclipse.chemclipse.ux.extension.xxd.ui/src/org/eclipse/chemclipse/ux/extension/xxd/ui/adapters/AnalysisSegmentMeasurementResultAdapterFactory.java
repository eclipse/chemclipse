/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.adapters;

import java.util.Collection;

import org.eclipse.chemclipse.model.results.AnalysisSegmentMeasurementResult;
import org.eclipse.chemclipse.model.results.NoiseSegmentMeasurementResult;
import org.eclipse.chemclipse.model.support.ChromatogramSegment;
import org.eclipse.chemclipse.model.support.IAnalysisSegment;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentColumnDefinition;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentPaintListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.AnalysisSegmentSelectionChangedListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.segments.NoiseAnalysisSegmentColumnDefinition;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swtchart.ICustomPaintListener;

/**
 * Implements a generic adapter factory for well known types, custom implementations can provide an own Adapterfactory for more specific types
 * 
 * @author christoph
 *
 */
public class AnalysisSegmentMeasurementResultAdapterFactory implements IAdapterFactory, ITreeContentProvider {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {

		if(adaptableObject instanceof AnalysisSegmentMeasurementResult<?> result) {
			if(adapterType.isInstance(this)) {
				return adapterType.cast(this);
			}
			if(adapterType == ColumnDefinitionProvider.class) {
				if(result instanceof NoiseSegmentMeasurementResult) {
					return adapterType.cast(new NoiseAnalysisSegmentColumnDefinition(result::notifyListener));
				}
				return adapterType.cast(new AnalysisSegmentColumnDefinition(result::notifyListener));
			}
			if(adapterType == ICustomPaintListener.class) {
				return adapterType.cast(getPaintListener(result));
			}
			if(adapterType == ISelectionChangedListener.class) {
				return adapterType.cast(getSelectionListener(result));
			}
		}
		return null;
	}

	private <X extends IAnalysisSegment> AnalysisSegmentSelectionChangedListener<X> getSelectionListener(AnalysisSegmentMeasurementResult<X> result) {

		return new AnalysisSegmentSelectionChangedListener<>(result.getType(), result::setSelection);
	}

	private <X extends IAnalysisSegment> ICustomPaintListener getPaintListener(AnalysisSegmentMeasurementResult<X> result) {

		AnalysisSegmentColorScheme colorScheme;
		if(result instanceof NoiseSegmentMeasurementResult) {
			colorScheme = AnalysisSegmentColorScheme.NOISE;
		} else {
			colorScheme = AnalysisSegmentColorScheme.ANALYSIS;
		}
		return new AnalysisSegmentPaintListener<X>(colorScheme, result::getResult, result::isSelected);
	}

	@Override
	public Class<?>[] getAdapterList() {

		return new Class<?>[]{IStructuredContentProvider.class, ColumnDefinitionProvider.class, ISelectionChangedListener.class, ICustomPaintListener.class};
	}

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof AnalysisSegmentMeasurementResult<?>) {
			return getTreeNodes(((AnalysisSegmentMeasurementResult<?>)inputElement).getResult().toArray(), null);
		}
		return null;
	}

	private Object[] getTreeNodes(Object[] items, TreeNode parent) {

		TreeNode[] treeNodes = new TreeNode[items.length];
		for(int i = 0; i < treeNodes.length; i++) {
			TreeNode treeNode = new TreeNode(items[i]);
			treeNode.setParent(parent);
			treeNodes[i] = treeNode;
		}
		if(parent != null) {
			parent.setChildren(treeNodes);
		}
		return treeNodes;
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if(parentElement instanceof TreeNode parentNode) {
			if(parentNode.getChildren() == null) {
				Object value = parentNode.getValue();
				if(value instanceof IAnalysisSegment analysisSegment) {
					return getTreeNodes(analysisSegment.getChildSegments().toArray(), parentNode);
				}
			}
			return parentNode.getChildren();
		}
		if(parentElement instanceof IAnalysisSegment analysisSegment) {
			return analysisSegment.getChildSegments().toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {

		if(element instanceof TreeNode treeNode) {
			return treeNode.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if(element instanceof TreeNode treeNode) {
			if(treeNode.hasChildren()) {
				return true;
			}
			Object value = treeNode.getValue();
			if(value instanceof IAnalysisSegment analysisSegment) {
				Collection<? extends IAnalysisSegment> childSegments = analysisSegment.getChildSegments();
				for(IAnalysisSegment segment : childSegments) {
					if(!(segment instanceof ChromatogramSegment)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
