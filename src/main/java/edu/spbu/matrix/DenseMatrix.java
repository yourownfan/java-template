package edu.spbu.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class DenseMatrix implements Matrix {

  int height, width;
  double[][] matrix;

  public DenseMatrix(double[][] matrix) {
    this.height = matrix.length;
    this.width = matrix[0].length;
    this.matrix = matrix;
  } //билдер класса с помощью двумерного массива

  public DenseMatrix(String fileName) throws RuntimeException {
    try {
      ArrayList<double[]> rows = new ArrayList<>();
      Scanner fileData = new Scanner(new File(fileName));

      if (fileData.hasNextLine()) {
        String[] stringRow = fileData.nextLine().split("\\s+");
        double[] doubleRow = new double[stringRow.length];
        for (int i = 0; i < stringRow.length; i++) {
          doubleRow[i] = Double.parseDouble(stringRow[i]);
        }
        rows.add(doubleRow);
        int fileWidth = doubleRow.length;

        while (fileData.hasNextLine()) {
          stringRow = fileData.nextLine().split("\\s+");

          if (stringRow.length != fileWidth) {
            throw new RuntimeException("Невозможно загрузить матрицу: строки имеют разную длину");
          }

          doubleRow = new double[stringRow.length];
          for (int i = 0; i < stringRow.length; i++) {
            doubleRow[i] = Double.parseDouble(stringRow[i]);
          }
          rows.add(doubleRow);
        }

        int fileHeight = rows.size();
        double[][] fileMatrix = new double[fileHeight][fileWidth];
        for (int i = 0; i < fileHeight; i++) {
          fileMatrix[i] = rows.get(i);
        }

        this.height = fileHeight;
        this.width = fileWidth;
        this.matrix = fileMatrix;
      } else {
        System.err.println("Невозможно загрузить матрицу: файл пуст");
      }
    } catch (FileNotFoundException e) {
      System.err.println("Невозможно загрузить матрицу: файл не найден");
    }
  } // билдер класса с помощью матрицы из файла

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        sb.append(this.matrix[i][j]).append(' ');
      }
      sb.append('\n');
    }
    return sb.toString();
  }// переводим матрицу в строку для удобного вывода

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public Matrix transpose(){
    double[][] newMatrix = new double[this.width][this.height];
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        newMatrix[j][i] = this.matrix[i][j];
      }
    }
    return new DenseMatrix(newMatrix);
  }// транспонирование

  @Override
  public Matrix mul(Matrix o) {
    if (this.width != o.getHeight()) {
      System.out.println("Операция невозможна: у матриц нет подходящих размеров");
    } else if (o instanceof DenseMatrix) {
      return this.mul((DenseMatrix) o);
    } else if (o instanceof SparseMatrix) {
      return this.mul((SparseMatrix) o);
    }
    return null;
  }// распределине по методам для умножения

  private Matrix mul(DenseMatrix o) {
    int newHeight = this.height, newWidth = o.getWidth();
    double[][] newMatrix = new double[newHeight][newWidth];

    for (int i = 0; i < newHeight; i++) {
      for (int j = 0; j < newWidth; j++) {
        for (int k = 0; k < this.width; k++) {
          newMatrix[i][j] += this.matrix[i][k] * o.matrix[k][j];
        }
      }
    }
    return new DenseMatrix(newMatrix);
  }// перемножение dense на dense

  private Matrix mul(SparseMatrix o) {
    return o.transpose().mul(this.transpose()).transpose();
  }// транспонруем для перехода в sparse на dense который описн в классе sparse


  // многопоточное умножение матриц


  @Override
  public Matrix dmul(Matrix o) {
    if (this.width != o.getHeight()) {
      System.out.println("Операция невозможна: у матриц нет подходящих размеров");
    } else if (o instanceof DenseMatrix) {
      return this.dmul((DenseMatrix) o);
    } else if (o instanceof SparseMatrix) {
      return this.dmul((SparseMatrix) o);
    }
    return null;
  }

  private Matrix dmul(DenseMatrix o) {
    final DenseMatrix matrixF = new DenseMatrix(this.matrix);
    final DenseMatrix matrixS = new DenseMatrix(o.matrix);
    int newHeight = this.height, newWidth = o.getWidth();
    final double[][] newMatrix = new double[newHeight][newWidth];

    class DDMulThreaded implements Runnable {
      final int subHeightF, subHeightS, subWidthF, subWidthS;

      public DDMulThreaded(int subHeightF, int subHeightS, int subWidthF, int subWidthS) {
        this.subHeightF = subHeightF;
        this.subHeightS = subHeightS;
        this.subWidthF = subWidthF;
        this.subWidthS = subWidthS;
      }//билдер подкласса

      @Override
      public void run(){
        for (int i = subHeightF; i < subHeightS; i++) {
          for (int j = subWidthF; j < subWidthS; j++) {
            for (int k = 0; k < matrixF.width; k++) {
              newMatrix[i][j] += matrixF.matrix[i][k] * matrixS.matrix[k][j];
            }
          }
        }
      }//умножение подматриц
    }
    int subHeight = newHeight / 2, subWidth = newWidth / 2;
    try {
      Thread threadFirst = new Thread(new DDMulThreaded(0, subHeight, 0, subWidth));
      Thread threadSecond = new Thread(new DDMulThreaded(0, subHeight, subWidth, newWidth));
      Thread threadThird = new Thread(new DDMulThreaded(subHeight, newHeight, 0, subWidth));
      Thread threadFourth = new Thread(new DDMulThreaded(subHeight, newHeight, subWidth, newWidth));

      threadFirst.start(); threadSecond.start(); threadThird.start(); threadFourth.start();
      threadFirst.join(); threadSecond.join(); threadThird.join(); threadFourth.join();

    } catch (Exception e){
      e.printStackTrace();
    }
    return new DenseMatrix(newMatrix);
  }

  private Matrix dmul(SparseMatrix o){
    return o.transpose().dmul(this.transpose()).transpose();
  }

  @Override public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof DenseMatrix) {
      DenseMatrix dm = (DenseMatrix) o;

      if (this.height != dm.height || this.width != dm.width) {
        return false;
      }

      for (int i = 0; i < dm.height; i++) {
        for (int j = 0; j < dm.width; j++) {
          if (this.matrix[i][j] != dm.matrix[i][j]) {
            return false;
          }
        }
      }
      return true;
    }

    if (o instanceof SparseMatrix) {
      SparseMatrix sm = (SparseMatrix) o;

      if (this.height != sm.height || this.width != sm.width) {
        return false;
      }
      for (int i = 0; i < sm.height; i++) {
        for (int j = 0; j < sm.width; j++) {
          if (this.matrix[i][j] != sm.matrix[i][j]) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }
}



