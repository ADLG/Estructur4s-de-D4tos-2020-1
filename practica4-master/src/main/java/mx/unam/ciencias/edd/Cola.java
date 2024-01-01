package mx.unam.ciencias.edd;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString()
    {
        String s = "";
        if (this.esVacia())
            return s;
        Nodo aux = this.cabeza;
        while (aux != null)
        {
            s += aux.elemento + ",";
            aux = aux.siguiente;
        }
        return s;
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento)
    {
        if (elemento == null)
        throw new IllegalArgumentException();
        Nodo nuevo = new Nodo(elemento);
        if (this.esVacia())
        {
            this.cabeza = nuevo;
        }
        else
        {
            this.rabo.siguiente = nuevo;
        }
        this.rabo = nuevo;
    }
}

