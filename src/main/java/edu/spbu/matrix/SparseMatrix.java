package edu.spbu.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SparseMatrix implements Matrix {
  int height, width;
  double[] elements;
  int[] numElInRow, columnIndex;
  double[][] matrix;

  public SparseMatrix(int height, int width, double[] elements, int[] numElInRow, int[] columnIndex) {
    this.height = height;
    this.width = width;
    this.elements = elements;
    this.numElInRow = numElInRow;
    this.columnIndex = columnIndex;

    double[][] matrix = new double[this.height][this.width];
    for (int i = 0; i < this.height; i++) {
      Arrays.fill(matrix[i], 0);
    }

    for (int i = 0; i < this.numElInRow.length - 1; i++) {
      int startPoint = this.numElInRow[i], endPoint = this.numElInRow[i + 1];
      for (int j = startPoint; j < endPoint; j++){
        matrix[i][this.columnIndex[j]] = this.elements[j];
      }
    }
    this.matrix = matrix;
  } //билдер класса с  помощью представления ненулевых элементов

  public SparseMatrix(double[][] matrix) {
    this.height = matrix.length;
    this.width = matrix[0].length;
    this.matrix = matrix;

    ArrayList<Double> elements = new ArrayList<>();
    ArrayList<Integer> numElInRow = new ArrayList<>();
    ArrayList<Integer> columnIndex = new ArrayList<>();

    int count = 0;
    numElInRow.add(count);
    for (int i = 0; i < this.height; i++){
      for (int j = 0; j < this.width; j++){
        if (this.matrix[i][j] != 0){
          elements.add(matrix[i][j]);
          columnIndex.add(j);
          count++;
        }
      }
      numElInRow.add(count);// поиск ненулевых элементов в строке, их запись
    }
    double[] newElements = new double[elements.size()];
    for (int i = 0; i < elements.size(); i++) {
      newElements[i] = elements.get(i);
    }
    this.elements = newElements;

    int[] newNumElInRow = new int[numElInRow.size()];
    for (int i = 0; i < numElInRow.size(); i++) {
      newNumElInRow[i] = numElInRow.get(i);
    }
    this.numElInRow = newNumElInRow;

    int[] newColumnIndex = new int[columnIndex.size()];
    for (int i = 0; i < columnIndex.size(); i++) {
      newColumnIndex[i] = columnIndex.get(i);
    }
    this.columnIndex = newColumnIndex;
  }// билдер класса с помощью двумерного массива

  public SparseMatrix(String fileName) {
    try {

      ArrayList<Double> fileElements = new ArrayList<>();
      ArrayList<Integer> fileNumElInRow = new ArrayList<>();
      ArrayList<Integer> fileColumnIndex = new ArrayList<>();
      Scanner fileData = new Scanner(new File(fileName));

      if (fileData.hasNextLine()) {
        String[] stringRow = fileData.nextLine().split("\\s+");
        int fileWidth = stringRow.length;
        int fileHeight = 0;
        int countNZ = 0;
        fileNumElInRow.add(0);
        for (int i = 0; i < fileWidth; i++) {
          if (!stringRow[i].equals("0")) {
            fileElements.add(Double.parseDouble(stringRow[i]));
            fileColumnIndex.add(i);
            countNZ++;
          }
        }
        fileNumElInRow.add(countNZ);
        fileHeight++;

        while (fileData.hasNextLine()) {
          stringRow = fileData.nextLine().split("\\s+");

          if (stringRow.length != fileWidth) {
            throw new RuntimeException("Невозможно загрузить матрицу: строки имеют разную длину");
          }
          for (int i = 0; i < fileWidth; i++) {
            if (!stringRow[i].equals("0")) {
              fileElements.add(Double.parseDouble(stringRow[i]));
              fileColumnIndex.add(i);
              countNZ++;
            }
          }
          fileNumElInRow.add(countNZ);
          fileHeight++;
        }

        this.width = fileWidth;
        this.height = fileHeight;

        double[] fileMatrix = new double[fileElements.size()];
        for (int i = 0; i < fileElements.size(); i++) {
          fileMatrix[i] = fileElements.get(i);
        }
        this.elements = fileMatrix;

        int[] rowsSizes = new int[fileNumElInRow.size()];
        for (int i = 0; i < fileNumElInRow.size(); i++) {
          rowsSizes[i] = fileNumElInRow.get(i);
        }
        this.numElInRow = rowsSizes;

        int[] columnIndex = new int[fileColumnIndex.size()];
        for (int i = 0; i < fileColumnIndex.size(); i++) {
          columnIndex[i] = fileColumnIndex.get(i);
        }
        this.columnIndex = columnIndex;

        double[][] matrix = new double[this.height][this.width];
        for (int i = 0; i < this.height; i++) {
          Arrays.fill(matrix[i], 0);
        }

        for (int i = 0; i < this.numElInRow.length - 1; i++) {
          int startPoint = this.numElInRow[i], endPoint = this.numElInRow[i + 1];
          for (int j = startPoint; j < endPoint; j++){
            matrix[i][this.columnIndex[j]] = this.elements[j];
          }
        }
        this.matrix = matrix;

      } else {
        System.err.println("Невозможно загрузить матрицу: файл пуст");
      }
    } catch (FileNotFoundException e) {
      System.err.println("Невозможно загрузить матрицу: файл не найден");
    }
  }//билдер класса с помощью файла

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
  public Matrix transpose() {
    double[][] transMatrix = new double[this.width][this.height];
    for (int i = 0; i < this.width; i++) {
      Arrays.fill(transMatrix[i], 0);
    }

    for (int i = 0; i < this.numElInRow.length - 1; i++) {
      int startPoint = this.numElInRow[i], endPoint = this.numElInRow[i + 1];
      for (int j = startPoint; j < endPoint; j++){
        transMatrix[this.columnIndex[j]][i] = this.elements[j];
      }
    }
    return new SparseMatrix(transMatrix);
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
  }//распределение по методам

  private Matrix mul(DenseMatrix o) {
    int newHeight = this.height, newWidth = o.getWidth();
    double[][] newMatrix = new double[newHeight][newWidth];
    for (int i = 0; i < this.height; i++) {
      Arrays.fill(newMatrix[i], 0);
    }

    for (int i = 0; i < newWidth; i++) {
      for (int j = 0; j < this.numElInRow.length - 1; j++) {
        int startPoint = this.numElInRow[j], endPoint = this.numElInRow[j + 1];
        for (int k = startPoint; k < endPoint; k++){
          newMatrix[j][i] += this.elements[k]*o.matrix[this.columnIndex[k]][i];
        }
      }
    }
    return new SparseMatrix(newMatrix);
  }//перемножение sparse на dense

  private Matrix mul(SparseMatrix o) {
    int newHeight = this.height, newWidth = o.getWidth();
    SparseMatrix sm = (SparseMatrix)o.transpose();
    double[][] newMatrix = new double[newHeight][newWidth];
    for (int i = 0; i < this.height; i++) {
      Arrays.fill(newMatrix[i], 0);
    }

    for (int i = 0; i < this.numElInRow.length - 1; i++) {
      int startPointF = this.numElInRow[i], endPointF = this.numElInRow[i + 1];
      for (int j = 0; j < sm.numElInRow.length - 1; j++) {
        int startPointS = sm.numElInRow[j], endPointS = sm.numElInRow[j + 1];
        int currentPointF = startPointF, currentPointS = startPointS;

        while ((currentPointF < endPointF) && (currentPointS < endPointS)) {
          if (this.columnIndex[currentPointF] == sm.columnIndex[currentPointS]) {
            newMatrix[i][j] += this.elements[currentPointF] * sm.elements[currentPointS];
            currentPointF++;
            currentPointS++;
          } else if (this.columnIndex[currentPointF] < sm.columnIndex[currentPointS]) {
            currentPointF++;
          } else {
            currentPointS++;
          }
        }
      }
    }
    return new SparseMatrix(newMatrix);
  }//перемножение sparse на sparse

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

  private Matrix dmul(DenseMatrix o){
    final SparseMatrix matrixF = new SparseMatrix(this.matrix);
    final DenseMatrix matrixS = new DenseMatrix(o.matrix);
    int newHeight = this.height, newWidth = o.getWidth();
    final double[][] newMatrix = new double[newHeight][newWidth];
    for (int i = 0; i < this.height; i++) {
      Arrays.fill(newMatrix[i], 0);
    }

    class SDMulThreaded implements Runnable {
      final int subWidthF, subWidthS, subHeightF, subHeightS;

      public SDMulThreaded(int subWidthF, int subWidthS, int subHeightF, int subHeightS) {
        this.subWidthF = subWidthF;
        this.subWidthS = subWidthS;
        this.subHeightF = subHeightF;
        this.subHeightS = subHeightS;
      }

      @Override
      public void run(){
        for (int i = subWidthF; i < subWidthS; i++) {
          for (int j = subHeightF; j < subHeightS; j++) {
            int startPoint = matrixF.numElInRow[j], endPoint = matrixF.numElInRow[j + 1];
            for (int k = startPoint; k < endPoint; k++){
              newMatrix[j][i] += matrixF.elements[k]*matrixS.matrix[matrixF.columnIndex[k]][i];
            }
          }
        }
      }
    }

    int subHeight = newHeight / 2, subWidth = newWidth / 2;
    try {
      Thread threadFirst = new Thread(new SDMulThreaded(0, subWidth, 0, subHeight));
      Thread threadSecond = new Thread(new SDMulThreaded(subWidth, newWidth, 0, subHeight));
      Thread threadThird = new Thread(new SDMulThreaded(0, subWidth, subHeight, newHeight));
      Thread threadFourth = new Thread(new SDMulThreaded(subWidth, newWidth, subHeight, newHeight));

      threadFirst.start(); threadSecond.start(); threadThird.start(); threadFourth.start();
      threadFirst.join(); threadSecond.join(); threadThird.join(); threadFourth.join();

    } catch (Exception e){
      e.printStackTrace();
    }

    return new SparseMatrix(newMatrix);
  }

  private Matrix dmul(SparseMatrix o) {
    final SparseMatrix matrixF = new SparseMatrix(this.matrix);
    final SparseMatrix matrixS = (SparseMatrix)o.transpose();
    int newHeight = this.height, newWidth = o.getWidth();
    final double[][] newMatrix = new double[newHeight][newWidth];
    for (int i = 0; i < this.height; i++) {
      Arrays.fill(newMatrix[i], 0);
    }

    class SSMulThreaded implements Runnable {
      final int subHeightF, subHeightS, subWidthF, subWidthS;

      public SSMulThreaded(int subHeightF, int subHeightS, int subWidthF, int subWidthS) {
        this.subHeightF = subHeightF;
        this.subHeightS = subHeightS;
        this.subWidthF = subWidthF;
        this.subWidthS = subWidthS;
      }

      @Override
      public void run() {
        for (int i = subHeightF; i < subHeightS; i++) {
          int startPointF = matrixF.numElInRow[i], endPointF = matrixF.numElInRow[i + 1];
          for (int j = subWidthF; j < subWidthS; j++) {
            int startPointS = matrixS.numElInRow[j], endPointS = matrixS.numElInRow[j + 1];
            int currentPointF = startPointF, currentPointS = startPointS;

            while ((currentPointF < endPointF) && (currentPointS < endPointS)) {
              if (matrixF.columnIndex[currentPointF] == matrixS.columnIndex[currentPointS]) {
                newMatrix[i][j] += matrixF.elements[currentPointF] * matrixS.elements[currentPointS];
                currentPointF++;
                currentPointS++;
              } else if (matrixF.columnIndex[currentPointF] < matrixS.columnIndex[currentPointS]) {
                currentPointF++;
              } else {
                currentPointS++;
              }
            }
          }
        }
      }
    }

    int subHeight = newHeight / 2, subWidth = newWidth / 2;
    try {
      Thread threadFirst = new Thread(new SSMulThreaded(0, subHeight, 0, subWidth));
      Thread threadSecond = new Thread(new SSMulThreaded(0, subHeight, subWidth, newWidth));
      Thread threadThird = new Thread(new SSMulThreaded(subHeight, newHeight, 0, subWidth));
      Thread threadFourth = new Thread(new SSMulThreaded(subHeight, newHeight, subWidth, newWidth));

      threadFirst.start(); threadSecond.start(); threadThird.start(); threadFourth.start();
      threadFirst.join(); threadSecond.join(); threadThird.join(); threadFourth.join();

    } catch (Exception e){
      e.printStackTrace();
    }

    return new SparseMatrix(newMatrix);
  }

  @Override public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (o instanceof DenseMatrix) {
      return o.equals(this);
    }

    if (o instanceof SparseMatrix) {
      SparseMatrix sm = (SparseMatrix) o;

      if (this.height != sm.height || this.width != sm.width) {
        return false;
      }
      return (Arrays.equals(this.elements, sm.elements)) && (Arrays.equals(this.columnIndex, sm.columnIndex))
              && (Arrays.equals(this.numElInRow, sm.numElInRow));
    }

    return false;
  }
}