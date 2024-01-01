package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        public T elemento;
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nodo con un elemento. */
        public Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nuevo iterador. */
        public Iterador() {
            start();
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return this.siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override public T next() {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            this.anterior = this.siguiente;
            this.siguiente = this.siguiente.siguiente;
            return this.anterior.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override public boolean hasPrevious() {
        	return this.anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override public T previous() {
            if (!this.hasPrevious())
            {
                throw new NoSuchElementException();
            }
            this.siguiente = this.anterior;
            this.anterior = this.anterior.anterior;
            return this.siguiente.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override public void start() {
            this.anterior = null;
            this.siguiente = cabeza;
        }

        /* Mueve el iterador al final de la lista. */
        @Override public void end() {
            this.siguiente = null;
            this.anterior = rabo;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a {@link
     * #getElementos}.
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return this.longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a {@link
     * #getLongitud}.
     * @return el número elementos en la lista.
     */
    @Override public int getElementos() {
        return this.longitud;
    }

    /**
     * Nos dice si la lista es vacía.
     * @return <code>true</code> si la lista es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return this.cabeza == null;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
        	throw new IllegalArgumentException();
        Nodo nuevo = new Nodo(elemento);
        if (esVacia())
        {
        	this.cabeza = nuevo;
        	this.rabo = nuevo;
        }
        else
        {
        	this.rabo.siguiente = nuevo;
        	nuevo.anterior = this.rabo;
        	this.rabo = nuevo;
        }
        longitud ++;
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaFinal(T elemento) {
    	agrega(elemento);
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
     * el elemento a agregar será el primero y último.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void agregaInicio(T elemento) {
        if (elemento == null)
        	throw new IllegalArgumentException();
        Nodo nuevo = new Nodo(elemento);
        if (esVacia())
        {
        	this.cabeza = nuevo;
        	this.rabo = nuevo;
        }
        else
        {
        	this.cabeza.anterior = nuevo;
        	nuevo.siguiente = this.cabeza;
            this.cabeza = nuevo;
        }
        longitud ++;
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio
     * de la lista. Si el índice es mayor o igual que el número de elementos en
     * la lista, el elemento se agrega al fina de la misma. En otro caso,
     * después de mandar llamar el método, el elemento tendrá el índice que se
     * especifica en la lista.
     * @param i el índice dónde insertar el elemento. Si es menor que 0 el
     *          elemento se agrega al inicio de la lista, y si es mayor o igual
     *          que el número de elementos en la lista se agrega al final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    public void inserta(int i, T elemento) {
        if (elemento == null)
        	throw new IllegalArgumentException();
        if (this.longitud == 0)
        {
        	agregaFinal(elemento);
        	return;
        }
        else if (i >= this.longitud)
        {
        	agregaFinal(elemento);
        	return;
        }
        else if ( i <= 0)
        {		
        	agregaInicio(elemento);
        	return;
        }
        this.longitud ++;
        Nodo aux = this.cabeza;
        int j=0;
        while (aux != null && j < i) {
        	aux = aux.siguiente;
        	j++;
        }
        Nodo nuevo = new Nodo(elemento);
        nuevo.siguiente = aux;
        nuevo.anterior = aux.anterior;
        aux.anterior.siguiente = nuevo;
        aux.anterior = nuevo;
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Nodo elimina = BuscaElemento(elemento);
        if (elimina == null)
        	return;
        if (this.cabeza == this.rabo)
        {
            this.cabeza = null;
            this.rabo = null;
        }
        else if (this.cabeza == elimina)
        {
        	this.cabeza = this.cabeza.siguiente;
        	this.cabeza.anterior = null;
        }
        else if (this.rabo == elimina)
        {
        	this.rabo = this.rabo.anterior;
        	this.rabo.siguiente = null;
        }
        else
        {
        	elimina.anterior.siguiente = elimina.siguiente; 
            elimina.siguiente.anterior = elimina.anterior;
        }
        this.longitud -=1;
    }

    private Nodo BuscaElemento(T elemento)
    {
    	Nodo n = cabeza;
    	while (n != null)
    	{
    		if (n.elemento.equals(elemento))
                return n;
    		n = n.siguiente;
    	}
    	return null;
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero(){
        if (esVacia())
        {
            throw new NoSuchElementException();
        }
        T eliminap = cabeza.elemento;
        cabeza = cabeza.siguiente;
        if (longitud == 1)
        {
            rabo = null;
        }
        else
        {
            cabeza.anterior = null;
        }
        longitud --;
        return eliminap;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo()
    {
        if (esVacia())
        {
            throw new NoSuchElementException();
        }
        T eliminau = rabo.elemento;
        rabo = rabo.anterior;
        if (longitud == 1)
        {
            cabeza = null;
        }
        else
        {
            rabo.siguiente = null;
        }
        longitud --;
        return eliminau;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <code>true</code> si <code>elemento</code> está en la lista,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return BuscaElemento(elemento) != null;
    }

    /**
     * Regresa la reversa de la lista.
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
        Lista<T> lista = new Lista<T>();
        Nodo n = this.cabeza;
        while (n != null)
        {
            lista.agregaInicio(n.elemento);
            n = n.siguiente;
        }
        return lista;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        return copia(new Lista<T>(),cabeza);
    }

    private Lista<T> copia(Lista<T> copia, Nodo nodo)
    {
        if (nodo == null)
        {
            return copia;
        }
        copia.agregaFinal(nodo.elemento);
        return copia(copia, nodo.siguiente);
    }
    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override public void limpia() {
        while (longitud != 0)
        {
            eliminaUltimo();
        }
    }

    /**
     * Regresa el primer elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if (esVacia())
        {
            throw new NoSuchElementException();
        }
        return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if (esVacia())
        {
            throw new NoSuchElementException();
        }
        return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *         igual que el número de elementos en la lista.
     */
    public T get(int i) {
        if (i < 0 || i >= getLongitud())
        {
            throw new ExcepcionIndiceInvalido();
        }
        Nodo iesimo = this.cabeza;
        int cont = 0;
        while (cont != i)
        {
            iesimo = iesimo.siguiente;
            cont ++;
        }
        return iesimo.elemento;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento
     *         no está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        Nodo indice = cabeza;
        int cont = 0;
        while (indice != null)
        {
            if (indice.elemento.equals(elemento))
                {return cont;}
                cont += 1;
                indice = indice.siguiente;
        }
        return -1;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * @return una representación en cadena de la lista.
     */
    @Override public String toString() {
        Nodo repre = cabeza;
        String listastr = "[";
        while (repre != null)
        {
            listastr += repre.elemento;
            repre = repre.siguiente;
            if (repre != null)
            {
                listastr += ", ";
            }
        }
        listastr += "]";
        return listastr;
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la lista es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;
        Nodo o = cabeza;
        Nodo o1 = lista.cabeza;
        while (o != null && o1 != null)
        {
            if (!o.elemento.equals(o1.elemento))
            {
                return false;
            }
            o = o.siguiente;
            o1 = o1.siguiente;
        }
        return (o != null) == (o1 != null);
        // verificar que this != null
        // verificar si 2 listas son vacias
        // verificar si tienen la misma longitud
        // verificar que los elementos de las listas son iguales
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }
}