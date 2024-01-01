package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        public Iterador() {
        cola = new Cola<Vertice>();
        if (raiz != null)
        cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
            Vertice vv = cola.saca();
            if (vv.izquierdo != null)
                cola.mete(vv.izquierdo);
            if (vv.derecho != null)
                cola.mete(vv.derecho);
            return vv.get();
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento)
    {
        if (elemento == null)
            throw new IllegalArgumentException();
        Vertice v = new Vertice(elemento);
        if (raiz == null)
        {
            raiz = v;
            elementos++;
            return;
        }
        Vertice vv = raiz;
        Cola<Vertice> cola = new Cola<Vertice>();
        cola.mete(vv);
        while (!cola.esVacia())
        {
            vv = cola.saca();
            if (!vv.hayIzquierdo() || !vv.hayDerecho())
            {
                if (!vv.hayIzquierdo())
                {
                    vv.izquierdo = v;
                    v.padre = vv;
                    elementos++;
                    return;
                }
                if (!vv.hayDerecho())
                {
                    vv.derecho = v;
                    v.padre = vv;
                    elementos++;
                    return;
                }
            }
            cola.mete(vv.izquierdo);
            cola.mete(vv.derecho);
        }
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento)
    {
        Vertice vv = (Vertice)busca(elemento);
        if (vv == null)
            return;
        elementos--;
        if (elementos == 0)
            raiz = null;
        Cola<Vertice> vertices = new Cola<Vertice>();
        if (raiz == null)
            return;
        vertices.mete(raiz);
        Vertice ultimo = null;
        while (!vertices.esVacia())
        {
            ultimo = vertices.saca();
            if (ultimo.hayIzquierdo())
                vertices.mete(ultimo.izquierdo);
            if (ultimo.hayDerecho())
                vertices.mete(ultimo.derecho);
        }
            vv.elemento = ultimo.elemento;
            Vertice padre = ultimo.padre;
            if (padre.izquierdo == ultimo)
                padre.izquierdo = null;
            else
                padre.derecho = null;
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
        if (raiz == null)
            return -1;
        else
            return raiz.altura();
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
        Cola<Vertice> cola = new Cola<Vertice>();
        cola.mete(raiz);
        while (!cola.esVacia())
        {
            Vertice v = cola.saca();
            accion.actua(v);
            if (v.hayIzquierdo())
                cola.mete(v.izquierdo);
            if (v.hayDerecho())
                cola.mete(v.derecho);
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
