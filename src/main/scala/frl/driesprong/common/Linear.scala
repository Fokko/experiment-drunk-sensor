package frl.driesprong.common

import breeze.linalg.{DenseMatrix, DenseVector, inv}

/**
  * Created by Fokko Driesprong on 15/07/2017.
  */
object Linear {

  // Fire up those CPU's, YOLO, N(D^3)
  def Regression(y: DenseVector[Double], x: DenseMatrix[Double]): DenseVector[Double] = inv(x.t * x) * x.t * y

}
