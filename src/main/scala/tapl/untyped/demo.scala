package tapl.untyped

import scala.io.Source

object UntypedDemo extends App {
  import Evaluator._
  import util.Print._
  import PrettyPrinter._
  import util.Document._

  val width = 60

  def processCommand(ctx: Context, cmd: Command): Context = cmd match {
    case Eval(t1) =>
      val doc1 = g2(ptmATerm(true, ctx, t1) :: ";")
      val t2 = eval(ctx, t1)
      val doc2 = g2(ptmATerm(true, ctx, t2) :: ";")

      println("====================")
      println(print(doc1, width))
      println("""||""")
      println("""\/""")
      println(print(doc2, width))

      ctx

    case Bind(n, b) =>
      ctx.addBinding(n, b)
  }

  def demo(s: String): Unit = {
    val (commands, _) = UntypedParsers.input(s)(Context())
    commands.foldLeft(Context())(processCommand)
  }
  
  val inFile = if (args.isEmpty) "examples/untyped.tapl" else args(0)
  val input = Source.fromFile(inFile).mkString("")

  demo(input)
}