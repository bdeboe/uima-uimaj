/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.uima.cas.text;

public final class AnnotationPredicates_WW implements AnnotationPredicates {
  @Override
  public boolean coveredBy(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aYBegin <= aXBegin && aXEnd <= aYEnd;
  }

  @Override
  public boolean coveredBy(AnnotationFS aX, int aYBegin, int aYEnd) {
    return aYBegin <= aX.getBegin() && aX.getEnd() <= aYEnd;
  }

  /**
   * Y is starting before or at the same position as A and ends after or at the same position as X.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X is covered by Y.
   */
  @Override
  public boolean coveredBy(AnnotationFS aX, AnnotationFS aY) {
    return aY.getBegin() <= aX.getBegin() && aX.getEnd() <= aY.getEnd();
  }

  @Override
  public boolean covers(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aXBegin <= aYBegin && aYEnd <= aXEnd;
  }

  @Override
  public boolean covers(AnnotationFS aX, int aYBegin, int aYEnd) {
    return aX.getBegin() <= aYBegin && aYEnd <= aX.getEnd();
  }

  /**
   * X is starting before or at the same position as Y and ends after or at the same position as Y.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X is covering Y.
   */
  @Override
  public boolean covers(AnnotationFS aX, AnnotationFS aY) {
    return aX.getBegin() <= aY.getBegin() && aY.getEnd() <= aX.getEnd();
  }

  @Override
  public boolean colocated(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aXBegin == aYBegin && aXEnd == aYEnd;
  }

  @Override
  public boolean colocated(AnnotationFS aX, int aYBegin, int aYEnd) {
    return aX.getBegin() == aYBegin && aX.getEnd() == aYEnd;
  }

  /**
   * X starts and ends at the same position as Y.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X is at the same location as Y.
   */
  @Override
  public boolean colocated(AnnotationFS aX, AnnotationFS aY) {
    return aX.getBegin() == aY.getBegin() && aX.getEnd() == aY.getEnd();
  }

  @Override
  public boolean overlaps(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aXBegin <= aYEnd && aYBegin <= aXEnd;
  }

  @Override
  public boolean overlaps(AnnotationFS aX, int aYBegin, int aYEnd) {
    int xBegin = aX.getBegin();
    return xBegin <= aYEnd && aYBegin <= aX.getEnd();
  }

  /**
   * The intersection of the spans X and Y is non-empty. If either X or Y have a zero-width, then
   * the intersection is considered to be non-empty if the begin of X is either within Y or the same
   * as the begin of Y - and vice versa.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X overlaps with Y in any way.
   */
  @Override
  public boolean overlaps(AnnotationFS aX, AnnotationFS aY) {
    int xBegin = aX.getBegin();
    int yBegin = aY.getBegin();
    return xBegin <= aY.getEnd() && yBegin <= aX.getEnd();
  }

  @Override
  public boolean overlapsLeft(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aXBegin <= aYBegin && aYBegin <= aXEnd && aXEnd <= aYEnd;
  }

  @Override
  public boolean overlapsLeft(AnnotationFS aX, int aYBegin, int aYEnd) {
    int xEnd = aX.getEnd();
    return aYBegin <= xEnd && xEnd <= aYEnd && aX.getBegin() <= aYBegin;
  }

  /**
   * X is starting before or at the same position as Y and ends before Y ends.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X overlaps Y on the left.
   */
  @Override
  public boolean overlapsLeft(AnnotationFS aX, AnnotationFS aY) {
    int xEnd = aX.getEnd();
    int yBegin = aY.getBegin();
    return yBegin <= xEnd && xEnd <= aY.getEnd() && aX.getBegin() <= yBegin;
  }

  @Override
  public boolean overlapsRight(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aYBegin <= aXBegin && aXBegin <= aYEnd && aYEnd <= aXEnd;
  }

  @Override
  public boolean overlapsRight(AnnotationFS aX, int aYBegin, int aYEnd) {
    int xBegin = aX.getBegin();
    return aYBegin <= xBegin && xBegin <= aYEnd && aYEnd <= aX.getEnd();
  }

  /**
   * X is starting after Y starts and ends after or at the same position as Y.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X overlaps Y on the right.
   */
  @Override
  public boolean overlapsRight(AnnotationFS aX, AnnotationFS aY) {
    int xBegin = aX.getBegin();
    int yEnd = aY.getEnd();
    return aY.getBegin() <= xBegin && xBegin <= yEnd && yEnd <= aX.getEnd();
  }

  @Override
  public boolean rightOf(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aXBegin >= aYEnd;
  }

  @Override
  public boolean rightOf(AnnotationFS aX, int aYBegin, int aYEnd) {
    return aX.getBegin() >= aYEnd;
  }

  /**
   * X starts at or after the position that Y ends.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X is right of Y.
   */
  @Override
  public boolean rightOf(AnnotationFS aX, AnnotationFS aY) {
    int xBegin = aX.getBegin();
    return xBegin >= aY.getEnd();
  }

  @Override
  public boolean leftOf(int aXBegin, int aXEnd, int aYBegin, int aYEnd) {
    return aYBegin >= aXEnd;
  }

  @Override
  public boolean leftOf(AnnotationFS aX, int aYBegin, int aYEnd) {
    return aYBegin >= aX.getEnd();
  }

  /**
   * X ends before or at the position that Y starts.
   * 
   * @param aX
   *          X
   * @param aY
   *          Y
   * @return whether X left of Y.
   */
  @Override
  public boolean leftOf(AnnotationFS aX, AnnotationFS aY) {
    return aY.getBegin() >= aX.getEnd();
  }
}
