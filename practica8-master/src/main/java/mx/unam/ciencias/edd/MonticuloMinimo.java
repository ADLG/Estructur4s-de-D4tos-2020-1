package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return (indice < arbol.length) && (arbol[indice] != null);
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            if (indice >= arbol.length)
            throw new NoSuchElementException();
            return arbol[indice++];
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
            this.elemento = elemento;
            this.indice = -1;
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
            return this.indice;
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
            return this.elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
        return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        arbol = nuevoArreglo(100); /* 100 es arbitrario. */
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
        this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) 
    {
        int a = 0,indice;
        this.arbol = nuevoArreglo(n);
        elementos = n;
        for (T elemento : iterable)
        {
            arbol[a] = elemento;
            elemento.setIndice(a);
            a++;
        }
        indice = (n-1)/2;

        for (int i=indice; i>=0; i--)
            heapify_down(arbol[i]);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento)
    {
        if (elementos == arbol.length)
        {
            T [] arbolx2 = nuevoArreglo(elementos*2);
            for (int i=0; i<elementos; i++)
                arbolx2[i] = arbol[i];

            arbol = arbolx2;
        }
        arbol[elementos] = elemento;
        elemento.setIndice(elementos);
        elementos++;
        heapify_up(elemento);
    }

    private void intercambia(T a, T b)
    {
        int aux = b.getIndice();
        arbol[a.getIndice()] = b;
        arbol[b.getIndice()] = a;
        b.setIndice(a.getIndice());
        a.setIndice(aux);
    }

    private boolean indiceValido(int i)
    {
        if (i < 0 || i >= elementos)
            return false;
        else
            return true;
    }

    private void heapify_down(T elemento)
    {
        if (elemento == null)
           return;
        int izq = elemento.getIndice()*2 +1;
        int der = elemento.getIndice()*2 +2;
        if (!indiceValido(izq) && !indiceValido(der))
           return;
        int min = der;
        if (indiceValido(izq))
        {
           if (indiceValido(der))
            {
              if (arbol[izq].compareTo(arbol[der]) < 0)
              min = izq;
            }
            else
            min = izq;
        }
        if (elemento.compareTo(arbol[min]) > 0)
        {
           intercambia(elemento,arbol[min]);
           heapify_down(elemento);
        }
    }

    private void heapify_up(T elemento)
    {
        int padre = elemento.getIndice()-1;
        if (padre != -1)
            padre/=2;
        if (!indiceValido(padre) || arbol[padre].compareTo(elemento) < 0)
            return;
        intercambia(arbol[padre],elemento);
        heapify_up(elemento);
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override public T elimina()
    {
        if (esVacia())
        throw new IllegalStateException();
        T raiz = arbol[0];
        T ultimo = arbol[elementos-1];
        intercambia(raiz,ultimo);
        elementos--;
        arbol[elementos].setIndice(-1);
        arbol[elementos] = null;
        heapify_down(arbol[0]);
        return raiz;
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento)
    {
        int indice = elemento.getIndice();
        if(!indiceValido(indice))
            return;
        intercambia(arbol[indice],arbol[elementos-1]);
        elementos--;
        arbol[elementos] = null;
        elemento.setIndice(-1);
        reordena(arbol[indice]);
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento)
    {
        int indice = elemento.getIndice();
        if (!indiceValido(indice))
            return false;
        else
            return true;
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <code>true</code> si ya no hay elementos en el montículo,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia()
    {
        for (int i=0; i<elementos; i++)
            arbol[i] = null;
        elementos = 0;
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento)
    {
        if (elemento == null)
            return;
        int padre = elemento.getIndice()-1;
        if (padre != -1)
            padre/= 2;

        if (!indiceValido(padre) || arbol[padre].compareTo(elemento) <= 0)
            heapify_down(elemento);
        else
            heapify_up(elemento);
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
        return this.elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
        if (i < 0 || i >= elementos)
        throw new NoSuchElementException();
        return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
        String s = "";
        for (int i=0; i<arbol.length; i++)
            s += arbol[i] + ", ";
        return s;
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
            (MonticuloMinimo<T>)objeto;
        if (monticulo.getElementos() != getElementos())
            return false;
        for (int i=0; i<getElementos(); i++)
            if (!arbol[i].equals(monticulo.get(i)))
            return false;

        return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion)
    {
        Lista<Adaptador<T>> l1 = new Lista<Adaptador<T>>();
        for (T ec : coleccion)
            l1.agrega(new Adaptador<T>(ec));
        Lista<T> l2 = new Lista<T>();
        MonticuloMinimo<Adaptador<T>> monticulo = new MonticuloMinimo<Adaptador<T>>(l1);
        while (!monticulo.esVacia())
            l2.agrega(monticulo.elimina().elemento);
        return l2;
    }
}
