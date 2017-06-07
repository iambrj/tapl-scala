package tapl.fullrecon

import scala.io.Source

object FullReconDemo extends App {
  import Evaluator._
  import Typer._
  import util.Print._
  import PrettyPrinter._

  val width = 60

  def processCommand(in: (Context, UVarGenerator, IdConstr), cmd: Command): (Context, UVarGenerator, IdConstr) = in match {
    case (ctx, nextuvar, constr) => cmd match {
      case Eval(t1) =>

        val (tyT, nextuvar1, constrT) = recon(ctx, nextuvar, t1)
        val constr11 = constr ++ constrT
        val constr12 = unify(ctx, "Could not simplify constraints", constr11)
        
        val ty1 = applySub(constr12, tyT)
        val doc1 = g2(ptmATerm(true, ctx, t1) :: ":" :/: ptyTy(ctx, ty1) :: ";")

        val t2 = eval(ctx, t1)
        val (tyT2, nextuvar2, constrT2) = recon(ctx, nextuvar, t1)
        val constr21 = constr ++ constrT2
        val constr22 = unify(ctx, "Could not simplify constraints", constr21)
        val ty2 = applySub(constr22, tyT2)
        val doc2 = g2(ptmATerm(true, ctx, t2) :: ":" :/: ptyTy(ctx, ty1) :: ";")

        println("====================")
        println(print(doc1, width))
        println("""||""")
        println("""\/""")
        println(print(doc2, width))
        (ctx, nextuvar1, constr12)
      case Bind(x, bind) =>
        val doc1 = x :: pBindingTy(ctx, bind) :: ";"
        println("====================")
        println(print(doc1, width))
        (ctx.addBinding(x, bind), nextuvar, constr)
    }
  }

  def demo(s: String): Unit = {
    val (commands, _) = FullReconParsers.input(s)(Context())
    commands.foldLeft((Context(), uvargen, emptyIdConstr))(processCommand)
  }

  val inFile = if (args.isEmpty) "examples/fullrecon.tapl" else args(0)
  val input = Source.fromFile(inFile).mkString("")

  demo(input)

}