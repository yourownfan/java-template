package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  // lev-левый край массива , prav-правый край массива , cen - середина
  static void sort(int array[], int lev, int prav) {
    if (lev < prav) {
      int cen = (lev + prav) / 2;
// сортируем левую половину массива
      sort(array, lev, cen);
// сортируем правую половину массива
      sort(array, cen + 1, prav);
// применяем слияние к отсортированным половинкам
      merge(array, lev, cen, prav);
    }
  }
  static void merge(int arr[], int lev, int cen, int prav) {
// вычисляем размеры половинок и создаём для них новые массивы
    int n1 = cen - lev + 1;
    int n2 = prav - cen;
    int arr1[] = new int[n1];
    int arr2[] = new int[n2];
// заполняем временные масивы
    for (int i = 0; i < n1; ++i)
      arr1[i] = arr[lev + i];
    for (int j = 0; j < n2; ++j)
      arr2[j] = arr[cen + 1 + j];
// выполняем слияние массивов
    int i = 0, j = 0;
    int k = lev;
    while (i < n1 && j < n2) {
      if (arr1[i] <= arr2[j]) {
        arr[k] = arr1[i];
        i++;
      } else {
        arr[k] = arr2[j];
        j++;
      }
      k++;
    }
// если в arr1 остались элементы, копируем их
    while (i < n1) {
      arr[k] = arr1[i];
      i++;
      k++;
    }
// для arr2
    while (j < n2) {
      arr[k] = arr2[j];
      j++;
      k++;
    }
  }
  static void sort (int arr[]) {
    sort(arr, 0, arr.length-1);
  }
  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}