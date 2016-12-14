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
package org.apache.uima.internal.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.uima.cas.impl.CopyOnWriteIndexPart;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.internal.util.OrderedFsSet_array;

/**
 * implements OrderedFsSet_array partially, for iterator use
 * Delegates generated by Eclipse
 */

public class CopyOnWriteOrderedFsSet_array implements NavigableSet<TOP>, CopyOnWriteIndexPart {
  
  private OrderedFsSet_array set;
  
  final public Comparator<TOP> comparatorWithoutID;
  
  
  public CopyOnWriteOrderedFsSet_array(OrderedFsSet_array original) {
    this.set = original;    
    this.comparatorWithoutID = original.comparatorWithoutID;
  }

  /**
   * Called by index when about to make an update
   */
  @Override
  public void makeCopy() {
    set = new OrderedFsSet_array(set);
  }

  /**
   * @see java.lang.Iterable#forEach(java.util.function.Consumer)
   */
  @Override
  public void forEach(Consumer<? super TOP> action) {
    set.forEach(action);
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return set.hashCode();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    return set.equals(obj);
  }

  /**
   * @see OrderedFsSet_array#comparator()
   */
  @Override
  public Comparator<? super TOP> comparator() {
    return set.comparator();
  }

  /**
   * @see OrderedFsSet_array#first()
   */
  @Override
  public TOP first() {
    return set.first();
  }

  /**
   * @see OrderedFsSet_array#last()
   */
  @Override
  public TOP last() {
    return set.last();
  }

  /**
   * @see OrderedFsSet_array#size()
   */
  @Override
  public int size() {
    return set.size();
  }

  /**
   * @see OrderedFsSet_array#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  /**
   * @see OrderedFsSet_array#contains(java.lang.Object)
   */
  @Override
  public boolean contains(Object o) {
    return set.contains(o);
  }

  /**
   * @see OrderedFsSet_array#toArray()
   */
  @Override
  public Object[] toArray() {
    return set.toArray();
  }

  /**
   * @see OrderedFsSet_array#toArray(java.lang.Object[])
   */
  @Override
  public <T> T[] toArray(T[] a1) {
    return set.toArray(a1);
  }

  /**
   * @see OrderedFsSet_array#add(TOP)
   */
  @Override
  public boolean add(TOP fs) {
    return set.add(fs);
  }

  /**
   * @see java.util.SortedSet#spliterator()
   */
  @Override
  public Spliterator<TOP> spliterator() {
    return set.spliterator();
  }

  /**
   * @see java.util.Collection#removeIf(java.util.function.Predicate)
   */
  @Override
  public boolean removeIf(Predicate<? super TOP> filter) {
    return set.removeIf(filter);
  }

  /**
   * @see java.util.Collection#stream()
   */
  @Override
  public Stream<TOP> stream() {
    return set.stream();
  }

  /**
   * @see java.util.Collection#parallelStream()
   */
  @Override
  public Stream<TOP> parallelStream() {
    return set.parallelStream();
  }

  /**
   * @see OrderedFsSet_array#remove(java.lang.Object)
   */
  @Override
  public boolean remove(Object o) {
    return set.remove(o);
  }

  /**
   * @see OrderedFsSet_array#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    return set.containsAll(c);
  }

  /**
   * @see OrderedFsSet_array#addAll(java.util.Collection)
   */
  @Override
  public boolean addAll(Collection<? extends TOP> c) {
    return set.addAll(c);
  }

  /**
   * @see OrderedFsSet_array#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll(Collection<?> c) {
    return set.retainAll(c);
  }

  /**
   * @see OrderedFsSet_array#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    return set.removeAll(c);
  }

  /**
   * 
   * @see OrderedFsSet_array#clear()
   */
  @Override
  public void clear() {
    set.clear();
  }

  /**
   * @see OrderedFsSet_array#lower(TOP)
   */
  @Override
  public TOP lower(TOP fs) {
    return set.lower(fs);
  }

