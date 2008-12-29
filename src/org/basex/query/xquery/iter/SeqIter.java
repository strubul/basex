package org.basex.query.xquery.iter;

import java.io.IOException;
import org.basex.data.Result;
import org.basex.data.Serializer;
import org.basex.query.xquery.XQContext;
import org.basex.query.xquery.XQException;
import org.basex.query.xquery.item.Item;
import org.basex.query.xquery.item.Seq;

/**
 * Sequence Iterator.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
public final class SeqIter extends ResetIter implements Result {
  /** Query context. */
  private XQContext ctx;
  /** Items. */
  public Item[] item;
  /** Size. */
  public int size;
  /** Iterator. */
  public int pos = -1;

  /**
   * Constructor.
   */
  public SeqIter() {
    item = new Item[1];
  }

  /**
   * Constructor, specifying the query context.
   * @param c query context
   */
  public SeqIter(final XQContext c) {
    this();
    ctx = c;
  }

  /**
   * Constructor.
   * @param iter iterator
   * @throws XQException evaluation exception
   */
  public SeqIter(final Iter iter) throws XQException {
    item = new Item[1];
    add(iter);
  }

  /**
   * Constructor.
   * @param it item array
   * @param s size
   */
  private SeqIter(final Item[] it, final int s) {
    item = it;
    size = s;
  }

  /**
   * Constructor.
   * @param it item array
   * @param s size
   * @return iterator
   */
  public static Iter get(final Item[] it, final int s) {
    return s == 0 ? Iter.EMPTY : new SeqIter(it, s);
  }

  /**
   * Adds the contents of an iterator.
   * @param iter entry to be added
   * @throws XQException evaluation exception
   */
  public void add(final Iter iter) throws XQException {
    Item i;
    while((i = iter.next()) != null) add(i);
  }

  /**
   * Adds a single item.
   * @param it item to be added
   */
  public void add(final Item it) {
    if(size == item.length) resize();
    item[size++] = it;
  }

  /**
   * Resizes the sequence array.
   */
  private void resize() {
    final Item[] tmp = new Item[size << 1];
    System.arraycopy(item, 0, tmp, 0, size);
    item = tmp;
  }

  public boolean same(final Result v) {
    if(!(v instanceof SeqIter)) return false;

    final SeqIter sb = (SeqIter) v;
    if(size != sb.size) return false;
    try {
      for(int i = 0; i < size; i++) {
        if(item[i].type != sb.item[i].type ||
          !item[i].eq(sb.item[i])) return false;
      }
      return true;
    } catch(final XQException e) {
      return false;
    }
  }

  public void serialize(final Serializer ser) throws IOException {
    for(int c = 0; c < size && !ser.finished(); c++) serialize(ser, c);
  }

  public void serialize(final Serializer ser, final int n) throws IOException {
    ser.openResult();
    ctx.serialize(ser, item[n]);
    ser.closeResult();
  }
  
  @Override
  public Item next() {
    return ++pos < size ? item[pos] : null;
  }

  @Override
  public void reset() {
    pos = -1;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public Item finish() {
    return Seq.get(item, size);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("(");
    for(int v = 0; v != size; v++) {
      sb.append((v != 0 ? ", " : "") + item[v]);
      if(sb.length() > 15 && v + 1 != size) {
        sb.append(", ...");
        break;
      }
    }
    return sb.append(")").toString();
  }
}
