package frl.driesprong

import breeze.linalg.{DenseMatrix, DenseVector}
import frl.driesprong.common.Linear
import org.scalactic.TolerantNumerics
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Fokko Driesprong on 15/07/2017.
  */
class LinearRegression extends FlatSpec with Matchers {

  val epsilon = 1e-4f
  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(epsilon)

  "The linear regression" should "be able to get the right slope/intercept" in {

    val N = 10

    val y = DenseVector((0 until N).map(_.toDouble + 22.0).toArray)
    val x = DenseVector((0 until N).map(_.toDouble * 2.0).toArray)

    val X = DenseMatrix.ones[Double](N, 2)
    X(::, 1) := x

    val w = Linear.Regression(y, X)

    assert(w(0) === 22.0)
    assert(w(1) === 0.5)
  }

}
