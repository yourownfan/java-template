package edu.spbu.matrix;

/**
 * Matrix is a mathematical model usually represented by a 2D array of
 * floating point numbers with some operations defined. This interface requires
 * a minimal set of these operations.
 */

public interface Matrix {

  /**
   * Method returns this matrix like a string
   * @return string consisting of matrix element
   */
  String toString();

  /**
   * Returns height of a matrix
   * @return height
   */
  int getHeight();

  /**
   * Returns width of a matrix
   * @return width
   */
  int getWidth();

  /**
   * Transpose the matrix and return a copy
   * @return a copy of M^T
   */
  Matrix transpose();

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   * @param o - another matrix to multiply by (this * o)
   * @return resulting matrix of the multiplication
   */
  Matrix mul(Matrix o);

  /**
   * многопоточное умножение матриц
   * @param o - another matrix to multiply by (this * o)
   * @return resulting matrix of the multiplication
   */
  Matrix dmul(Matrix o);

  /**
   * Check if matrix are equals
   * @return boolean value
   */
  boolean equals(Object o);
}


































//package edu.spbu.matrix;

/**
 *
 */
//public interface Matrix
//{
  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   * @param o
   * @return
   */
 // Matrix mul(Matrix o);

  /**
   * многопоточное умножение матриц
   * @param o
   * @return
   */
  //Matrix dmul(Matrix o);

//}
