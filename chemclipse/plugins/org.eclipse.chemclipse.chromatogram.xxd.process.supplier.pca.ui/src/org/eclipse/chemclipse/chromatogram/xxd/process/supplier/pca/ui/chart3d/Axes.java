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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.support.text.ValueFormat;

import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Axes {

	private boolean addPlane = false;
	final private PhongMaterial cornMaterial = new PhongMaterial();
	private Chart3DData data;
	final private NumberFormat format = ValueFormat.getNumberFormatEnglish();
	final private PhongMaterial gridaxisMaterial = new PhongMaterial();
	private double lableDistance;
	private double lableDistanceNameAxis;
	private double lengthX;
	private double lengthY;
	private double lengthZ;
	private double lineSpacing;
	private double lineSpacingScaled;
	final private Group mainGroup = new Group();
	private double maxNumberLine;
	private double maxX;
	private double maxY;
	private double maxZ;
	private double minX;
	private double minY;
	private double minZ;
	final private PhongMaterial planeMaterial = new PhongMaterial();
	private double tickLenght;
	private double widthCorn;

	public Axes(Chart3DData chart3dData) {
		this.data = chart3dData;
		maxNumberLine = 10;
		cornMaterial.setDiffuseColor(Color.BLACK);
		cornMaterial.setSpecularColor(Color.BLACK.brighter());
		/*
		 * set look plane
		 */
		gridaxisMaterial.setDiffuseColor(Color.GRAY);
		gridaxisMaterial.setSpecularColor(Color.GRAY.brighter());
		planeMaterial.setDiffuseColor(Color.LIGHTGRAY);
		planeMaterial.setSpecularColor(Color.LIGHTGRAY.brighter());
		/*
		 * set fix size
		 */
		tickLenght = 40;
		lableDistance = 70;
		lableDistanceNameAxis = 200;
		widthCorn = 4;
	}

	public Group createAxes() {

		return mainGroup;
	}

	private Node createCorn(double height, Point3D rotation, double rotate, double translationX, double translationY, double translationZ, double lengthening) {

		Box corn = new Box(height + lengthening, widthCorn, widthCorn);
		corn.setMaterial(cornMaterial);
		corn.setRotationAxis(rotation);
		corn.setRotate(rotate);
		corn.setTranslateX(translationX);
		corn.setTranslateY(translationY);
		corn.setTranslateZ(translationZ);
		return corn;
	}

	private Group createCorns() {

		Group group = new Group();
		Node corn = createCorn(lengthX, Rotate.X_AXIS, 0, 0, minY, minZ, 500);
		group.getChildren().add(corn);
		corn = createCorn(lengthX, Rotate.X_AXIS, 0, 0, maxY, minZ, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthX, Rotate.X_AXIS, 0, 0, minY, maxZ, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthX, Rotate.X_AXIS, 0, 0, maxY, maxZ, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthY, Rotate.Z_AXIS, 90, minX, 0, minZ, 500);
		group.getChildren().add(corn);
		corn = createCorn(lengthY, Rotate.Z_AXIS, 90, maxX, 0, minZ, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthY, Rotate.Z_AXIS, 90, minX, 0, maxZ, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthY, Rotate.Z_AXIS, 90, maxX, 0, maxZ, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthZ, Rotate.Y_AXIS, 90, minX, minY, 0, 500);
		group.getChildren().add(corn);
		corn = createCorn(lengthZ, Rotate.Y_AXIS, 90, maxX, minY, 0, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthZ, Rotate.Y_AXIS, 90, minX, maxY, 0, 0);
		group.getChildren().add(corn);
		corn = createCorn(lengthZ, Rotate.Y_AXIS, 90, maxX, maxY, 0, 0);
		group.getChildren().add(corn);
		return group;
	}

	private Node createGridLine(Point3D origin, Point3D target) {

		Point3D yAxis = new Point3D(0, 1, 0);
		Point3D diff = target.subtract(origin);
		double height = diff.magnitude();
		Point3D mid = target.midpoint(origin);
		Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
		Point3D axisOfRotation = diff.crossProduct(yAxis);
		double angle = Math.acos(diff.normalize().dotProduct(yAxis));
		Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
		Box line = new Box(2, height, 2);
		line.setMaterial(gridaxisMaterial);
		line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
		return line;
	}

	private Group createPlane(double sizeH, double sizeV) {

		Group group = new Group();
		if(addPlane) {
			Box plane = new Box(sizeH, sizeV, 2);
			plane.setMaterial(planeMaterial);
			group.getChildren().add(plane);
		}
		double startX = -sizeH / 2;
		double finishX = sizeH / 2;
		double startY = -sizeV / 2;
		double finishY = sizeV / 2;
		for(double x = startX; x <= finishX; x += lineSpacingScaled) {
			group.getChildren().add(createGridLine(new Point3D(x, startY, 0), new Point3D(x, finishY, 0)));
		}
		for(double y = startY; y <= finishY; y += lineSpacingScaled) {
			group.getChildren().add(createGridLine(new Point3D(startX, y, 0), new Point3D(finishX, y, 0)));
		}
		return group;
	}

	private Group createXLabels() {

		Group group = new Group();
		String nameAxis = data.getLabelAxisX();
		Label name = new Label(nameAxis);
		name.setTranslateZ(lableDistanceNameAxis);
		name.setRotationAxis(Rotate.X_AXIS);
		name.setRotate(90);
		setLabelStyleAxis(name);
		group.getChildren().add(name);
		for(int i = (int)(minX / lineSpacingScaled) + 1; i < (int)(maxX / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * lineSpacing));
			setLabelStyle(label);
			Rotate xRotate = new Rotate(90, 0, 0, 0, Rotate.X_AXIS);
			Rotate yRotate = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
			label.getTransforms().addAll(yRotate, xRotate);
			label.setTranslateX(i * lineSpacingScaled);
			label.setTranslateZ(2 * lableDistance);
			Node tick = createGridLine(new Point3D(i * lineSpacingScaled, 0, -tickLenght / 2), new Point3D(i * lineSpacingScaled, 0, tickLenght / 2));
			group.getChildren().add(tick);
			group.getChildren().add(label);
		}
		group.setTranslateX((maxX - minX) / 2 + minX);
		group.setTranslateY(minY);
		group.setTranslateZ(maxZ);
		return group;
	}

	private Group createXYPlane() {

		Group group = createPlane(lengthX, lengthY);
		group.setTranslateZ(minZ);
		return group;
	}

	private Group createXZPlane() {

		Group group = createPlane(lengthX, lengthZ);
		group.setTranslateY(minY);
		group.setRotationAxis(Rotate.X_AXIS);
		group.setRotate(90);
		return group;
	}

	private Group createYLabels() {

		Group group = new Group();
		String axisName = data.getLabelAxisY();
		Label name = new Label(axisName);
		Rotate zRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Z_AXIS);
		Rotate yRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS);
		name.getTransforms().addAll(yRotateAxis, zRotateAxis);
		name.setTranslateZ(lableDistanceNameAxis);
		setLabelStyleAxis(name);
		group.getChildren().add(name);
		for(int i = (int)(minY / lineSpacingScaled) + 1; i < (int)(maxY / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * lineSpacing));
			setLabelStyle(label);
			Rotate xRotate = new Rotate(180, 0, 0, 0, Rotate.X_AXIS);
			Rotate yRotate = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
			label.getTransforms().addAll(yRotate, xRotate);
			label.setTranslateY(i * lineSpacingScaled);
			label.setTranslateZ(2 * lableDistance);
			Node tick = createGridLine(new Point3D(0, i * lineSpacingScaled, -tickLenght / 2), new Point3D(0, i * lineSpacingScaled, tickLenght / 2));
			group.getChildren().add(tick);
			group.getChildren().add(label);
		}
		group.setTranslateY((maxY - minY) / 2 + minY);
		group.setTranslateX(minX);
		group.setTranslateZ(maxZ);
		return group;
	}

	private Group createYZPlane() {

		Group group = createPlane(lengthZ, lengthY);
		group.setTranslateX(minX);
		group.setRotationAxis(Rotate.Y_AXIS);
		group.setRotate(90);
		return group;
	}

	private Group createZLanels() {

		Group group = new Group();
		String nameAxis = data.getLabelAxisZ();
		Label name = new Label(nameAxis);
		Rotate yRotateAxis = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
		Rotate zRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Z_AXIS);
		name.getTransforms().addAll(zRotateAxis, yRotateAxis);
		name.setTranslateX(lableDistanceNameAxis);
		setLabelStyleAxis(name);
		group.getChildren().add(name);
		for(int i = (int)(minZ / lineSpacingScaled) + 1; i < (int)(maxZ / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * lineSpacing));
			setLabelStyle(label);
			label.setRotationAxis(Rotate.X_AXIS);
			label.setRotate(90);
			label.setTranslateZ(i * lineSpacingScaled);
			label.setTranslateX(lableDistance);
			Node tick = createGridLine(new Point3D(-tickLenght / 2, 0, i * lineSpacingScaled), new Point3D(tickLenght / 2, 0, i * lineSpacingScaled));
			group.getChildren().add(tick);
			group.getChildren().add(label);
		}
		group.setTranslateZ((maxZ - minZ) / 2 + minZ);
		group.setTranslateX(maxX);
		group.setTranslateY(minY);
		return group;
	}

	public double getLengthX() {

		return lengthX;
	}

	public double getLengthY() {

		return lengthY;
	}

	public double getLengthZ() {

		return lengthZ;
	}

	public double getMaxX() {

		return maxX;
	}

	public double getMaxY() {

		return maxY;
	}

	public double getMaxZ() {

		return maxZ;
	}

	public double getMinX() {

		return minX;
	}

	public double getMinY() {

		return minY;
	}

	public double getMinZ() {

		return minZ;
	}

	private void setLabelStyle(Label label) {

		label.setAlignment(Pos.CENTER);
		label.setFont(new Font("Arial", 35));
	}

	private void setLabelStyleAxis(Label label) {

		label.setFont(new Font("Arial", 50));
	}

	public void update() {

		double absMaximum = Arrays.stream(new double[]{data.getMinX(false), data.getMaxX(false), data.getMinY(false), data.getMaxY(false), data.getMinZ(false), data.getMaxZ(false)}).map(d -> Math.abs(d)).max().getAsDouble();
		double numberDigits = Math.floor(Math.log10(absMaximum));
		double round = Math.pow(10, numberDigits);
		lineSpacing = (((Math.round(absMaximum / round) * round) / maxNumberLine));
		lineSpacingScaled = lineSpacing * data.getScale();
		BiFunction<Double, Double, Double> getAbsMax = (min, max) -> {
			double absMax = (Math.abs(min) > Math.abs(max) ? Math.abs(min) : Math.abs(max));
			return Math.ceil(absMax / lineSpacingScaled) * lineSpacingScaled;
		};
		this.maxX = getAbsMax.apply(data.getMinX(true), data.getMaxX(true)) + lineSpacingScaled;
		this.maxY = getAbsMax.apply(data.getMinY(true), data.getMaxY(true)) + lineSpacingScaled;
		this.maxZ = getAbsMax.apply(data.getMinZ(true), data.getMaxZ(true)) + lineSpacingScaled;
		this.minX = -this.maxX;
		this.minY = -this.maxY;
		this.minZ = -this.maxZ;
		this.lengthX = Math.abs(this.maxX - this.minX);
		this.lengthY = Math.abs(this.maxY - this.minY);
		this.lengthZ = Math.abs(this.maxZ - this.minZ);
		mainGroup.getChildren().clear();
		mainGroup.getChildren().addAll(createXYPlane(), createYZPlane(), createXZPlane(), createCorns(), createXLabels(), createYLabels(), createZLanels());
	}
}
