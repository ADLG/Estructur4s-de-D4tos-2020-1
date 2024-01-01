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
            this.iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return this.iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return this.iterador.next().get();
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>,
                          ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* La lista de vecinos del vértice. */
        public Lista<Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento)
        {
            this.elemento = elemento;
            this.color = Color.NINGUNO;
            this.vecinos = new Lista<Vecino>();
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
            return this.vecinos;
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return this.indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
            if (distancia > vertice.distancia)
                return 1;
            else if (distancia < vertice.distancia)
                return -1;
            return 0;
        }       
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
            return this.vecino.elemento;
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return this.vecino.getGrado();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return this.vecino.getColor();
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return this.vecino.vecinos;
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica.Vertice v, Grafica.Vecino a);
    }

    /* Vértices. */
    private Lista<Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        this.vertices = new Lista<>();
        this.aristas = 0;
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return this.vertices.getLongitud();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return this.aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null || this.contiene(elemento))
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
    public void conecta(T a, T b) {
        if (!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        if (a.equals(b) || sonVecinos(a,b))
        throw new IllegalArgumentException();

        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        va.vecinos.agrega(new Vecino(vb,1));
        vb.vecinos.agrega(new Vecino(va,1));
        aristas++;
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
        if (!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        if (a.equals(b) || sonVecinos(a,b) || peso < 0)
        throw new IllegalArgumentException();

        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        va.vecinos.agrega(new Vecino(vb,peso));
        vb.vecinos.agrega(new Vecino(va,peso));
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

        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        Vecino vea = null, veb = null;
        for (Vecino vecino : va.vecinos)
        {
            if (vecino.vecino.equals(vb))
                vea = vecino;
        }
        va.vecinos.elimina(vea);
        vb.vecinos.elimina(veb);
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for (Vertice vertice : vertices)
        {
            if (vertice.get().equals(elemento))
            return true;
        }
        return false;
    }

    /**
     * Método auxiliar para búsqueda de vértices.
     * @param e elemento a buscar
     * @return el vértice con el elemento si lo encuentra.
     */
    private Vertice busca(T elemento)
    {
        for (Vertice vertice : vertices)
        {
            if (vertice.get().equals(elemento))
            return vertice;
        }
        return null;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento)
    {
        if (!contiene(elemento))
        throw new NoSuchElementException();
        Vertice v = (Vertice) vertice(elemento);
        for (Vertice vertice : vertices)
        {
            for (Vecino vecino : vertice.vecinos)
            { 
                if (vecino.vecino.equals(v))
                {
                    vertice.vecinos.elimina(vecino);
                    aristas--;
                }
            }
        }
        vertices.elimina(v);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b)
    {
        if (!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        Vertice va = (Vertice) vertice(a);
        Vertice vb = (Vertice) vertice(b);
        for (Vecino vecino : va.vecinos)
        {
          if (vecino.vecino.equals(vb))
            return true;
        }
        return false;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b)
    {
        if (!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        if (!sonVecinos(a,b))
        throw new IllegalArgumentException();
        Vertice c = (Vertice) vertice(a);
        Vertice d = (Vertice) vertice(b);
        for (Vecino vecino : c.vecinos)
        {
            if (vecino.vecino.equals(d))
                return vecino.peso;
        }
        return -1;
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        if (!contiene(a) || !contiene(b))
        throw new NoSuchElementException();
        if (a.equals(b) || !sonVecinos(a,b) || peso <= 0)
        throw new IllegalArgumentException();
        Vertice c = busca(a);
        Vertice d = busca(b);
        for (Vecino vecinoa : c.vecinos)
        {
            if (vecinoa.vecino.equals(d))
                vecinoa.peso = peso;
        }
        for (Vecino vecinob : d.vecinos)
        {
            if (vecinob.vecino.equals(c))
                vecinob.peso = peso;
        }
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
        if (vertice == null || (vertice.getClass() != Vertice.class && vertice.getClass() != Vecino.class))
        throw new IllegalArgumentException();
        if (vertice.getClass() == Vertice.class)
        {
            Vertice v = (Vertice)vertice;
            v.color = color;
        }
        if (vertice.getClass() == Vecino.class)
        {
            Vecino v = (Vecino)vertice;
            v.vecino.color = color;
        }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa()
    {
        for (Vertice vertice : vertices)
        vertice.color = Color.ROJO;
        Cola<Vertice> cola = new Cola<Vertice>();
        cola.mete(vertices.getPrimero());
        vertices.getPrimero().color = Color.NEGRO;
        while (!cola.esVacia())
        {
            Vertice v = cola.saca();
            for (Vecino vecino : v.vecinos)
            {
                if (vecino.vecino.color == Color.ROJO)
                {
                    vecino.vecino.color = Color.NEGRO;
                    cola.mete(vecino.vecino);
                }
            }
        }
        for (Vertice vertice : vertices)
        {
            if (vertice.color != Color.NEGRO)
                return false;
        }
            return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice vertice : vertices)
            accion.actua(vertice);
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
        recorrido(elemento, accion, new Cola<Grafica<T>.Vertice>());
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
        recorrido(elemento, accion, new Pila<Grafica<T>.Vertice>());
    }

    private void recorrido(T elemento, AccionVerticeGrafica<T> accion, MeteSaca<Grafica<T>.Vertice> metesaca)
    {
        if (!contiene(elemento))
        throw new NoSuchElementException();
        Vertice v = (Vertice) vertice(elemento);
        metesaca.mete(v);
        while (!metesaca.esVacia())
        {
            Vertice vv = metesaca.saca();
            setColor(vv,Color.ROJO);
            accion.actua(vv);
            for (Vecino vecino : vv.vecinos)
            {
                if (vecino.vecino.color != Color.ROJO)
                {
                    setColor(vecino,Color.ROJO);
                    metesaca.mete(vecino.vecino);
                }
            }
        }
        this.paraCadaVertice(vertice -> this.setColor(vertice,Color.NINGUNO));
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return this.vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        this.vertices.limpia();
        this.aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString()
    {
        Lista<T> l = new Lista<T>();
        for (Vertice vertice : vertices)
           vertice.color = Color.ROJO;
        String s = "{";
        String a = "{";
        for (Vertice vertice : vertices)
        {
            s += vertice.elemento + ", ";
            for (Vecino vecino : vertice.vecinos)
            {
                if (vecino.getColor() == Color.ROJO)
                    a += "(" + vertice.get() + ", " + vecino.get() + "), ";
                vertice.color = Color.NEGRO;
            }
            l.agrega(vertice.elemento);
        }

        for (Vertice vertice : vertices)
           vertice.color = Color.NINGUNO;
        return s + "}, " + a + "}";
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
        if (getElementos() != grafica.getElementos() || (aristas != grafica.aristas))
          return false;
        for (Vertice vertice : vertices)
        {
            vertice.color = Color.ROJO;
            if (!grafica.contiene(vertice.elemento))
            return false;
        }
        for (Vertice vertice : vertices)
        {
            for (Vecino vecino : vertice.vecinos)
            {
                if (vecino.getColor() == Color.ROJO)
                {
                    if (!grafica.sonVecinos(vecino.get(),vertice.elemento))
                        return false;
                }
            }
            vertice.color = Color.NEGRO;
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

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino)
    {
        if (!contiene(origen) || !contiene(destino))
        throw new NoSuchElementException();
        Lista<VerticeGrafica<T>> l = new Lista<VerticeGrafica<T>>();
        Cola<Vertice> cola = new Cola<Vertice>();
        Vertice a = busca(origen);
        Vertice b = busca(destino);
        if (origen.equals(destino))
        {
            l.agrega(a);
            return l;
        }
        for (Vertice vertice : vertices)
            vertice.distancia = -1;
            a.distancia = 0;
            cola.mete(a);
            while (!cola.esVacia())
            {
                a = cola.saca();
                for (Vecino vecino : a.vecinos)
                {
                    if (vecino.vecino.distancia == -1)
                    {
                        vecino.vecino.distancia = a.distancia +1;
                        cola.mete(vecino.vecino);
                    }
                }
            }
            if (b.distancia == -1)
                return l;
            l.agrega(b);
                while (!a.elemento.equals(origen)) 
                    for (Vecino vecino : a.vecinos)
                    {
                        if (a.distancia == vecino.vecino.distancia +1)
                        {
                            l.agrega(vecino.vecino);
                            a = vecino.vecino;
                        }
                    }
        return l.reversa();
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino)
    {
        if (!contiene(origen) || !contiene(destino))
        throw new NoSuchElementException();
        Lista<VerticeGrafica<T>> l = new Lista<VerticeGrafica<T>>();
        MonticuloMinimo<Vertice> monticulomin = new MonticuloMinimo<Vertice>(vertices);
        Vertice a = (Vertice) vertice(origen);
        Vertice b = (Vertice) vertice(destino);
        for (Vertice vertice : vertices)
        {
            vertice.distancia = Double.MAX_VALUE;
        }
        a.distancia = 0;
        while (!monticulomin.esVacia())
        {
            Vertice ve = monticulomin.elimina();
            for (Vecino vecino : ve.vecinos)
            {
                if (vecino.vecino.distancia > (ve.distancia + vecino.peso))
                    vecino.vecino.distancia = ve.distancia + vecino.peso;
                    monticulomin.reordena(vecino.vecino);
            }
        }
        if (b.distancia == Double.MAX_VALUE)
            return l;
        l.agrega(b);
        while (!b.elemento.equals(origen))
        {
            for (Vecino vecino : b.vecinos)
            {
                if (b.distancia == vecino.vecino.distancia + vecino.peso)
                {
                    l.agrega(vecino.vecino);
                    b = vecino.vecino;
                }
            }
        }
        return l.reversa();
    }
}
