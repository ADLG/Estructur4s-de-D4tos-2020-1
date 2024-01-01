package mx.unam.ciencias.edd;

import java.util.Comparator;

/**
 * Clase para ordenar y buscar arreglos genéricos.
 */
public class Arreglos {

    /* Constructor privado para evitar instanciación. */
    private Arreglos() {}

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */

    public static <T> void
    quickSort(T[] arreglo, Comparator<T> comparador)
    {
        aquickSorta(arreglo, comparador);
    }

    private static <T> void
    aquickSorta(T[] arreglo, Comparator<T> comparador)
    {
        Lista<Integer> lista = new Lista<Integer>();
        lista.agregaFinal(0);
        lista.agregaFinal(arreglo.length-1);
        while (lista.getLongitud() > 0)
        {
            int a = lista.eliminaUltimo();
            int b = lista.eliminaUltimo();
            if (a - b < 1)
                continue;
            int i = b + 1;
            int j = a;
            while (i < j)
                {
                if (comparador.compare(arreglo[i],arreglo[b]) > 0 && comparador.compare(arreglo[j],arreglo[b]) <= 0)
                    intercambiapalquick(arreglo,i++,j--);   
                else if (comparador.compare(arreglo[i],arreglo[b]) <= 0)
                    i++;
                else
                    j--;
                }
                if (comparador.compare(arreglo[i],arreglo[b]) > 0)
                    i--;
                intercambiapalquick(arreglo,b,i);
                lista.agregaFinal(b);
                lista.agregaFinal(i-1);
                lista.agregaFinal(i+1);
                lista.agregaFinal(a);
        }
    }
 
    private static <T> void intercambiapalquick(T[] arreglo, int i, int j)
    {
        if (i == j)
            return;
        T aux = arreglo[j];
        arreglo[j] = arreglo[i];
        arreglo[i] = aux;
    }

    /**
     * Ordena el arreglo recibido usando QickSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    quickSort(T[] arreglo) {
        quickSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo a ordenar.
     * @param comparador el comparador para ordernar el arreglo.
     */
    public static <T> void
    selectionSort(T[] arreglo, Comparator<T> comparador)
    {
        int max = arreglo.length;
        for(int i = 0; i<max; i++)
        {
            int min = i;
            for (int j = i+1; j < max; j++)
            {
                if (comparador.compare(arreglo[j], arreglo[min]) < 0)
                min = j;
            }
                intercambiapalselec(arreglo, i, min);
        }
    }

    private static <T> void intercambiapalselec (T [] arreglo, int i, int j)
    {
        T aux1 = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = aux1;
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void
    selectionSort(T[] arreglo) {
        selectionSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo el arreglo dónde buscar.
     * @param elemento el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T> int
    busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador)
    {
        int i = 0;
        int j = arreglo.length-1;
        while(i <= j)
        {
            int mitad = j+(i-j)/2;
            if(comparador.compare(arreglo[mitad], elemento) == 0)
                return mitad;
            else if(comparador.compare(arreglo[mitad], elemento) > 0)
            {
                if(comparador.compare(arreglo[i], elemento) == 0)
                    return i;
                j = mitad - 1;
                i = i + 1;
            }
            else
            {
                if (comparador.compare(arreglo[j], elemento) == 0)
                    return j;
                j = j - 1;
                i = i + 1;
            }
        }
        return -1;
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice
     * del elemento en el arreglo, o -1 si no se encuentra.
     * @param <T> tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     * @param elemento el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int
    busquedaBinaria(T[] arreglo, T elemento) {
        return busquedaBinaria(arreglo, elemento, (a, b) -> a.compareTo(b));
    }
}
