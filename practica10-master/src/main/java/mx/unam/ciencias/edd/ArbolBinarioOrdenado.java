package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        public Iterador()
        {
            pila = new Pila<Vertice>();
            pila = new Pila<Vertice>();
            Vertice aux = raiz;
            while(aux != null)
            {
              pila.mete(aux);
              aux = aux.izquierdo;
            }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
            Vertice v = pila.saca(), vv;
            if (v.hayDerecho())
            {
                vv = v.derecho;
                while (vv != null)
                {
                    pila.mete(vv);
                    vv = vv.izquierdo;
                }
            }
            return v.elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento)
    {
        if (elemento == null)
            throw new IllegalArgumentException();
        if (raiz == null)
        {
            raiz = nuevoVertice(elemento);
            ultimoAgregado = raiz;
            elementos++;
            return;
        }
        agrega(elemento,raiz);
    }

    private void agrega(T elemento, Vertice vertice)
    {
        if (elemento.compareTo(vertice.elemento) <= 0)
        {
            if (!vertice.hayIzquierdo())
            {
                vertice.izquierdo = nuevoVertice(elemento);
                vertice.izquierdo.padre = vertice;
                ultimoAgregado = vertice.izquierdo;
                elementos++;
            }
            else
                agrega(elemento,vertice.izquierdo);
        }
        else
        {
            if (!vertice.hayDerecho())
            {
                vertice.derecho = nuevoVertice(elemento);
                vertice.derecho.padre = vertice;
                ultimoAgregado = vertice.derecho;
                elementos++;
            }
            else
                agrega(elemento,vertice.derecho);
        }
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento)
    {
        Vertice v = vertice(busca(elemento));
        if (v == null)
            return;
        elementos--;
        if (v.izquierdo != null && v.derecho != null)
            v = intercambiaEliminable(v);
        eliminaVertice(v);
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        Vertice v = maximoEnSubarbol(vertice.izquierdo);
        vertice.elemento = v.elemento;
        return v;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice)
    {
        Vertice v = hijo(vertice);
        Vertice vv = vertice.padre;
        if (vv != null)
        {
            if (vv.izquierdo == vertice)
                vv.izquierdo = v;
            if (vv.derecho == vertice)
                vv.derecho = v;
        }
        else
            raiz = v;
        if (v != null)
            v.padre = vv;
    }

    private Vertice hijo(Vertice v) {
      if (v.izquierdo != null)
          return v.izquierdo;
      return v.derecho;
    }
        
    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
        return busca(raiz,elemento);
    }

    private VerticeArbolBinario<T> busca(Vertice v, T elemento)
    {
        return (v == null || elemento == null) ? null :
        (elemento.compareTo(v.get()) == 0) ? v :
        (elemento.compareTo(v.get()) < 0) ? busca(v.izquierdo, elemento)
                                                       : busca(v.derecho, elemento);
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    protected Vertice maximoEnSubarbol(Vertice vertice)
    {
        if (vertice.derecho == null)
            return vertice;
        return maximoEnSubarbol(vertice.derecho);
        
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        Vertice v = vertice(vertice);
        if (!v.hayIzquierdo())
            return;
        Vertice nvertice = v.padre;
        Vertice vv = v.izquierdo;
        Vertice vderecho = vv.derecho;
        v.padre = vv;
        vv.derecho = v;
        if (vderecho != null)
        vderecho.padre = v;
        v.izquierdo = vderecho;
        vv.padre = nvertice;
        if (nvertice == null)
            raiz = vv;
        else
            if (nvertice.izquierdo == v)
                nvertice.izquierdo = vv;
            else
                nvertice.derecho = vv;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice)
    {
        Vertice v = vertice(vertice);
        if (!v.hayDerecho())
            return;
        Vertice nvertice = v.padre;
        Vertice vv = v.derecho;
        Vertice vizquierdo = vv.izquierdo;
        v.padre = vv;
        vv.izquierdo = v;
        if (vizquierdo != null)
            vizquierdo.padre = v;
        v.derecho = vizquierdo;
        vv.padre = nvertice;
        if (nvertice == null)   
            raiz = vv;
        else
            if (nvertice.izquierdo == v)
                nvertice.izquierdo = vv;
            else
                nvertice.derecho = vv;
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPreOrder(raiz,accion);
    }

    private void dfsPreOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion)
    {
        if (vertice != null)
        {
            accion.actua(vertice);
            dfsPreOrder(vertice.izquierdo,accion);
            dfsPreOrder(vertice.derecho,accion);
        }
        else
            return;
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        dfsInOrder(raiz,accion);
    }

    private void dfsInOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion)
    {
        if (vertice != null)
        {
            dfsInOrder(vertice.izquierdo,accion);
            accion.actua(vertice); 
            dfsInOrder(vertice.derecho,accion);    
        }
        else
            return;
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPostOrder(raiz,accion);
    }

    private void dfsPostOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion)
    {
        if (vertice != null)
        {
            dfsPostOrder(vertice.izquierdo,accion);
            dfsPostOrder(vertice.derecho,accion);
            accion.actua(vertice);
        }
        else
            return;
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