  /**
   * @see OrderedFsSet_array#lowerPos(TOP)
   * @param fs the Feature Structure to use for positioning
   * @return -
   */
  public int lowerPos(TOP fs) {
    return set.lowerPos(fs);
  }

  /**
   * @see OrderedFsSet_array#floor(TOP)
   */
  @Override
  public TOP floor(TOP fs) {
    return set.floor(fs);
  }

  /**
   * @see OrderedFsSet_array#floorPos(TOP)
   * @param fs the Feature Structure to use for positioning
   * @return -
   */
  public int floorPos(TOP fs) {
    return set.floorPos(fs);
  }

  /**
   * @see OrderedFsSet_array#ceiling(TOP)
   */
  @Override
  public TOP ceiling(TOP fs) {
    return set.ceiling(fs);
  }

  /**
   * @see OrderedFsSet_array#ceilingPos(TOP)
   * @param fs the Feature Structure to use for positioning
   * @return -
   */
  public int ceilingPos(TOP fs) {
    return set.ceilingPos(fs);
  }

  /**
   * @see OrderedFsSet_array#higher(TOP)
   */
  @Override
  public TOP higher(TOP fs) {
    return set.higher(fs);
  }

  /**
   * @see OrderedFsSet_array#higherPos(TOP)  
   * @param fs the Feature Structure to use for positioning
   * @return -
   */
  public int higherPos(TOP fs) {
    return set.higherPos(fs);
  }

  /**
   * @see OrderedFsSet_array#pollFirst()
   */
  @Override
  public TOP pollFirst() {
    return set.pollFirst();
  }

  /**
   * @see OrderedFsSet_array#pollLast()
   */
  @Override
  public TOP pollLast() {
    return set.pollLast();
  }

  /**
   * @see OrderedFsSet_array#iterator()
   */
  @Override
  public Iterator<TOP> iterator() {
    return set.iterator();
  }

  /**
   * @see OrderedFsSet_array#descendingSet()
   */
  @Override
  public NavigableSet<TOP> descendingSet() {
    return set.descendingSet();
  }

  /**
   * @see OrderedFsSet_array#descendingIterator()
   */
  @Override
  public Iterator<TOP> descendingIterator() {
    return set.descendingIterator();
  }

  /**
   * @see OrderedFsSet_array#subSet(TOP, boolean, TOP, boolean)
   */
  @Override
  public NavigableSet<TOP> subSet(TOP fromElement, boolean fromInclusive, TOP toElement,
      boolean toInclusive) {
    return set.subSet(fromElement, fromInclusive, toElement, toInclusive);
  }

  /**
   * @see OrderedFsSet_array#headSet(TOP, boolean)
   */
  @Override
  public NavigableSet<TOP> headSet(TOP toElement, boolean inclusive) {
    return set.headSet(toElement, inclusive);
  }

  /**
   * @see OrderedFsSet_array#tailSet(TOP, boolean)
   */
  @Override
  public NavigableSet<TOP> tailSet(TOP fromElement, boolean inclusive) {
    return set.tailSet(fromElement, inclusive);
  }

  /**
   * @see OrderedFsSet_array#subSet(TOP, TOP)
   */
  @Override
  public SortedSet<TOP> subSet(TOP fromElement, TOP toElement) {
    return set.subSet(fromElement, toElement);
  }

  /**
   * @see OrderedFsSet_array#headSet(TOP)
   */
  @Override
  public SortedSet<TOP> headSet(TOP toElement) {
    return set.headSet(toElement);
  }

  /**
   * @see OrderedFsSet_array#tailSet(TOP)
   */
  @Override
  public SortedSet<TOP> tailSet(TOP fromElement) {
    return set.tailSet(fromElement);
  }

  /**
   * @return the modification count
   */
  public int getModificationCount() {
    return set.getModificationCount();
  }

  /**
   * @see OrderedFsSet_array#toString()
   */
  @Override
  public String toString() {
    return set.toString();
  }


}