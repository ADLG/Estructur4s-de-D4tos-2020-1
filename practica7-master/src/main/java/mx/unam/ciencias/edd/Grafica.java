package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().get();
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La lista de vecinos del vértice. */
        public Lista<Vertice> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento)
        {
            this.elemento = elemento;
            this.color = Color.NINGUNO;
            this.vecinos = new Lista<Vertice>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return this.elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return this.vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return this.color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }
    }

    /* Vértices. */
    private Lista<Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        this.vertices = new Lista<Vertice>();
        this.aristas = 0;
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento)
    {
        if (elemento == null || contiene(elemento))
        throw new IllegalArgumentException();
        vertices.agrega(new Vertice(elemento));
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b)
    {
        if (!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        if (a.equals(b) || sonVecinos(a,b))
        throw new IllegalArgumentException();

        Vertice va = busca(a);
        Vertice vb = busca(b);
        va.vecinos.agrega(vb);
        vb.vecinos.agrega(va);
        aristas++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        if (!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        if (!sonVecinos(a,b))
        throw new IllegalArgumentException();

        Vertice va = busca(a);
        Vertice vb = busca(b);
        va.vecinos.elimina(vb);
        vb.vecinos.elimina(va);
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        if (busca(elemento) != null)
            return true;
        else
            return false;
    }

    /**
     *Método auxiliar para búsqueda de vértices.
     *@param e elemento a buscar
     *@return el vértice con el elemento si lo encuentra.
     */
    private Vertice busca(T elemento)
    {
      for (Vertice v : vertices)
        if (v.get().equals(elemento))
          return v;
        return null;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        if (busca(elemento) == null)
        throw new NoSuchElementException();
        Vertice eliminarAux = busca(elemento);
        for (Vertice v : eliminarAux.vecinos)
        {
            v.vecinos.elimina(eliminarAux);
            aristas--;
        }
        vertices.elimina(eliminarAux);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        if(!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        Vertice va = busca(a);
        Vertice vb = busca(b);
        for(Vertice v : va.vecinos)
          if(v == vb)
            return true;
          return false;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        if(!contiene(elemento))
        throw new NoSuchElementException();
        return busca(elemento);
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color)
    {
        if (vertice == null || vertice.getClass() != Vertice.class)
        throw new IllegalArgumentException("Vértice inválido");
        Vertice v = (Vertice)vertice;
        if(!vertices.contiene(v))
        throw new IllegalArgumentException();
        v.color = color;
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa()
    {
        if (getElementos() == 0)
            return true;
        Vertice v = vertices.getPrimero();
        Cola<Vertice> vert = new Cola<Vertice>();
        paraCadaVertice((u) -> setColor(u, Color.NINGUNO));
        v.color = Color.NEGRO;
        vert.mete(v);

        while (!vert.esVacia())
        {
            Vertice aux = vert.saca();
            for (Vertice n: aux.vecinos)
            {
                if (n.color == Color.NINGUNO)
                {
                    vert.mete(n);
                    n.color = Color.NEGRO;
                }
            }
        }

        for (Vertice w: vertices)
        {
            if (w.color.equals(Color.NINGUNO))
            {
                return false;
            }
        }

        paraCadaVertice((u) -> setColor(u, Color.NINGUNO));
        return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice v : vertices)
            accion.actua(v);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion)
    {
        if (!contiene(elemento))
        throw new NoSuchElementException();
        Cola<Vertice> cola = new Cola<>();
        for(Vertice v : vertices)
          v.color = Color.ROJO;
        Vertice w = busca(elemento);
        w.color = Color.NEGRO;
        cola.mete(w);
        while (!cola.esVacia())
        {
          Vertice u = cola.saca();
          accion.actua(u);
          for (Vertice v : u.vecinos)
          {
            if (v.color == Color.ROJO)
            {
              v.color = Color.NEGRO;
              cola.mete(v);
            }
          }
        }

        for (Vertice v : vertices)
          v.color = Color.NINGUNO;
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion)
    {
        if (!contiene(elemento))
        throw new NoSuchElementException();
        for(Vertice v : vertices)
          v.color = Color.ROJO;
        Pila<Vertice> pila = new Pila<>();
        Vertice w = busca(elemento);
        w.color = Color.NEGRO;
        pila.mete(w);
        while (!pila.esVacia())
        {
          Vertice s = pila.saca();
          accion.actua(s);
          for (Vertice v : s.vecinos)
          {
            if (v.color == Color.ROJO)
            {
              v.color = Color.NEGRO;
              pila.mete(v);
            }
          }
        }
        for(Vertice v : vertices)
          v.color = Color.NINGUNO;
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices.limpia();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
        Lista<T> list = new Lista<>();
        for (Vertice v : vertices)
           v.color = Color.ROJO;
        String s = "{";
        String e = "{";
        for (Vertice v : vertices)
        {
           s += v.get() + ", ";
            for (Vertice vv : v.vecinos)
            {
                if (vv.color == Color.ROJO)
                    e += "(" + v.get() + ", " + vv.get() + "), ";
                v.color = Color.NEGRO;
            }
            list.agrega(v.elemento);
        }
        for (Vertice u : vertices)
           u.color = Color.NINGUNO;
        return s + "}, " + e + "}";
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
        if (getElementos() != grafica.getElementos())
          return false;
        if (aristas != grafica.aristas)
          return false;
        for (Vertice v : vertices)
        {
          v.color = Color.ROJO;
          if (!grafica.contiene(v.elemento))
            return false;
        }
        for (Vertice v : vertices)
        {
          for (Vertice vg : v.vecinos)
          {
            if (v.color == Color.ROJO)
            {
              if (!grafica.sonVecinos(v.elemento, vg.elemento))
                return false;
            }
          }
          v.color = Color.NEGRO;
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
