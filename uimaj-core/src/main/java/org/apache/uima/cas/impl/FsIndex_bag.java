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

package org.apache.uima.cas.impl;

import java.util.List;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.admin.FSIndexComparator;
import org.apache.uima.internal.util.ObjHashSet;
import org.apache.uima.jcas.cas.TOP;

/**
 * Used for UIMA FS Bag Indexes
 * Uses ObjHashSet to hold instances of FeatureStructures
 * @param <T> the Java cover class type for this index, passed along to (wrapped) iterators producing Java cover classes
 * NOTE: V3 doesn't support ALLOW_DUP_ADD_TO_INDEXES
 */
public class FsIndex_bag<T extends TOP> extends FsIndex_singletype<T> {
  
//  // package private
//  final static boolean USE_POSITIVE_INT_SET = !FSIndexRepositoryImpl.IS_ALLOW_DUP_ADD_2_INDEXES;

  // The index
  final private ObjHashSet<TOP> index;
  
  FsIndex_bag(CASImpl cas, Type type, int initialSize, int indexType) {
    super(cas, type, indexType);
    this.index = new ObjHashSet<TOP>(initialSize, TOP.class);
  }

  /**
   * Substitutes an empty comparator if one is specified - may not be needed
   * @see org.apache.uima.cas.impl.FSLeafIndexImpl#init(org.apache.uima.cas.admin.FSIndexComparator)
   */
  boolean init(FSIndexComparator comp) {
    // The comparator for a bag index must be empty, except for the type. If
    // it isn't, we create an empty one.
    FSIndexComparator newComp;
    if (comp.getNumberOfKeys() > 0) {
      newComp = new FSIndexComparatorImpl(casImpl);
      newComp.setType(comp.getType());
    } else {
      newComp = comp;
    }
    return super.init(newComp);
  }

  public void flush() {
    index.clear();
  }

  @Override
  public final boolean insert(T fs) {
    return index.add((TOP) fs);
  }

  @SuppressWarnings("unchecked")
  public final boolean insert(int fs) {
    return insert((T) casImpl.getFsFromId_checked(fs)); 
  }
    
  /**
   * Override the super impl which uses comparators.
   * For bag indexes, compare equal only if identical addresses
   */
  @Override
  public int compare(TOP fs1, TOP fs2) {
    return (fs1 == fs2) ? 0 : (fs1.id() < fs2.id()) ? -1 : 1;
  }

  /*
   * // Do binary search on index. 
   * private final int binarySearch(int [] array, int ele, int start, int end) { 
   *   --end;  // Make end a legal value. 
   *   int i; // Current position 
   *   int comp; // Compare value 
   *   while (start <= end) { 
   *     i = (start + end) / 2; 
   *     comp = compare(ele, array[i]); 
   *     if (comp ==  0) { 
   *       return i; 
   *     } 
   *     if (start == end) {
   *       if (comp < 0) {
   *         return (-i)-1;
   *       } else { // comp > 0 
   *         return (-i)-2; // (-(i+1))-1
   *       } 
   *     } 
   *     if (comp < 0) {
   *       end = i-1; 
   *     } else { // comp > 0 
   *       start = i+1;
   *     } 
   *   } //This means that the input span is empty. 
   *   return (-start)-1; 
   * }
   */

//  public FsIterator_bag<T>SIteratorSingleType_ImplBase<T> createFSIterator(int[] detectIllegalIndexUpdates, int typeCode) {
//    return new FsIterator_bag<T>(this, detectIllegalIndexUpdates, typeCode);
//  }

  /**
   * @see org.apache.uima.cas.FSIndex#contains(FeatureStructure)
   * @param fs A Feature Structure used a template to match for equality with the
   *           FSs in the index.
   * @return <code>true</code> if the index contains such an element.
   */
  public boolean contains(FeatureStructure fs) {
    return this.index.contains(fs);
  }
  
  public boolean containsEq(FeatureStructureImplC fs) {
    return index.contains(fs);
  }
  
  boolean ll_contains(int fsAddr) {
    return contains(casImpl.getFsFromId_checked(fsAddr));
  }

  /**
   * This is a silly method for bag indexes in V3, since dupl add to indexes is not allowed.
   * @param fs
   * @return null or the original fs if the fs is in the index
   */
  @Override
  public T find(FeatureStructure fs) {
    final int resultAddr =  this.index.find((TOP) fs);
    if (resultAddr >= 0) {
      return (T) fs;
    }
    // Not found.
    return null;
  }

  /**
   * @see org.apache.uima.cas.FSIndex#size()
   */
  public int size() {
    return this.index.size();
  }

  /**
   * only for backwards compatibility
   * 
   */
  @Override
  public boolean deleteFS(T fs) {
    return this.index.remove(fs);
  }
  
  @Override
  public boolean remove(int fsRef) {    
    return deleteFS(casImpl.getFsFromId_checked(fsRef));
  }

  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void bulkAddTo(List<TOP> fss) {
    fss.addAll(this.index);
  }
  
  /* (non-Javadoc)
   * @see org.apache.uima.cas.FSIndex#iterator()
   */
  @Override
  public FSIterator<T> iterator() {
    return new FsIterator_bag<T>(this, getDetectIllegalIndexUpdates(), getTypeCode());
  }

  /*
   * Iterator support 
   */
  boolean isValid(int itPos) {
    return index.isValid(itPos);
  }
  
  int moveToFirst() {
    return index.moveToFirst();
  }

  int moveToLast() {
    return index.moveToLast();
  }
  
  int moveToNext(int itPos) {
     return index.moveToNext(itPos);
  }
  
  int moveToPrevious(int itPos) {
    return index.moveToPrevious(itPos);
  }
  
  T get(int itPos) {
    return (T) index.get(itPos);
  }
  
  ObjHashSet<TOP> getObjHashSet() {
    return index;
  }
  
  

}