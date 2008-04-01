package org.basex.util;

/**
 * This is a simple container for native boolean values.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
public final class ByteList {
  /** Value array. */
  public byte[][] list = new byte[8][];
  /** Current array size. */
  public int size;

  /**
   * Default constructor.
   */
  public ByteList() { }

  /**
   * Adds next value.
   * @param v value to be added
   */
  public void add(final byte[] v) {
    if(size == list.length) list = Array.extend(list);
    list[size++] = v;
  }

  /**
   * Finishes the int array.
   * @return int array
   */
  public byte[][] finish() {
    return size == list.length ? list : Array.finish(list, size);
  }

  /**
   * Resets the integer list.
   */
  public void reset() {
    size = 0;
  }
}
