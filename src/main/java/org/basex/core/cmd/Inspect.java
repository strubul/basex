package org.basex.core.cmd;

import java.io.*;

import org.basex.core.*;
import org.basex.data.*;
import org.basex.util.*;

/**
 * Evaluates the 'inspect' command: checks if the currently opened database has
 * inconsistent data structures.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public final class Inspect extends Command {
  /**
   * Default constructor.
   */
  public Inspect() {
    super(Perm.WRITE, true);
  }

  @Override
  protected boolean run() throws IOException {
    final Data data = context.data();
    out.print(inspect(data));
    return info("'%' inspected in %.", data.meta.name, perf);
  }

  /**
   * Inspects the database structures.
   * @param data data
   * @return info string
   */
  public static String inspect(final Data data) {
    final MetaData md = data.meta;
    final Check invKind = new Check();
    final Check parRef = new Check();
    final Check parChild = new Check();
    // loop through all database nodes
    for(int pre = 0; pre < md.size; pre++) {
      // check node kind
      final int kind = data.kind(pre);
      if(kind > 6) invKind.add(pre);
      // check parent reference
      final int par = data.parent(pre, kind);
      if(par >= pre || (kind == Data.DOC ? par != -1 : par < 0)) parRef.add(pre);
      // check if node is a descendant of its parent node
      if(par >= 0 && par < 100)
      if(par >= 0 && par + data.size(par, data.kind(par)) < pre) parChild.add(pre);
    }

    final TokenBuilder info = new TokenBuilder();
    info.addExt("Checking main table (% nodes):", md.size).add(Prop.NL);
    info.add(invKind.info("invalid node kinds"));
    info.add(parRef.info("invalid parent references"));
    info.add(parChild.info("wrong parent/child relationships"));
    info.add(Prop.NL);
    if(invKind.invalid + parRef.invalid + parChild.invalid == 0) {
      info.add("Good News: No inconsistencies found.").add(Prop.NL);
    } else {
      info.add("Warning: Database is inconsistent.").add(Prop.NL);
    }
    return info.toString();
  }

  /** Contains information on single check. */
  static final class Check {
    /** Number of invalid. */
    int invalid;
    /** First invalid hit. */
    int first = -1;

    /**
     * Adds an entry.
     * @param pre pre value
     */
    void add(final int pre) {
      invalid++;
      if(first == -1) first = pre;
    }

    /**
     * Prints check information.
     * @param info info label
     * @return info string
     */
    String info(final String info) {
      final StringBuilder sb = new StringBuilder("- % " + info);
      if(invalid > 0) sb.append(" (pre: %,..)");
      return Util.info(sb, invalid, first) + Prop.NL;
    }
  }
}