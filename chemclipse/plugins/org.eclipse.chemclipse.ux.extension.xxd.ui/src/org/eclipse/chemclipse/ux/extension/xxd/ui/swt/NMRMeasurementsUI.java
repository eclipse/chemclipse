/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.core.IComplexSignalMeasurement;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.filter.IMeasurementFilter;
import org.eclipse.chemclipse.nmr.model.core.FIDMeasurement;
import org.eclipse.chemclipse.nmr.model.core.SpectrumMeasurement;
import org.eclipse.chemclipse.nmr.model.selection.DataNMRSelection;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection.ChangeType;
import org.eclipse.chemclipse.processing.filter.FilterFactory;
import org.eclipse.chemclipse.processing.filter.Filtered;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.actions.IMeasurementFilterAction;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

public class NMRMeasurementsUI implements Observer {

	private static final Image IMAGE_FREQUENCY = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_NMR, IApplicationImage.SIZE_16x16);
	private static final Image IMAGE_FID = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_FID, IApplicationImage.SIZE_16x16);
	private static final TreeNode[] EMPTY = new TreeNode[0];
	private TreeViewer treeViewer;
	private DataNMRSelection selection;
	private FilterFactory filterFactory;
	private ProcessTypeSupport processTypeSupport;

	public NMRMeasurementsUI(Composite parent, FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
		processTypeSupport = new ProcessTypeSupport(filterFactory);
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		treeViewer.setUseHashlookup(true);
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.setComparer(new IElementComparer() {

			@Override
			public int hashCode(Object element) {

				if(element instanceof TreeNode) {
					return element.hashCode();
				}
				return element.hashCode();
			}

			@Override
			public boolean equals(Object a, Object b) {

				if(a instanceof TreeNode) {
					a = ((TreeNode)a).getValue();
				}
				if(b instanceof TreeNode) {
					b = ((TreeNode)b).getValue();
				}
				return a.equals(b);
			}
		});
		createColumn(treeViewer, "Measurements", 300, new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				element = getMeasurement(element);
				if(element instanceof IMeasurement) {
					return ((IMeasurement)element).getDataName();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {

				element = getMeasurement(element);
				if(element instanceof FIDMeasurement) {
					return IMAGE_FID;
				}
				if(element instanceof SpectrumMeasurement) {
					return IMAGE_FREQUENCY;
				}
				return super.getImage(element);
			}
		});
		treeViewer.setContentProvider(new TreeNodeContentProvider());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IComplexSignalMeasurement<?> measurement = getMeasurement(treeViewer.getStructuredSelection().getFirstElement());
				if(selection != null) {
					selection.setActiveMeasurement(measurement);
				}
			}
		});
		createContextMenu();
	}

	private void createContextMenu() {

		MenuManager contextMenu = new MenuManager("ViewerContextMenu"); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager mgr) {

				Set<IComplexSignalMeasurement<?>> measurements = Collections.singleton(selection.getMeasurement());
				BiFunction<IMeasurementFilter<?>, Map<String, ?>, Boolean> acceptor = new BiFunction<IMeasurementFilter<?>, Map<String, ?>, Boolean>() {

					@Override
					public Boolean apply(IMeasurementFilter<?> filter, Map<String, ?> properties) {

						return filter.acceptsIMeasurements(measurements);
					}
				};
				Collection<IMeasurementFilter<?>> filters = filterFactory.getFilters(FilterFactory.genericClass(IMeasurementFilter.class), acceptor);
				Consumer<Collection<? extends IMeasurement>> consumer = new Consumer<Collection<? extends IMeasurement>>() {

					@Override
					public void accept(Collection<? extends IMeasurement> filtered) {

						for(IMeasurement item : filtered) {
							if(item instanceof IComplexSignalMeasurement<?>) {
								if(selection != null) {
									selection.addMeasurement((IComplexSignalMeasurement<?>)item);
								}
							}
						}
					}
				};
				for(IMeasurementFilter<?> filter : filters) {
					IAction action = new IMeasurementFilterAction(filter, measurements, consumer, processTypeSupport);
					mgr.add(action);
				}
				mgr.add(new DeleteAction());
			}
		});
		Menu menu = contextMenu.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
	}

	private final class DeleteAction extends Action {

		public DeleteAction() {
			setText("Delete");
		}

		@Override
		public void run() {

			if(selection != null) {
				ITreeSelection structuredSelection = treeViewer.getStructuredSelection();
				Object element = structuredSelection.getFirstElement();
				if(element instanceof TreeNode) {
					remove((TreeNode)element, selection);
				}
			}
		}
	}

	private static void remove(TreeNode node, DataNMRSelection selection) {

		TreeNode[] children = node.getChildren();
		if(children != null) {
			for(TreeNode child : children) {
				remove(child, selection);
			}
		}
		Object value = node.getValue();
		if(value instanceof IComplexSignalMeasurement<?>) {
			if(value instanceof Filtered<?, ?>) {
				selection.removeMeasurement((IComplexSignalMeasurement<?>)value);
			}
		}
	}

	private static IComplexSignalMeasurement<?> getMeasurement(Object value) {

		if(value instanceof TreeNode) {
			return getMeasurement(((TreeNode)value).getValue());
		}
		if(value instanceof IComplexSignalMeasurement<?>) {
			return (IComplexSignalMeasurement<?>)value;
		}
		return null;
	}

	public void update(DataNMRSelection selection) {

		if(this.selection != null) {
			this.selection.removeObserver(this);
		}
		this.selection = selection;
		updateTree();
		selection.addObserver(this);
	}

	public void updateTree() {

		treeViewer.setInput(createTreeNodes(selection).toArray(EMPTY));
		treeViewer.refresh();
		treeViewer.expandAll();
		IComplexSignalMeasurement<?> measurement = selection.getMeasurement();
		if(measurement == null) {
			treeViewer.setSelection(StructuredSelection.EMPTY);
		} else {
			treeViewer.setSelection(new StructuredSelection(measurement));
		}
	}

	private List<TreeNode> createTreeNodes(DataNMRSelection selection) {

		Collection<IComplexSignalMeasurement<?>> measurements = new ArrayList<>(Arrays.asList(selection.getMeasurements()));
		List<TreeNode> list = new ArrayList<>();
		for(Iterator<IComplexSignalMeasurement<?>> iterator = measurements.iterator(); iterator.hasNext();) {
			IComplexSignalMeasurement<?> measurement = iterator.next();
			if(measurement instanceof Filtered) {
				continue;
			}
			iterator.remove();
			TreeNode treeNode = new TreeNode(measurement);
			list.add(treeNode);
		}
		for(TreeNode treeNode : list) {
			findChildren(treeNode, measurements);
		}
		return list;
	}

	private void findChildren(TreeNode parent, Collection<IComplexSignalMeasurement<?>> measurements) {

		List<TreeNode> children = new ArrayList<>();
		for(Iterator<IComplexSignalMeasurement<?>> iterator = measurements.iterator(); iterator.hasNext();) {
			IComplexSignalMeasurement<?> measurement = iterator.next();
			if(measurement instanceof Filtered<?, ?>) {
				Filtered<?, ?> filtered = (Filtered<?, ?>)measurement;
				if(filtered.getFilterContext().getFilteredObject() == parent.getValue()) {
					iterator.remove();
					TreeNode childNode = new TreeNode(filtered);
					childNode.setParent(parent);
					children.add(childNode);
				}
			}
		}
		for(TreeNode childNode : children) {
			findChildren(childNode, measurements);
		}
		parent.setChildren(children.toArray(EMPTY));
	}

	private static TreeViewerColumn createColumn(TreeViewer treeViewer, String text, int width, CellLabelProvider labelProvider) {

		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		column.getColumn().setText(text);
		column.getColumn().setWidth(width);
		column.setLabelProvider(labelProvider);
		return column;
	}

	@Override
	public void update(Observable o, Object arg) {

		if(arg == ChangeType.NEW_ITEM || arg == ChangeType.REMOVED_ITEM) {
			Display.getDefault().asyncExec(this::updateTree);
		}
	}
}
