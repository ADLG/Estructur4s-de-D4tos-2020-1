package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
            color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        public String toString() {
            return (color == Color.ROJO) ? "R{"+String.valueOf(elemento)+"}":
            "N{"+String.valueOf(elemento)+"}";
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
                VerticeRojinegro vertice = (VerticeRojinegro)objeto;
            return this.equals(this, vertice);
        }

        private boolean equals(VerticeRojinegro v1, VerticeRojinegro v2)
        {
            if (v1 == null && v2 == null)
                return true;
            if ((v1 == null && v2 != null) || (v1 != null && v2 == null) || !v1.elemento.equals(v2.elemento) || v1.color != v2.color)
                return false;
            return equals(verticeRojinegro(v1.izquierdo), verticeRojinegro(v2.izquierdo)) && equals(verticeRojinegro(v1.derecho), verticeRojinegro(v2.derecho));
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    private VerticeRojinegro verticeRojinegro(VerticeArbolBinario<T> vertice)
    {
        VerticeRojinegro v = (VerticeRojinegro)vertice;
        return v;
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        if (vertice.getClass() != VerticeRojinegro.class)
        throw new ClassCastException();
        return verticeRojinegro(vertice).color; 
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro vrj = (VerticeRojinegro)super.getUltimoVerticeAgregado();
        vrj.color = Color.ROJO;
        rebalanceaAgrega(vrj);
    }

    private void rebalanceaAgrega(VerticeRojinegro vertice)
    {
        VerticeRojinegro padre,tio,abuelo,aux;
        // Caso 1
        if (!vertice.hayPadre())
        {
            vertice.color = Color.NEGRO;
            return;
        }

        // Caso 2
        padre = getPadre(vertice);
        if (esNegro(padre))
            return;

        // Caso 3
        abuelo = getAbuelo(vertice);
        tio = getTio(vertice);
        if (esRojo(tio))
        {
            tio.color = Color.NEGRO;
            padre.color = Color.NEGRO;
            abuelo.color = Color.ROJO;
            rebalanceaAgrega(abuelo);
        }
        else
        {

        // Caso 4
        aux = padre;
        if (abuelo.izquierdo == padre && padre.derecho == vertice)
        {
            super.giraIzquierda(padre);
            padre = vertice;
            vertice = aux;
        }
        if (abuelo.derecho == padre && padre.izquierdo == vertice)
        {
            super.giraDerecha(padre);
            padre = vertice;
            vertice = aux;
        }

        // Caso 5
        padre.color = Color.NEGRO;
        abuelo.color = Color.ROJO;
        if (vertice == padre.izquierdo)
            super.giraDerecha(abuelo);
        else
            super.giraIzquierda(abuelo);
        }
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento)
    {
        VerticeRojinegro vertice = buscaElemento(verticeRojinegro(super.raiz),elemento);
        if (vertice == null)
            return;

        VerticeRojinegro aux = null;
        if (vertice.hayIzquierdo())
        {
            aux = vertice;
            vertice = intercambiaVertice(verticeRojinegro(vertice.izquierdo));
            aux.elemento = vertice.elemento;
            aux = null;
        }

        if (!vertice.hayIzquierdo() && !vertice.hayDerecho())
        {
            vertice.izquierdo = nuevoVertice(null);
            aux = verticeRojinegro(vertice.izquierdo);
            aux.padre = vertice;
            aux.color = Color.NEGRO;
        }

        VerticeRojinegro hermano = null;
        if (vertice.hayIzquierdo())
            hermano = verticeRojinegro(vertice.izquierdo);
        else
            hermano = verticeRojinegro(vertice.derecho);

        desconecta(hermano,vertice);

        if (esRojo(hermano) || esRojo(vertice))
            hermano.color = Color.NEGRO;
        else
        {
            hermano.color = Color.NEGRO;
            rebalanceaElimina(hermano);
        }

        if (aux != null)
        {
            if (!aux.hayPadre())
            {
                super.raiz = null;
                ultimoAgregado = null;
                aux = null;
            }
            else if (aux.padre.derecho == aux)
                aux.padre.derecho = null;
            else
                aux.padre.izquierdo = null;
        }
        elementos--;
    }

    private VerticeRojinegro intercambiaVertice(VerticeRojinegro vertice)
    {
        if (vertice.hayDerecho())
            return intercambiaVertice(verticeRojinegro(vertice.derecho));
        else
            return vertice;
    }

    private void desconecta(VerticeRojinegro hijo, VerticeRojinegro vertice)
    {
        if (!vertice.hayPadre())
        {
            raiz = hijo;
            raiz.padre = null;
            return;
        }
        hijo.padre = vertice.padre;
        if (vertice == vertice.padre.izquierdo)
        {
            if (vertice.izquierdo == hijo)
                vertice.padre.izquierdo = vertice.izquierdo;
            else
                vertice.padre.izquierdo = vertice.derecho;
        }
        if (vertice == vertice.padre.derecho)
        {
            if (vertice.izquierdo == hijo)
                vertice.padre.derecho = vertice.izquierdo;
            else
                vertice.padre.derecho = vertice.derecho;
        }
    }

    private VerticeRojinegro buscaElemento(VerticeRojinegro vertice, T elemento)
    {
        if (vertice == null)
        return null;
        if (elemento.equals(vertice.elemento))
            return vertice;
        if (elemento.compareTo(vertice.elemento) < 0)
            return buscaElemento(verticeRojinegro(vertice.izquierdo), elemento);
        else
            return buscaElemento(verticeRojinegro(vertice.derecho), elemento);
    }

    private VerticeRojinegro getPadre(VerticeRojinegro vertice)
    {
        VerticeRojinegro padre = verticeRojinegro(vertice.padre);
        return padre;
    }

    private VerticeRojinegro getAbuelo(VerticeRojinegro vertice)
    {
        VerticeRojinegro padre = verticeRojinegro(vertice.padre);
        return verticeRojinegro(padre.padre);
    }

    private VerticeRojinegro getTio(VerticeRojinegro vertice)
    {
        VerticeRojinegro abuelo = getAbuelo(vertice);
        if (vertice.padre == abuelo.izquierdo)
            return verticeRojinegro(abuelo.derecho);
        else
            return verticeRojinegro(abuelo.izquierdo);
    }

    private VerticeRojinegro getHermano(VerticeRojinegro vertice)
    {
        VerticeRojinegro padre = verticeRojinegro(vertice.padre);
        if (vertice == padre.izquierdo)
            return verticeRojinegro(padre.derecho);
        else
            return verticeRojinegro(padre.izquierdo);
    }

    private boolean esNegro(VerticeRojinegro vertice)
    {
        return(vertice == null || vertice.color == Color.NEGRO);
    }

    private boolean esRojo(VerticeRojinegro vertice)
    {
        return(vertice != null && vertice.color == Color.ROJO);
    }

    private void rebalanceaElimina(VerticeRojinegro vertice)
    {
        VerticeRojinegro hermano,padre,hijoIzquierdo,hijoDerecho;
        /* Caso 1
            v tiene padre null
        */
            if (!vertice.hayPadre())
            {
                vertice.color = Color.NEGRO;
                raiz = vertice;
                return;
            }
            padre = getPadre(vertice);
            hermano = getHermano(vertice);

        /* Caso 2
            vertice h es rojo y por lo tanto p es negro
        */
            if (esRojo(hermano) && esNegro(padre))
            {
                padre.color = Color.ROJO;
                hermano.color = Color.NEGRO;
                if (vertice == padre.izquierdo)
                    super.giraIzquierda(padre);
                else
                    super.giraDerecha(padre);
                hermano = getHermano(vertice);
                padre = getPadre(vertice);
            }
            hijoIzquierdo = verticeRojinegro(hermano.izquierdo);
            hijoDerecho = verticeRojinegro(hermano.derecho);

        /* Caso 3
            los vertices p,h,hi y hd son negros
        */
            if (esNegro(padre) && esNegro(hermano) && esNegro(hijoIzquierdo) && esNegro(hijoDerecho))
            {
                hermano.color = Color.ROJO;
                rebalanceaElimina(padre);
                return;
            }

        /* Caso 4
            vertices h,hi y hd son negros y p es rojo
        */
            if (esNegro(hermano) && esNegro(hijoIzquierdo) && esNegro(hijoDerecho) && esRojo(padre))
            {
                hermano.color = Color.ROJO;
                padre.color = Color.NEGRO;
                return;
            }

        /* Caso 5
            v es izquierdo, hi es rojo y hd es negro;
            o el vértice v es derecho,
            hi es negro y hd es rojo
        */
            if (vertice == padre.izquierdo && esRojo(hijoIzquierdo) && esNegro(hijoDerecho) && esNegro(hermano))
            {
                hermano.color = Color.ROJO;
                hijoIzquierdo.color = Color.NEGRO;
                super.giraDerecha(hermano);
            }
            else if (vertice == padre.derecho && esNegro(hijoIzquierdo) && esRojo(hijoDerecho) && esNegro(hermano))
            {
                hermano.color = Color.ROJO;
                hijoDerecho.color = Color.NEGRO;
                super.giraIzquierda(hermano);
            }
            hermano = getHermano(vertice);
            hijoIzquierdo = verticeRojinegro(hermano.izquierdo);
            hijoDerecho = verticeRojinegro(hermano.derecho);

        /* Caso 6
            v es izquierdo y hd es rojo o v es derecho y hi es rojo
        */
            hermano.color = padre.color;
            padre.color = Color.NEGRO;
            if (vertice == padre.izquierdo)
            {
                hijoDerecho.color = Color.NEGRO;
                super.giraIzquierda(padre);
            }
            else
            {
                hijoIzquierdo.color = Color.NEGRO;
                super.giraDerecha(padre);
            }
        }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }
}
