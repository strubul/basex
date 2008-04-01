package org.basex.query.xpath.expr;

import org.basex.data.Serializer;
import org.basex.query.QueryException;
import org.basex.query.xpath.XPContext;
import org.basex.query.xpath.values.NodeSet;
import org.basex.query.xpath.values.Item;

/**
 * Path Expression.
 * This Expression represents a relative location path operating
 * on a nodeset (return value of expression).
 * 
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Tim Petrowsky
 */
public final class Path extends DualExpr {
  /**
   * Constructor for a relative location path.
   * @param e Expression evaluating to a nodeset
   * @param p Location Path (or maybe other Expression after optimization)
   */
  public Path(final Expr e, final Expr p) {
    super(e, p);
  }

  @Override
  public NodeSet eval(final XPContext ctx) throws QueryException {
    final Item val = ctx.eval(expr1);
    int[][] ftprepos = null;
    int[] ftpoin = null;
    if(val instanceof NodeSet) {
      NodeSet ns = (NodeSet) val;
      ctx.local = ns;
      ftprepos =  ns.ftidpos;
      ftpoin = ns.ftpointer;
    }
    // <CG> hack, maybe this causes errors?? SG
    ctx.local = (NodeSet) ctx.eval(expr2);
    ctx.local.ftidpos = ftprepos;
    ctx.local.ftpointer = ftpoin;
    return ctx.local;
  }

  @Override
  public Expr compile(final XPContext ctx) throws QueryException {
    expr1 = expr1.compile(ctx);
    expr2 = expr2.compile(ctx);
    return this;
  }

  @Override
  public void plan(final Serializer ser) throws Exception {
    ser.openElement(this);
    expr1.plan(ser);
    expr2.plan(ser);
    ser.closeElement(this);
  }

  @Override
  public String color() {
    return "FF9900";
  }

  @Override
  public String toString() {
    return "Path(" + expr1 + ", " + expr2 + ')';
  }
}
