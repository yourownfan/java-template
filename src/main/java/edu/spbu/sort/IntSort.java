package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */

  // lev-левый край массива , prav-правый край массива , cen - середина
  public class IntSort {

    public static void sort(int[] array, int lev, int prav) {
      if (array.length == 0)
        return;//завершить выполнение если длина массива равна 0

      if (lev >= prav)
        return;//завершить выполнение если уже нечего делить

      // выбираем опорный элемент
      int cen = lev + (prav - lev) / 2;
      int opora = array[cen];

      // разделяем на подмассивы, который больше и меньше опорного элемента
      int i = lev, j = prav;
      while (i <= j) {
        while (array[i] < opora) {
          i++;
        }

        while (array[j] > opora) {
          j--;
        }

        if (i <= j) {//меняем местами
          int temp = array[i];
          array[i] = array[j];
          array[j] = temp;
          i++;
          j--;
        }
      }

      // вызываем рекурсию для сортировки левой и правой части
      if (lev < j)
        sort(array, lev, j);

      if (prav > i)
        sort(array, i, prav);
    }
  static void sort (int arr[]) {
    sort(arr, 0, arr.length - 1);
  }
  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}